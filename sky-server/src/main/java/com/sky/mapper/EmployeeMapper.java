package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    @Select("select * from employee where id=#{id}")
    Employee getById(Long id);

    @AutoFill(value=OperationType.INSERT)
    void insert(Employee employee);

    List<Employee> getByName(String name);

    @AutoFill(value= OperationType.UPDATE)
    void update(Employee employee);


}
