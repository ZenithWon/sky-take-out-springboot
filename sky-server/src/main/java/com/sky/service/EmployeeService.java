package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void save(EmployeeDTO employeeDTO);


    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    void permission(Integer status,Long id);

    void editPassword(PasswordEditDTO passwordEditDTO);

    Employee getEmployee(Long id);

    void updateEmployee(EmployeeDTO employeeDTO);
}
