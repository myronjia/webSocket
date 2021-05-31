package com.jkhl;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextListener;


/**
 * server
 * <p>
 * Create by Jung at 2018年05月14日17:48:25
 */

//@EnableFeignClients
//@EnableTransactionManagement
//@EnableAutoConfiguration
//@EnableEurekaClient
//@EnableScheduling
//@EnableDiscoveryClient
// 除了...这些配置类
@SpringBootApplication(exclude = {
        DruidDataSourceAutoConfigure.class,
        MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
//@SpringBootApplication
@EnableAsync
@MapperScan("com.jkhl.entrance.dao")
//扫描组件
@ComponentScan(basePackages = {"com.jkhl.*.**","com.unity.common.*.**","com.unity.springboot"})
//@RestController
//@RequestMapping(value = "/demo1")
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
//        SpringContextUtil.setApplicationContext(context);
    }
    //必须new 一个RestTemplate并放入spring容器当中,否则启动时报错
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(30 * 1000);
        httpRequestFactory.setConnectTimeout(30 * 3000);
        httpRequestFactory.setReadTimeout(30 * 3000);
        return new RestTemplate(httpRequestFactory);
    }

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }

//    @RequestMapping(value = "/first", method = RequestMethod.GET)
//    public Object firstResp (){
//        HttpServletRequest request = SessionHolder.getRequest();
//        Map<String, Object> map = (Map)request.getSession(). getAttribute("map");
//        if(map==null)  map =   new HashMap<>();
//
//        map.put("request Url demo2", request.getRequestURL());
//        request.getSession().setAttribute("map", map);
//        return map;
//    }
//
//    @Resource
//    private RedisUtils redisUtils;
//    @RequestMapping(value = "/sessions", method = RequestMethod.GET)
//    public Object sessions (@RequestParam("token") String token){
////        HttpServletRequest request = SessionHolder.getRequest();
//        Map<String, Object> map = new HashMap<>();
////        map.put("sessionId", request.getSession().getId());
////        map.put("message", request.getSession().getAttribute("map"));
//        map.put("user", redisUtils.getCurrentUserByToken(token));
//        return map;
//    }
}