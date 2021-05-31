package com.jkhl.entrance.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

//@Configuration
//@ConfigurationProperties(prefix = "spring")
//public class JdbcConfig {
//
//    @Bean
//    @Primary
//    @ConfigurationProperties(prefix = "spring.datasource.db")
//    public DataSource timedtaskDatasource(){
//        return DataSourceBuilder.create()
//                .build();
//    }
//}
