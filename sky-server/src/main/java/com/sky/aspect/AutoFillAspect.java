package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Slf4j
@Component
public class AutoFillAspect {
    @Pointcut("@annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){};

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill=signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType=autoFill.value();

        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }

        Object entity=args[0];

        if(operationType==OperationType.INSERT){
            try{
                Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

                Method setCreateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                Method setCreateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);

                setUpdateTime.invoke(entity, LocalDateTime.now());
                setUpdateUser.invoke(entity, BaseContext.getCurrentId());
                setCreateTime.invoke(entity, LocalDateTime.now());
                setCreateUser.invoke(entity, BaseContext.getCurrentId());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(operationType==OperationType.UPDATE){
            try{
                Method setUpdateUser=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                Method setUpdateTime=entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

                setUpdateTime.invoke(entity, LocalDateTime.now());
                setUpdateUser.invoke(entity, BaseContext.getCurrentId());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
