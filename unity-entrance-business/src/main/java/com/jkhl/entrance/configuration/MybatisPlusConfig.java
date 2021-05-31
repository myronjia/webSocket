package com.jkhl.entrance.configuration;


import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.unity.common.base.config.interceptor.SelectCountInjector;
import com.unity.common.base.config.interceptor.WildcardInterceptor;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(MybatisProperties.class)
public class MybatisPlusConfig {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private MybatisProperties properties;

    @Autowired
    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Autowired(required = false)
    private Interceptor[] interceptors;

    @Autowired(required = false)
    private DatabaseIdProvider databaseIdProvider;


    /**
     * mybatis-plus分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor page = new PaginationInterceptor();
        return page;
    }
    /**
     * sql注入器  逻辑删除插件
     * @return
     */
    @Primary
    @Bean
    public ISqlInjector iSqlInjector(){
        return new SelectCountInjector();
//        return new LogicSqlInjector();

    }
   /* // 自定义拦截器实现模糊查询中的特殊字符处理
    @Bean
    public WildcardInterceptor wildcardInterceptor() {
        WildcardInterceptor sql = new WildcardInterceptor();
        return sql;
    }*/
    /**
     * sql性能分析插件，输出sql语句及所需时间
     * @return
     */
    @Bean
    @Profile({"devel","test"})// 设置 dev test 环境开启
    public PerformanceInterceptor performanceInterceptor() {
        return new PerformanceInterceptor();
    }
    /**
     * 乐观锁插件
     * @return
     */
    public OptimisticLockerInterceptor optimisticLockerInterceptor(){
        return new OptimisticLockerInterceptor();
    }
    // 自定义拦截器实现模糊查询中的特殊字符处理
    @Bean
    public WildcardInterceptor wildcardInterceptor() {
        return new WildcardInterceptor();
    }

    /**
     * is_deleted=0拦截器
     *
     * @return
     */
//     @Bean
//     public DeletedFilterInterceptor deletedFilterInterceptor() {
//     DeletedFilterInterceptor di = new DeletedFilterInterceptor();
//     di.setDialectType("mysql");
//     di.setTargetMethods(
//     new String[] { SqlMethod.UPDATE_BY_ID.getMethod(),
//     SqlMethod.UPDATE_ALL_COLUMN_BY_ID.getMethod(),
//     SqlMethod.UPDATE.getMethod(), SqlMethod.SELECT_BY_ID.getMethod(),
//     SqlMethod.SELECT_BY_MAP.getMethod(),
//     SqlMethod.SELECT_BATCH_BY_IDS.getMethod(),
//     SqlMethod.SELECT_ONE.getMethod(), SqlMethod.SELECT_COUNT.getMethod(),
//     SqlMethod.SELECT_LIST.getMethod(), SqlMethod.SELECT_PAGE.getMethod(),
//     SqlMethod.SELECT_MAPS.getMethod(),
//     SqlMethod.SELECT_MAPS_PAGE.getMethod(),
//     SqlMethod.SELECT_OBJS.getMethod() });
//
//     return di;
//     }

//    /**
//     * 这里全部使用mybatis-autoconfigure 已经自动加载的资源。不手动指定 配置文件和mybatis-boot的配置文件同步
//     *
//     * @return
//     */
//    @Bean
//    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean() {
//        MybatisSqlSessionFactoryBean mybatisPlus = new MybatisSqlSessionFactoryBean();
//        mybatisPlus.setDataSource(dataSource);
//        mybatisPlus.setVfs(SpringBootVFS.class);
//        if (StringUtils.hasText(this.properties.getConfigLocation())) {
//            mybatisPlus.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
//        }
//        mybatisPlus.setConfiguration(properties.getConfiguration());
//        if (!ObjectUtils.isEmpty(this.interceptors)) {
//            mybatisPlus.setPlugins(this.interceptors);
//        }
////
////        // MP 全局配置，更多内容进入类看注释
////        GlobalConfiguration globalConfig = new GlobalConfiguration(new LogicSqlInjector());
////        globalConfig.setDbType(DBType.MYSQL.name());// 数据库类型
////        // ID 策略 AUTO->`0`("数据库ID自增") INPUT->`1`(用户输入ID")
////        // ID_WORKER->`2`("全局唯一ID") UUID->`3`("全局唯一ID")
////        globalConfig.setIdType(2);
////        globalConfig.setLogicDeleteValue("1");
////        globalConfig.setLogicNotDeleteValue("0");
////        // MP 属性下划线 转 驼峰 , 如果原生配置 mc.setMapUnderscoreToCamelCase(true)
////        // 开启，该配置可以无。
////        // globalConfig.setDbColumnUnderline(true);
////        mybatisPlus.setGlobalConfig(globalConfig);
////        MybatisConfiguration mc = new MybatisConfiguration();
////        // 对于完全自定义的mapper需要加此项配置，才能实现下划线转驼峰
////        // mc.setMapUnderscoreToCamelCase(true);
////        mc.setDefaultScriptingLanguage(MybatisXMLLanguageDriver.class);
////        mybatisPlus.setConfiguration(mc);
//
//        if (this.databaseIdProvider != null) {
//            mybatisPlus.setDatabaseIdProvider(this.databaseIdProvider);
//        }
//        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
//            mybatisPlus.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
//        }
//        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
//            mybatisPlus.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
//        }
//        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
//            mybatisPlus.setMapperLocations(this.properties.resolveMapperLocations());
//        }
//
//
//        return mybatisPlus;
//    }

}
