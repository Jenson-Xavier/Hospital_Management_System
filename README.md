# NUAA 479HospitalSystem

### 基于springboot+vue3的医院预约挂号系统

#### 1、开发平台

- 【前端】基于【VScode】IDE，采用【vue3】开发框架
- 【后端】基于【IntelliJ IDEA】IDE，采用【SpringBoot】开发框架
- 另含Android端患者用户版本，基于【Android Studio】IDE开发，待提交…

#### 2、用户类型

- 管理员
- 患者用户
- 医生用户

#### 3、部署方式

使用前请确保本地已安装相应IDE和Redis工具。

Redis工具安装地址：https://www.cnblogs.com/qingheshiguang/p/17952623

```
git clone https://gitee.com/tjy_chenxi/479-hospital-system.git
cd 479-hospital-system
```

##### 3.1 前端

```
# 1、进入vue目录
cd vue
# 2、以当前路径打开终端，启动vscode
code .
# 3、安装所需依赖
npm install
# 4、启动前端工程
npm run dev
```

##### 3.2 后端

请确保本地已安装IntelliJ IDEA(专业版)和mySQL数据库。

```
# 在MySQL Command Line Client工具中创建数据库db_hospitalSys
create database db_hospitalSys;
```

在IDEA中以[springboot](https://gitee.com/tjy_chenxi/479-hospital-system/tree/master/springboot)为根目录打开项目。

1. 加载所有Maven项目，导入依赖 (见[pom.xml](https://gitee.com/tjy_chenxi/479-hospital-system/blob/master/springboot/pom.xml))。

2. 打开项目后请在[application.yml](https://gitee.com/tjy_chenxi/479-hospital-system/blob/master/springboot/src/main/resources/application.yml)文件中修改数据库密码。


##### 3.3 安卓端

请使用android studio打开[android/SecondJava](https://gitee.com/tjy_chenxi/479-hospital-system/tree/master/android/SecondJava)目录，核心代码逻辑包含于hospitalsystem目录下。

本项目尚未部署到云服务器上，但已经实现安卓端与电脑端的本地连接。请确保手机与PC连接同一网络。

1. 请在[configUtil.java](https://gitee.com/tjy_chenxi/479-hospital-system/blob/master/android/SecondJava/hospitalsystem/src/main/java/com/example/hospitalsystem/util/configUtil.java)中，设置IP地址为PC的IP地址。
2. 请在[JDBCUtil.java](https://gitee.com/tjy_chenxi/479-hospital-system/blob/master/android/SecondJava/hospitalsystem/src/main/java/com/example/hospitalsystem/util/JDBCUtil.java)中，设置PC上数据库的用户名与密码。

#### 4、效果预览

##### Web端

![img](https://gitee.com/tjy_chenxi/479-hospital-system/raw/master/images/web_login.png)

![img](https://gitee.com/tjy_chenxi/479-hospital-system/raw/master/images/patient_appoint.png)

![img](https://gitee.com/tjy_chenxi/479-hospital-system/raw/master/images/doctor_appoint.png)

![img](https://gitee.com/tjy_chenxi/479-hospital-system/raw/master/images/admin.png)

##### Android端

<center class="half">
    <img src="https://gitee.com/tjy_chenxi/479-hospital-system/raw/master/images/android_login.jpg" width="300"/>
    <img src="https://gitee.com/tjy_chenxi/479-hospital-system/raw/master/images/android_index.jpg" width="300"/>
</center>

