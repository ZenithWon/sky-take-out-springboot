package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Slf4j
@Api("User api")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation("User login from wechat")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        User user=userService.wexinLogin(userLoginDTO);

        Map<String,Object>claims =new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());

        String userToken=JwtUtil.createJWT(jwtProperties.getUserSecretKey(),
                                            jwtProperties.getUserTtl(),
                                            claims);

        UserLoginVO userLoginVO=UserLoginVO.builder()
                            .id(user.getId())
                            .openid(user.getOpenid())
                            .token(userToken)
                            .build();
        return Result.success(userLoginVO);
    }
}