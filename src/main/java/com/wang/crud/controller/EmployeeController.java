package com.wang.crud.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.crud.bean.Employee;
import com.wang.crud.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

// 处理员工的 CRUD
@Controller
public class EmployeeController {

    // 调用 service 层
    @Autowired
    EmployeeService employeeService;

    // 查询所有员工信息：分页查询
    @RequestMapping(value = "/emps")
    public String getEmps(
            @RequestParam(value = "pn", defaultValue = "1") Integer pn,
            Model model) {

        // 引入PageHelper分页插件,在查询之前只需要调用，传入页码，以及每页的大小
        PageHelper.startPage(pn, 5);
        // startPage后面紧跟的这个查询就是一个分页查询
        List<Employee> employees = employeeService.getAll();

        // 使用 pageInfo 包装查询后的结果，只需要将pageInfo交给页面就行了
        // pageInfo 封装了详细的分页信息,包括有我们|查询出来的数据，传入连续显示的页数
        PageInfo<Employee> page = new PageInfo<>(employees, 5);

        // 把分页查询数据放入到页面里面
        model.addAttribute("pageInfo", page);

        return "list";
    }
}
