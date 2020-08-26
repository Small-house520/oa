package com.shop.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.mapper.EmployeeMapper;
import com.shop.pojo.Employee;
import com.shop.pojo.EmployeeExample;
import com.shop.pojo.EmployeeExample.Criteria;
import com.shop.service.EmployeeService;

@Service(value = "EmployeeService")
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	private EmployeeMapper employeeMapper;

	// 根据员工账号查询员工
	@Override
	public Employee isLogin(String username) {
		EmployeeExample example = new EmployeeExample();
		Criteria criteria = example.createCriteria();
		criteria.andNameEqualTo(username);

		List<Employee> list = this.employeeMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	// 根据上级id查询上级
	@Override
	public Employee findEmployeeManagerByManagerId(long managerId) {

		Employee employee = this.employeeMapper.selectByPrimaryKey(managerId);
		return employee;
	}

	// 查询员工信息
	@Override
	public List<Employee> findEmployeeList() {
		return this.employeeMapper.selectByExample(null);
	}

	// 添加员工
	@Override
	public void saveEmployee(Employee employee) {
		this.employeeMapper.insertSelective(employee);
	}

	// 根据id删除员工信息
	@Override
	public void deleteEmployee(Long id) {
		this.employeeMapper.deleteByPrimaryKey(id);
	}

	// 根据id查询员工信息
	@Override
	public Employee findEmployee(Long id) {
		return this.employeeMapper.selectByPrimaryKey(id);
	}

	// 根据id修改员工信息
	@Override
	public void updateEmployee(Employee employee) {
		this.employeeMapper.updateByPrimaryKeySelective(employee);
	}

}
