package com.wang.crud.controller;

import com.wang.crud.bean.Department;
import com.wang.crud.bean.Msg;
import com.wang.crud.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

// 处理和部门有关的请求
@Controller
public class DepartmentController {

    // 调用 service 层
    @Autowired
    private DepartmentService departmentService;

    // 返回所有部门信息
    @ResponseBody
    @RequestMapping("/depts")
    public Msg getDept() {
        List<Department> depts = departmentService.getDepts();
        return Msg.success().add("depts", depts);
    }
}
