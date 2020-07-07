package com.wang.crud.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wang.crud.bean.Employee;
import com.wang.crud.bean.Msg;
import com.wang.crud.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

// 处理员工的 CRUD
@Controller
public class EmployeeController {

    // 调用 service 层
    @Autowired
    EmployeeService employeeService;

    // 查询所有员工信息：分页查询
    // @RequestMapping(value = "/emps")
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

    /**
     * 查询所有员工信息, 返回 Json 数据方式
     * ResponseBody 注解会把返回数据转为 JSon 数据，需要导入 jackson 包
     */
    @ResponseBody
    @GetMapping(value = "/emps")
    public Msg getEmpsWithJson(@RequestParam(value = "pn", defaultValue = "1") Integer pn) {
        // 引入PageHelper分页插件,在查询之前只需要调用，传入页码，以及每页的大小
        PageHelper.startPage(pn, 5);
        // startPage后面紧跟的这个查询就是一个分页查询
        List<Employee> employees = employeeService.getAll();

        // 使用 pageInfo 包装查询后的结果，只需要将pageInfo交给页面就行了
        // pageInfo 封装了详细的分页信息,包括有我们|查询出来的数据，传入连续显示的页数
        PageInfo<Employee> pageInfo = new PageInfo<>(employees, 5);

        return Msg.success().add("pageInfo", pageInfo);
    }

    /**
     * 保存员工
     * 导入 Hibernate-Validator 进行 JSR303 校验
     * 使用 @Valid 对参数进行校验，然后返回 BindingResult 类型的结果
     */
    @ResponseBody
    @PostMapping(value = "/save")
    public Msg saveEmp(@Valid Employee employee, BindingResult result) {
        if (result.hasErrors()) {
            // 校验失败,获取校验失败的参数
            HashMap<String, Object> map = new HashMap<>();
            List<FieldError> errors = result.getFieldErrors();
            for (FieldError fieldError : errors) {
                System.out.println("错误的字段名：" + fieldError.getField());
                System.out.println("错误信息：" + fieldError.getDefaultMessage());
                map.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            return Msg.failed().add("errorFields", map);
        } else {
            // 校验成功
            employeeService.saveEmp(employee);
            return Msg.success();
        }
    }

    // 检查用户名是否可用
    @ResponseBody
    @PostMapping(value = "/checkuser")
    public Msg checkuser(@RequestParam("empName") String empName) {
        // 和前端保持一致，先校验用户名合法性
        String regx = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\\u2E80-\\u9FFF]{2,5})";
        if (!empName.matches(regx)) {
            return Msg.failed().add("va_msg", "用户名必须是6-16位数字和字母的组合或者2-5位中文");
        }

        // 数据库用户名重复校验
        boolean b = employeeService.checkUser(empName);
        if (b) {
            return Msg.success().add("va_msg", "用户名可用");
        } else {
            return Msg.failed().add("va_msg", "用户名不可用");
        }
    }
}
