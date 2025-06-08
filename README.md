# 深海回响（Deep Echo）

![横向思维谜题](https://img.shields.io/badge/Lateral%20Thinking-Puzzles-blue)![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen)![Vue.js](https://img.shields.io/badge/Vue.js-3.2.13-42b883)![Java](https://img.shields.io/badge/Java-17-orange)

## 📖 概述

这是一个海龟汤网页应用，玩家可与AI法官一起参与游戏。AI会呈现一个神秘场景，玩家需要通过是/否回答收集线索，拼凑出真相。

本应用采用Spring Boot后端和Vue.js前端构建，创造现代化、响应式的用户体验。

## 🛠️ 技术栈

### 后端
- **Java 17**
- **Spring Boot 3.4.3**
- **MyBatis-Plus**：用于数据库操作的ORM框架
- **MySQL**：存储聊天室和消息的数据库
- **Knife4j**：API文档和测试
- **Volcengine Java SDK**：AI服务集成
- **Lombok**：减少样板代码

### 前端
- **Vue.js 3.2.13**：渐进式JavaScript框架
- **Axios**：用于API请求的HTTP客户端

## 🚀 快速开始

### 前提条件
- JDK 17+
- Maven 3.6+
- Node.js 14+
- MySQL 8.0+

### 后端设置

1. 克隆仓库：
   ```bash
   git clone https://github.com/yourusername/Lateral_Thinking_Puzzles.git
   cd Lateral_Thinking_Puzzles
   ```

2. 设置MySQL数据库：
   ```bash
   mysql -u root -p < haigui/init.sql
   ```

3. 配置应用：
   - 在`haigui/src/main/resources`目录下复制`application.template.yml`为`application.yml`
   - 按照模板中的注释修改配置，尤其是数据库连接和AI服务API密钥
   ```bash
   # 在haigui/src/main/resources目录下执行
   cp application.template.yml application.yml
   # 然后编辑application.yml文件设置你的配置
   ```

4. 构建并运行Spring Boot应用：
   ```bash
   cd haigui
   mvn spring-boot:run
   ```

后端服务器将在`http://localhost:3000`上运行。

### 前端设置

1. 导航到前端目录：
   ```bash
   cd frontend
   ```

2. 安装依赖：
   ```bash
   npm install
   ```

3. 启动开发服务器：
   ```bash
   npm run serve
   ```

前端开发服务器将在`http://localhost:8080`上运行。

## 📝 项目结构

```
Lateral_Thinking_Puzzles/
├── haigui/                  # 后端Spring Boot应用
│   ├── src/                 # 源代码
│   │   ├── main/
│   │   │   ├── java/com/haigui/haigui/
│   │   │   │   ├── config/         # 配置类
│   │   │   │   ├── controller/     # REST控制器
│   │   │   │   ├── mapper/         # MyBatis数据库映射器
│   │   │   │   ├── model/          # 数据模型/实体
│   │   │   │   ├── service/        # 业务逻辑服务
│   │   │   │   └── manager/        # 外部服务管理器
│   │   │   └── resources/          # 应用属性配置等
│   └── pom.xml              # Maven依赖
├── frontend/                # Vue.js前端应用
│   ├── public/              # 静态资源
│   ├── src/
│   │   ├── assets/          # 图片、字体等
│   │   ├── components/      # Vue组件
│   │   ├── router/          # Vue Router配置
│   │   ├── utils/           # 工具函数
│   │   ├── views/           # 页面组件
│   │   ├── App.vue          # 根组件
│   │   └── main.js          # 应用入口点
│   └── package.json         # npm依赖
└── README.md                # 项目文档
```

## 🔍 API文档

当后端运行时，API文档可在以下地址获取：
```
http://localhost:3000/api/doc.html
```

