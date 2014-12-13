///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package unicaferater.auth;
//
///**
// *
// * @author chang
// */
//import com.jolbox.bonecp.BoneCPDataSource;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
// 
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//import java.util.Properties;
// 
//@Configuration
//@EnableJpaRepositories(basePackages = {
//        "net.petrikainulainen.spring.social.signinmvc.user.repository"
//})
//@EnableTransactionManagement
//public class PersistenceContext {
// 
//    @Resource
//    private Environment env;
// 
//    @Bean
//    public DataSource dataSource() {
//        BoneCPDataSource dataSource = new BoneCPDataSource();
// 
//        dataSource.setDriverClass(env.getRequiredProperty("db.driver"));
//        dataSource.setJdbcUrl(env.getRequiredProperty("db.url"));
//        dataSource.setUsername(env.getRequiredProperty("db.username"));
//        dataSource.setPassword(env.getRequiredProperty("db.password"));
// 
//        return dataSource;
//    }
// 
//    @Bean
//    public JpaTransactionManager transactionManager() {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
//        return transactionManager;
//    }
// 
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
// 
//        entityManagerFactoryBean.setDataSource(dataSource());
//        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
//        entityManagerFactoryBean.setPackagesToScan({
//                "net.petrikainulainen.spring.social.signinmvc.common.model",
//                "net.petrikainulainen.spring.social.signinmvc.user.model"
//        });
// 
//        Properties jpaProperties = new Properties();
//        jpaProperties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
//        jpaProperties.put("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));
//        jpaProperties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("hibernate.hbm2ddl.auto"));
//        jpaProperties.put("hibernate.ejb.naming_strategy", env.getRequiredProperty("hibernate.ejb.naming_strategy"));
//        jpaProperties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
// 
//        entityManagerFactoryBean.setJpaProperties(jpaProperties);
// 
//        return entityManagerFactoryBean;
//    }
//}