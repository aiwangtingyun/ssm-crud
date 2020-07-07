
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>员工列表</title>

    <!-- web路径：
         不以/开始的相对路径，找资源，以当前资源的路径为基准，经常容易出问题。
         以/开始的相对路径，找资源，以服务器的路径为标准(http://localhost:3306)；需要加上项目名
		 http://localhost:3306/crud
    -->
    <%
        pageContext.setAttribute("APP_PATH", request.getContextPath());
    %>

    <%-- 引入 jquery --%>
    <script type="text/javascript" src="${APP_PATH}/static/js/jquery-1.12.4.min.js"></script>
    <%-- 引入 bootstrap --%>
    <link rel="stylesheet" href="${APP_PATH}/static/bootstrap3.7/css/bootstrap.min.css">
    <script type="text/javascript" src="${APP_PATH}/static/bootstrap3.7/js/bootstrap.min.js"></script>
</head>
<body>

<!-- 员工添加的模态框 -->
<div class="modal fade" id="empAddModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <%-- 模态框头部 --%>
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">员工添加</h4>
            </div>
            <%-- 模态框主体 --%>
            <div class="modal-body">
                <%-- 表单元素的 name 属性需要和 employee 对象的属性名相对应 --%>
                <form class="form-horizontal">
                    <%--员工姓名--%>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">empName</label>
                        <div class="col-sm-10">
                            <input type="text" name="empName" id="empName_add_input" class="form-control"
                                   placeholder="empName">
                            <span class="help-block"></span> <%--校验提示信息--%>
                        </div>
                    </div>
                    <%--邮箱--%>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">email</label>
                        <div class="col-sm-10">
                            <input type="text" name="email" class="form-control" id="email_add_input"
                                   placeholder="email@google.com">
                            <span class="help-block"></span> <%--校验提示信息--%>
                        </div>
                    </div>
                    <%--性别--%>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">gender</label>
                        <div class="col-sm-10">
                            <label class="radio-inline">
                                <input type="radio" name="gender" id="gender1_add_input" value="M" checked="checked">男
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="gender" id="gender2_add_input" value="F">女
                            </label>
                        </div>
                    </div>
                    <%--部门名--%>
                    <div class="form-group">
                        <label class="col-sm-2 control-label">deptName</label>
                        <div class="col-sm-4">
                            <%--部门提交部门id即可--%>
                            <select class="form-control" name="dId"></select>
                        </div>
                    </div>
                </form>
            </div>
            <%-- 模态框底部 --%>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="emp_save_btn">保存</button>
            </div>
        </div>
    </div>
</div>

<!-- 搭建显示页面 -->
<div class="container">
    <%-- 标题 --%>
    <div class="row">
        <div class="col-md-12">
            <h1>SSM-CRUD</h1>
        </div>
    </div>

    <%-- 按钮 --%>
    <div class="row">
        <div class="col-md-4 col-md-offset-8">
            <button class="btn btn-primary" id="emp_add_modal_btn">新增</button>
            <button class="btn btn-danger">删除</button>
        </div>
    </div>

    <!-- 显示表格数据 -->
    <div class="row">
        <div class="col-md-12">
            <table class="table table-hover" id="emps_table">
                <%-- 表头 --%>
                <thead>
                    <tr class="info">
                        <th>#</th>
                        <th>empName</th>
                        <th>gender</th>
                        <th>email</th>
                        <th>deptName</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <%-- 表体 --%>
                <tbody>

                </tbody>
            </table>
        </div>
    </div>

    <!-- 显示分页信息 -->
    <div class="row">
        <!--分页文字信息  -->
        <div class="col-md-6" id="page_info_area"></div>
        <!-- 分页条信息 -->
        <div class="col-md-6" id="page_nav_area">
        </div>
    </div>

    <%-- 使用 AJAX 请求来获取数据 --%>
    <script type="text/javascript">

        var currentPage; // 当前页
        var totalRecord; // 总记录数

        // 页面加载完成后跳转到第一页
        $(function () {
            toPage(1);
        });

        // AJAX 请求数据，跳转指定页
        function toPage(pn) {
            $.ajax({
                url: "${APP_PATH}/emps",
                data: "pn=" + pn,
                type: "GET",
                success: function (result) {
                    // 1、解析并显示员工数据
                    build_emp_table(result);
                    // 2、解析并显示分页信息
                    build_page_info(result);
                    // 3、解析显示分页条数据
                    build_page_nav(result);
                }
            });
        }
        
        // 解析并显示员工数据
        function build_emp_table(result) {
            // 清空先前的数据
            $("#emps_table tbody").empty();

            var emps = result.extend.pageInfo.list;

            // 遍历元素
            $.each(emps, function (index, emp) {
                var empIdTd = $("<td></td>").append(emp.empId);
                var empNameTd = $("<td></td>").append(emp.empName);
                var genderTd = $("<td></td>").append(emp.gender == "M" ? "男" : "女");
                var emailTd = $("<td></td>").append(emp.email);
                var deptNameTd = $("<td></td>").append(emp.department.deptName);

                // 按钮组
                var editBtn = $("<button></button>").addClass("btn btn-primary btn-sm")
                    .append($("<span></span>").addClass("glyphicon glyphicon-pencil")).append("编辑")
                var delBtn = $("<button></button>").addClass("btn btn-danger btn-sm")
                    .append($("<span></span>").addClass("glyphicon glyphicon-trash")).append("编辑")
                var btnTd = $("<td></td>").append(editBtn).append(" ").append(delBtn)

                $("<tr></tr>").append(empIdTd)
                    .append(empNameTd)
                    .append(genderTd)
                    .append(emailTd)
                    .append(deptNameTd)
                    .append(btnTd)
                    .appendTo("#emps_table tbody");
            });
        }
        
        // 解析并显示分页信息
        function build_page_info(result) {
            currentPage = result.extend.pageInfo.pageNum;
            totalRecord = result.extend.pageInfo.total;
            var totalPages = result.extend.pageInfo.pages;

            $("#page_info_area").empty().append("当前第 " + currentPage + " 页，总共 " + totalPages + " 页，总共 " +
                    totalRecord + " 记录");
        }

        // 解析显示分页条数据，添加点击跳转
        function build_page_nav(result) {
            // 清空先前的数据
            $("#page_nav_area").empty();

            // 构建 ul 元素
            var navUl = $("<ul></ul>").addClass("pagination");

            // 添加首页和上一页
            var firstPageLi = $("<li></li>").append($("<a></a>").append("首页").attr("href", "#"));
            var prePageLi = $("<li></li>").append($("<a></a>").append("&laquo;").attr("href", "#"));
            if (result.extend.pageInfo.hasPreviousPage === false) {
                firstPageLi.addClass("disabled");
                prePageLi.addClass("disabled");
            } else {
                // 为元素添加点击事件
                firstPageLi.click(function () {
                    toPage(1);
                });
                prePageLi.click(function () {
                    toPage(result.extend.pageInfo.pageNum - 1);
                });
            }
            navUl.append(firstPageLi).append(prePageLi);

            // 添加中间页码
            $.each(result.extend.pageInfo.navigatepageNums, function (index, pageNum) {
                var pageNumLi = $("<li></li>").append($("<a></a>").append(pageNum).attr("href", "#"));
                if (result.extend.pageInfo.pageNum === pageNum) {
                    pageNumLi.addClass("active");
                } else {
                    // 为元素添加点击事件
                    pageNumLi.click(function () {
                        toPage(pageNum);
                    });
                }
                navUl.append(pageNumLi);
            });

            // 添加末页和下一页
            var lastPageLi = $("<li></li>").append($("<a></a>").append("末页").attr("href", "#"));
            var nextPageLi = $("<li></li>").append($("<a></a>").append("&raquo;").attr("href", "#"));
            if (result.extend.pageInfo.hasNextPage === false) {
                lastPageLi.addClass("disabled");
                nextPageLi.addClass("disabled");
            } else {
                // 为元素添加点击事件
                lastPageLi.click(function () {
                    toPage(result.extend.pageInfo.pages);
                });
                nextPageLi.click(function () {
                    toPage(result.extend.pageInfo.pageNum + 1);
                });
            }
            navUl.append(nextPageLi).append(lastPageLi);

            // 把 ul 元素添加到 nav 元素中
            var navEle = $("<nav></nav>").append(navUl);
            navEle.appendTo("#page_nav_area")
        }

        // 点击新增按钮弹出模态框
        $("#emp_add_modal_btn").click(function () {
            // 查旬部门信息，并显示在下拉列表中
            getDepts("#empAddModal select")
            // 弹出模态框
            $("#empAddModal").modal({
                backdrop: "static"
            });
        });
        
        // 查出所有的部门信息,并显示在对应元素中
        function getDepts(ele) {
            // 先清空元素之前的数据
            $(ele).empty();

            // 发送 ajax 请求数据
            $.ajax({
                url: "${APP_PATH}/depts",
                type: "GET",
                success: function (result) {
                    // {"code":100,"msg":"处理成功！","extend":
                    // {"depts":[{"deptId":1,"deptName":"开发部"},{"deptId":2,"deptName":"测试部"}]}}
                    // console.log(result);
                    // 添加到下拉列表中
                    $.each(result.extend.depts, function () {
                        var optionEle = $("<option></option>").append(this.deptName).attr("value", this.deptId);
                        optionEle.appendTo(ele);
                    });
                }
            });
        }
        
    </script>

</div>
</body>
</html>
