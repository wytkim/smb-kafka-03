/*
 * Copyright (c) 2015 SM band, Inc.
 * All right reserved.
 *
 * This software is the confidential and proprietary information of SM band
 * , Inc. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * you entered into with SM band.
 *
 */
package com.smband.kafka.configure;

import javax.sql.DataSource;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * <pre>
 * 개요:
 * </pre>
 * @author ytkim
 * @create 2021. 8. 1.
 * @version 
 * @since 
 */
@Slf4j
@Configuration
@EnableTransactionManagement 
@MapperScan(basePackages= {"com.smband.kafka.mapper"}, sqlSessionTemplateRef = "sqlSessionTemplate")
public class DatasourceConfig {

	/** DataSource Main 생성 */
    @Bean("mainDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource-main")
    public DataSource mainDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .build();
    }
    
    /** sqlSessionFactory Main 생성 */
    @Bean("sqlSessionFactoryMain")
    @Primary
    public SqlSessionFactory sqlSessionFactoryMain(@Qualifier("mainDataSource") DataSource mainDataSource) throws Exception {
        log.info("SqlSessionFactory Main Start");
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(mainDataSource);
        factoryBean.setVfs(SpringBootVFS.class);
        factoryBean.setConfigLocation(resolver.getResource("classpath:mybatis-config.xml"));
        factoryBean.setTypeAliasesPackage("com.smband.kafka.model");
 
        factoryBean.setMapperLocations(resolver.getResources("classpath:/sqlmaps/*.xml"));
 
        return factoryBean.getObject();
    }
    
    /** sqlSession Main 생성 
     * @throws Exception */
    @Bean("sqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionMain(@Qualifier("sqlSessionFactoryMain") SqlSessionFactory mainSqlSessionFactory) throws Exception {
    	return new SqlSessionTemplate(mainSqlSessionFactory);
    }

    /** query batch 처리 */
    @Bean("sqlSessionBatchTemplate")
    public SqlSessionTemplate sqlSessionBatchMain(@Qualifier("sqlSessionFactoryMain") SqlSessionFactory mainSqlSessionFactory) throws Exception {
    	return new SqlSessionTemplate(mainSqlSessionFactory, ExecutorType.BATCH);
    }
    
    @Bean("tranManager")
    @Primary
    public DataSourceTransactionManager transactionManager(@Qualifier("mainDataSource") DataSource mainDataSource){
        DataSourceTransactionManager manager = new DataSourceTransactionManager(mainDataSource);
        return manager;
    } 
}
