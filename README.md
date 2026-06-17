# 学生管理系统

前后端分离学生管理系统：

- 前端：Vue 3 + Vite
- 后端：Spring Boot 2.7 + MyBatis + MySQL + Redis
- 数据库：MySQL 建库建表、索引、初始化数据、CRUD 示例 SQL

## 目录结构

```text
frontend/   Vue 前端项目
backend/    Spring Boot 后端项目
database/   MySQL 数据库脚本
```

## 数据库初始化

先启动 MySQL，然后依次执行：

```bash
mysql -uroot -proot < database/schema.sql
mysql -uroot -proot < database/seed.sql
```

如果你的 MySQL 用户名或密码不是 `root/root`，请同步修改 `backend/src/main/resources/application.yml`。

## 启动 Redis

后端默认连接本机 Redis：

```text
host: localhost
port: 6379
```

## 启动后端

```bash
cd backend
mvn spring-boot:run
```

接口地址：

```text
http://localhost:8080/api/students
```

## 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端地址：

```text
http://localhost:5173
```

## 已实现功能

- 学生新增、编辑、删除、查询
- 关键字、班级、状态筛选
- 学号唯一校验
- 参数校验和统一异常返回
- 班级列表、统计概览、班级人数分布、成绩分布
- MyBatis XML 动态 SQL
- Redis 缓存学生列表、详情、班级列表和统计数据
- 新增、修改、删除后自动清理缓存
