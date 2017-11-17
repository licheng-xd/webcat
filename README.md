# Webcat
Webcat is a faster and simpler http server by netty

## How to use

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
}
```
添加自己的controller:
```xml
<context:component-scan base-package="com.lchml.test"/>
```

```Java
@HttpController(path = "/test")
public class TestController {

    @HttpRequestMapping(path = "/hello", consumes = {"text/plain"})
    public String testHello() {
        return "hello webcat";
    }

    @HttpRequestMapping(path = "/bodytest", method = {ReqMethod.POST})
    public String testBody(@ReqBody String body) {
        return "hello webcat " + body;
    }

    @HttpRequestMapping(path = "/redirect", method = {ReqMethod.GET})
    public void testRedirect(FullHttpResponse response) {
        ResponseUtil.redirect(response, "http://lchml.com");
    }
}
```
## Config

* 默认会打开所有http请求的日志,logEnbale设置为false可以关闭日志
* 默认日志中不会输入response内容,logResponse设置为true可以打开
* 默认返回content-type为application/json;charset=utf-8,通过defaultProduce参数可以修改

```xml
<bean class="com.lchml.webcat.config.WebcatConf" id="webcatConf">
    <property name="logEnable" value="true"/>
    <property name="logResponse" value="true"/>
    <property name="defaultProduce" value="text/plain"/>
</bean>
```

