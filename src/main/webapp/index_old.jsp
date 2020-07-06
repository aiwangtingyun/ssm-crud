<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2020/7/5
  Time: 19:28
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="utf-8" %>

<%-- 转发请求为 /emp 请求 --%>
<jsp:forward page="/emps"/>

<html>
<head>
    <title>首页</title>
    <%-- 引入 jquery --%>
    <script type="text/javascript" src="static/js/jquery-1.12.4.min.js"></script>
    <%-- 引入 bootstrap --%>
    <link rel="stylesheet" href="static/bootstrap3.7/css/bootstrap.min.css">
    <script type="text/javascript" src="static/bootstrap3.7/js/bootstrap.min.js"></script>
</head>
<body>
    <button class="btn btn-success">按钮</button>
    <button class="btn btn-primary">按钮</button>
</body>
</html>
