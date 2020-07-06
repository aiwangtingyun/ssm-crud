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
```

