# IDPhotos-backend

一个基于 **Spring Boot 2.6 + MyBatis + Sa-Token + Redis + S3** 的证件照后端服务。

该项目主要面向小程序/前端管理后台，提供用户登录、证件照制作、历史记录、积分体系、后台管理、统计分析等能力。

---

## 目录

- [1. 项目简介](#1-项目简介)
- [2. 技术栈](#2-技术栈)
- [3. 功能模块](#3-功能模块)
- [4. 项目结构](#4-项目结构)
- [5. 本地开发环境准备](#5-本地开发环境准备)
- [6. 配置说明](#6-配置说明)
- [7. 启动方式](#7-启动方式)
- [8. 接口概览](#8-接口概览)
- [9. 数据库与索引建议](#9-数据库与索引建议)
- [10. 部署建议](#10-部署建议)
- [11. 常见问题排查](#11-常见问题排查)
- [12. 安全说明](#12-安全说明)

---

## 1. 项目简介

IDPhotos-backend 是一个证件照业务后端，核心能力包括：

1. 用户体系（微信登录、用户信息）
2. 证件照制作（上传照片、调用外部图像服务、结果存储）
3. 历史记录（制作历史、积分历史）
4. 积分系统（消耗、奖励、后台赠送）
5. 后台管理（用户管理、尺寸管理、系统配置）
6. 数据统计（总览、趋势、分布）

---

## 2. 技术栈

### 后端框架
- Java 8
- Spring Boot 2.6.13
- Spring MVC
- Spring Validation

### 数据访问
- MyBatis + tk.mybatis
- PageHelper（分页）
- MySQL

### 认证与缓存
- Sa-Token（登录鉴权）
- Redis / Redisson

### 对象存储与外部调用
- AWS SDK v2 S3（可兼容 MinIO / S3 类存储）
- OkHttp（HTTP 调用）

### 常用工具依赖
- Lombok
- fastjson
- guava
- commons-lang3

---

## 3. 功能模块

### 3.1 用户端接口（`/user`）
- 微信登录
- 用户信息查询/更新
- 制作历史分页
- 积分历史分页

### 3.2 菜单接口（`/menu`）
- 尺寸列表查询

### 3.3 照片接口（`/photo`）
- 上传并生成证件照

### 3.4 管理端接口（`/admin`）
- 管理员登录
- 用户管理（分页、状态、奖励积分）
- 制作历史查询
- **积分历史查询（支持按用户、类型筛选）**
- 尺寸管理、系统配置管理
- 统计分析（总览、趋势、分布）

---

## 4. 项目结构

```text
src/main/java/org/xuanfeng/idphotosbackend
├── config/            # 配置（异常处理、Redis、S3、Sa-Token 等）
├── constant/          # 常量定义
├── controller/        # Controller 层（admin/user/photo/menu）
├── core/              # 核心枚举、异常、缓存封装
├── model/
│   ├── po/            # 数据库实体
│   ├── mapper/        # MyBatis Mapper
│   ├── request/qo/    # 请求对象
│   ├── bo/vo/         # 业务对象 / 响应对象
│   └── response/      # 通用响应体
├── proxy/             # 第三方服务代理（微信、图片服务）
├── schedule/          # 定时任务
├── service/           # service 接口及实现
│   └── biz/           # 业务编排层
└── utils/             # 工具类

src/main/resources
├── application-dev.yml
├── application-test.yml
└── application-prod.yml
```

---

## 5. 本地开发环境准备

建议环境：

- JDK 1.8
- Maven 3.6+
- MySQL 5.7/8.0
- Redis 6+

可选：

- MinIO（本地对象存储模拟）

---

## 6. 配置说明

项目采用多环境配置文件：

- `application-dev.yml`：开发环境
- `application-test.yml`：测试环境
- `application-prod.yml`：生产环境

关键配置项：

1. `spring.datasource.*`：MySQL 连接
2. `spring.redis.*`：Redis 连接
3. `s3.*`：对象存储配置（endpoint/accessKey/secretKey/bucket）
4. `sa-token.*`：认证与 token 配置
5. `wx.host`：微信服务地址
6. `idphoto.host`：图像服务地址

> 建议：将敏感配置（数据库密码、Redis 密码、S3 密钥）通过环境变量或配置中心管理，不要写入公开仓库。

---

## 7. 启动方式

### 7.1 开发模式启动

```bash
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev
```

### 7.2 打包运行

```bash
mvn clean package -DskipTests
java -jar target/IDPhotos-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 7.3 验证服务

启动后可访问接口（示例）：

```text
GET /admin/checkLogin
```

---

## 8. 接口概览

以下为主要接口分组（非完整文档）：

### 8.1 用户端
- `GET /user/login`
- `GET /user/info`
- `POST /user/info/update`
- `POST /user/history`
- `POST /user/point/history`

### 8.2 照片
- `POST /photo/create`（`multipart/form-data`）

### 8.3 菜单
- `GET /menu/size/list`

### 8.4 管理端
- `POST /admin/login`
- `GET /admin/checkLogin`
- `POST /admin/user/list`
- `POST /admin/user/status/update`
- `POST /admin/user/points/reward`
- `POST /admin/user/history/list`
- `POST /admin/user/point/history/list`（支持 `userId/type` 筛选）
- `GET /admin/statistics/summary`
- `GET /admin/statistics/trend`
- `GET /admin/statistics/distribution`

---
