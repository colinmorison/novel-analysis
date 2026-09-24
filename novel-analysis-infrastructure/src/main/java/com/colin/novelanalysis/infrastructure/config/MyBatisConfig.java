package com.colin.novelanalysis.infrastructure.config;

import com.colin.novelanalysis.infrastructure.persistence.handler.PgVectorTypeHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * MyBatis 配置
 */
@Configuration
@MapperScan("com.colin.novelanalysis.infrastructure.persistence.mapper")
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTypeHandlers(new TypeHandler<?>[]{new PgVectorTypeHandler()});
        return sessionFactory.getObject();
    }
}
