package com.wang.crud.service;

import com.wang.crud.bean.Employee;
import com.wang.crud.bean.EmployeeExample;
import com.wang.crud.dao.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    // 调用 DAO 层
    @Autowired
    EmployeeMapper employeeMapper;

    // 查询所有员工
    public List<Employee> getAll() {
        return employeeMapper.selectByExampleWithDept(null);
    }

    // 员工保存
    public void saveEmp(Employee employee) {
        employeeMapper.insertSelective(employee);
    }

    // 按照员工 ID 查询员工信息
    public Employee getEmp(Integer id) {
        Employee employee = employeeMapper.selectByPrimaryKey(id);
        return employee;
    }

    // 员工更新
    public void updateEmp(Employee employee) {
        employeeMapper.updateByPrimaryKeySelective(employee);
    }

    // 员工删除
    public void deleteEmp(Integer id) {
        employeeMapper.deleteByPrimaryKey(id);
    }

    // 批量删除员工
    public void deleteBatch(List<Integer> ids) {
        EmployeeExample example = new EmployeeExample();
        EmployeeExample.Criteria criteria = example.createCriteria();

        //delete from xxx where emp_id in(1,2,3)
        criteria.andDIdNotIn(ids);
        employeeMapper.deleteByExample(example);
    }

    // 检查员工名是否可用
    public boolean checkUser(String empName) {
        EmployeeExample example = new EmployeeExample();
        EmployeeExample.Criteria criteria = example.createCriteria();
        criteria.andEmpNameEqualTo(empName);
        long count = employeeMapper.countByExample(example);
        return count == 0;
    }
}
