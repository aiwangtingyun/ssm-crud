package com.wang.crud.service;

import com.wang.crud.bean.Department;
import com.wang.crud.dao.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    // 调用 DAO 层
    @Autowired
    private DepartmentMapper departmentMapper;

    // 查询所有部分信息
    public List<Department> getDepts() {
        List<Department> list = departmentMapper.selectByExample(null);
        return list;
    }
}
