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

import javax.servlet.http.HttpServletRequest;
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
     * 保存员工，保存之前进行后端数据校验
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
        // 提示信息和前端保持一致，防止两次提示信息不一致
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

    // 根据员工ID查询员工信息
    @ResponseBody
    @GetMapping(value = "/emp/{id}")
    public Msg getEmp(@PathVariable("id") Integer id) {
        Employee emp = employeeService.getEmp(id);
        return Msg.success().add("emp", emp);
    }

    /**
     * 更新员工信息
     * 如果直接发送 ajax=PUT 形式的请求
     * 封装的数据：Employee = [empId=1014, empName=null, gender=null, email=null, dId=null]
     *
     * 问题：
     * 请求体中有数据, 但是Employee对象封装不上：update tbl_emp  where emp_id = 1014;
     *
     * 原因：
     * Tomcat：
     *      1、将请求体中的数据，封装一个map。
     *      2、request.getParameter("empName")就会从这个map中取值。
     *      3、SpringMVC封装POJO对象的时候，会调用 request.getParamter("email") 赋给POJO中每个属性的值
     *
     * AJAX发送PUT请求引发的血案：
     *      PUT请求，请求体中的数据，request.getParameter("empName")拿不到
     *      Tomcat一看是PUT不会封装请求体中的数据为map，只有POST形式的请求才封装请求体为map
     *
     * Tomcat源码：
     * org.apache.catalina.connector.Request--parseParameters() (3111);
     * if( !getConnector().isParseBodyMethod(getMethod()) ) {
     *      success = true;
     *      return;
     * }
     *
     * 解决方案：
     * 我们要能支持直接发送PUT之类的请求还要封装请求体中的数据
     * 1、配置上HttpPutFormContentFilter；
     * 2、他的作用；将请求体中的数据解析包装成一个map。
     * 3、request被重新包装，request.getParameter()被重写，就会从自己封装的map中取数据
     *
     */
    @ResponseBody
    @PutMapping(value = "/update/{empId}")
    public Msg updateEmp(Employee employee, HttpServletRequest request) {
        // System.out.println("请求体中的值：" + request.getParameter("emali"));
        // System.out.println("将要更新的员工数据：" + employee);
        employeeService.updateEmp(employee);
        return Msg.success();
    }

    /**
     * 删除员工数据
     */
    @ResponseBody
    @DeleteMapping(value = "/delete/{id}")
    public Msg deleteEmp(@PathVariable("id") Integer id) {
        employeeService.deleteEmp(id);
        return Msg.success();
    }
}
