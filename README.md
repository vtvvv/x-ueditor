# 百度富文本编辑器插件 x-ueditor

### 项目配置

#### 1、本地存储模式(默认)
+ 新建springboot项目
+ 配置pom.xml
```
	<dependency>
	    <groupId>com.vbabc.core</groupId>
	    <artifactId>x-ueditor</artifactId>
	    <version>1.1.0</version>
	</dependency>
```

+ 配置ueditor.config.js
```
    // 服务器统一请求接口路径
    , serverUrl: URL + "/ueditor/jsp/controller"
```

#### 2、OSS存储模式
+ 新建springboot项目
+ 配置pom.xml
```
	<dependency>
	    <groupId>com.vbabc.core</groupId>
	    <artifactId>x-ueditor</artifactId>
	    <version>1.0.0</version>
	</dependency>
```
+ 复制x-ueditor-example项目中config.json与OSSKey.properties文件至resources/ueditor文件夹下，并配置信息
+ 配置ueditor.config.js
```
    // 服务器统一请求接口路径（对应配置信息中server-url）
    , serverUrl: URL + <server-url>
```

#### 3、其它高级配置
    参见项目[x-ueditor-example](https://github.com/vtvvv/x-ueditor-example) 及对应配置文件说明
