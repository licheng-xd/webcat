# Webcat
Webcat is a faster and simpler http server by netty.

## 快速启动

在spring的配置中,加上对webcat的package扫描:
```xml
<context:component-scan base-package="com.lchml.webcat"/>
```
设置端口并启动:
```Java
public static void main(String[] args) throws WebcatStartException {
    HttpServer httpServer = context.getBean(WebcatHttpServer.class);
    httpServer.setPort(8080);
    httpServer.start();
    
    WebcatServer wsServer = context.getBean(WebcatWsServer.class);
    wsServer.setPort(8081);
    wsServer.start();
}
```

## 文档

[http://www.lchml.com/technology/webcat-doc/](http://www.lchml.com/technology/webcat-doc/)

