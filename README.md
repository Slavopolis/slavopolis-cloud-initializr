# Slavopolis Boot

SpringBoot 一站式快速集成。该项目基于 Java 21 和 Spring Boot 3.5.0 生态的基础组件封装项目，致力于将常用的企业级开发基础能力抽象为 独立模块化组件，帮助开发者快速复用成熟实现，减少重复造轮子，提升项目开发效率与代码一致性。

## 项目定位

* **模块化设计**：每个基础能力（如配置管理、安全校验、工具类库等）均封装为独立子模块，按需引入，避免冗余依赖。
* **生态兼容性**：深度适配 Spring Boot 自动配置机制，支持与主流中间件（如 Redis、MySQL、Nacos 等）的快速集成。
* **最佳实践沉淀**：组件实现遵循企业级开发规范（如异常处理、日志标准、线程安全等），降低新手使用门槛。

## 核心模块

当前已规划/实现的核心模块包括：

* `slavopolis-common`: 通用基础定义/工具类库（统一响应、字符串处理、集合操作、日期工具等）

## 快速开始

前置要求:

* Java 17+
* Maven 3.8.6+
* Spring Boot 3.5.0（与父项目版本一致）

在项目 pom.xml 中添加父模块依赖（按需选择子模块）：

```xml
<dependency>
    <groupId>club.slavopolis</groupId>
    <artifactId>slavopolis-xxxx</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 贡献与反馈

* 若需新增基础组件需求，可通过 Issue 提交需求描述。
* 欢迎提交 PR 优化现有组件实现（请遵循代码风格规范）。

## 许可证

Slavopolis-Boot 项目采用 Apache License 2.0 开源许可证。