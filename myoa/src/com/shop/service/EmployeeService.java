package com.shop.service;

import java.util.List;

import com.shop.pojo.Employee;

public interface EmployeeService {

	Employee isLogin(String username);

	Employee findEmployeeManagerByManagerId(long managerId);

	List<Employee> findEmployeeList();

	void saveEmployee(Employee employee);

	void deleteEmployee(Long id);

	Employee findEmployee(Long id);

	void updateEmployee(Employee employee);

}
