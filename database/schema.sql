create database if not exists student_management
  default character set utf8mb4
  collate utf8mb4_unicode_ci;

use student_management;

drop table if exists system_user;
drop table if exists student;

create table student (
  id bigint primary key auto_increment comment '主键',
  student_no varchar(32) not null comment '学号',
  name varchar(50) not null comment '姓名',
  gender varchar(10) not null comment '性别',
  age int not null comment '年龄',
  class_name varchar(50) not null comment '班级',
  phone varchar(20) not null comment '电话',
  email varchar(100) null comment '邮箱',
  address varchar(255) null comment '地址',
  score decimal(5,2) not null default 0.00 comment '成绩',
  status varchar(20) not null default '在读' comment '状态：在读、休学、毕业',
  remark varchar(500) null comment '备注',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  unique key uk_student_no (student_no),
  key idx_class_name (class_name),
  key idx_status (status),
  key idx_score (score)
) engine=InnoDB default charset=utf8mb4 comment='学生信息表';

create table system_user (
  id bigint primary key auto_increment comment '主键',
  username varchar(32) not null comment '用户名',
  password_hash varchar(255) not null comment '密码哈希',
  display_name varchar(50) not null comment '显示名称',
  created_at datetime not null default current_timestamp comment '创建时间',
  updated_at datetime not null default current_timestamp on update current_timestamp comment '更新时间',
  unique key uk_system_user_username (username)
) engine=InnoDB default charset=utf8mb4 comment='系统用户表';
