数据库文件：

```sql
# 创建数据库
DROP DATABASE IF EXISTS ssm_crud;
CREATE DATABASE IF NOT EXISTS ssm_crud;

USE ssm_crud;

# 部门表
DROP TABLE IF EXISTS `tbl_dept`;
CREATE TABLE IF NOT EXISTS `tbl_dept` (
	`dept_id` INT(11) PRIMARY KEY AUTO_INCREMENT,
	`dept_name` VARCHAR(255) NOT NULL
)ENGINE=INNODB, CHARACTER SET=utf8;

# 员工表
DROP TABLE IF EXISTS `tbl_emp`;
CREATE TABLE IF NOT EXISTS `tbl_emp` (
	`emp_id` INT(11) PRIMARY KEY AUTO_INCREMENT,
	`emp_name` VARCHAR(255) NOT NULL,
	`gender` CHAR(1),
	`email` VARCHAR(255),
	`d_id` INT(11) NOT NULL,
	CONSTRAINT fk_emp_dept FOREIGN KEY(`d_id`) REFERENCES `tbl_dept`(`dept_id`)
) ENGINE=INNODB, CHARACTER SET=utf8;

# 添加部门数据
INSERT INTO `tbl_dept`(`dept_name`) VALUES ("技术部"),("测试部"),("运营部"),("财务部");

# 添加员工数据
INSERT INTO `tbl_emp`(`emp_name`, `gender`, `email`, `d_id`)
VALUES 
("张三", "M", "zhangsan@163.com", 1),
("李四", "M", "lisi@163.com", 1),
("王五", "M", "wangwu@163.com", 1),
("小明", "M", "xiaoming@qq.com", 1),
("小花", "F", "xiaohua@qq.com", 1),
("小红", "F", "xiaohong@qq.com", 1),
("小月", "M", "xiaoyue@qq.com", 1),
("小赵", "M", "xiaozhao@163.com", 1),
("小云", "F", "xiaoyun@163.com", 1),
("Jack", "M", "Jack@google.com", 1),
("Marry", "F", "Marray@google.com", 1),
("Bob", "M", "Bob@google.com", 1),
("John", "M", "John@google.com", 1),
("Andy", "M", "Andy@google.com", 1);
```

