# 学生管理系统

前后端分离学生管理系统：

- 前端：Vue 3 + Vite
- 后端：Spring Boot 2.7 + MyBatis + MySQL，可选 Redis 缓存
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
mysql -uroot -p123456 < database/schema.sql
mysql -uroot -p123456 < database/seed.sql
```

当前 MySQL 用户名为 `root`，密码为 `123456`。如果你的 MySQL 用户名或密码不同，请同步修改 `backend/src/main/resources/application.yml`。

## 启动 Redis（可选）

后端本地开发默认使用内存缓存，不强制依赖 Redis。如果需要启用 Redis 缓存，先启动本机 Redis，并设置环境变量 `CACHE_TYPE=redis`：

```text
host: localhost
port: 6379
```

## 启动后端

```bash
run-backend.cmd
```

`run-backend.cmd` 会优先从已打包的 jar 启动；只有 jar 不存在或后端源码更新后，才自动执行一次 Maven 打包。这样日常启动不用每次走 `mvn spring-boot:run`。

如果提示 `8080` 端口已被占用，先执行：

```bash
stop-backend.cmd
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
- 可选 Redis 缓存学生列表、详情、班级列表和统计数据
- 新增、修改、删除后自动清理缓存
