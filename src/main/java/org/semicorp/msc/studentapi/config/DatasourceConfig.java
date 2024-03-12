package org.semicorp.msc.studentapi.config;

import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.spring4.JdbiFactoryBean;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.AnnotationMBeanExporter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;

import javax.sql.DataSource;

import static com.google.common.collect.Lists.newArrayList;

@Configuration
@EnableTransactionManagement
public class DatasourceConfig {

    @Bean
    @Qualifier("core.datasource")
    @Primary
    @ConfigurationProperties("core.datasource")
    public HikariDataSource coreDataSource() {
        final HikariDataSource dataSource = (HikariDataSource) DataSourceBuilder.create()
                .type(HikariDataSource.class).build();
        dataSource.setRegisterMbeans(true);
        dataSource.setPoolName("AdminCorePool");
        return dataSource;
    }

    @Bean
    @Primary
    @Qualifier("student.db")
    public JdbiFactoryBean coreDbi(@Qualifier("core.datasource") final DataSource dataSource) {
        final JdbiFactoryBean factoryBean = new JdbiFactoryBean(dataSource);
        factoryBean.setAutoInstallPlugins(true);
        factoryBean.setPlugins(newArrayList(new SqlObjectPlugin()));
        return factoryBean;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(@Qualifier("core.datasource") final DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public IdGenerator uuid() {
        return new JdkIdGenerator();
    }

    @Bean
    public MBeanExporter exporter() {
        final MBeanExporter exporter = new AnnotationMBeanExporter();
        exporter.setAutodetect(true);
        exporter.setExcludedBeans("coreDataSource");
        return exporter;
    }

}
