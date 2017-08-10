package springbook.user.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.UserDao;
import springbook.user.dao.concrete.UserDaoJdbc;
import springbook.user.service.forTest.DummyMailSender;
import springbook.user.service.UserService;


import javax.sql.DataSource;

/**
 * Created by user on 2017-08-03.
 */
@Configuration
public class Context {
    @Bean
    public UserDao userDao() {
        UserDaoJdbc userDao = new UserDaoJdbc();
        userDao.setDataSource(dataSource());
        return userDao;
    }

    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUrl("jdbc:mysql://localhost/toby");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");

        return dataSource;
    }

    @Bean
    public UserService userService() {
        UserService userService = new UserService();
        userService.setUserDao(userDao());
        userService.setTransactionManager(transactionManager());
        userService.setMailSender(mailSender());
        return userService;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        PlatformTransactionManager transactionManager =
                new DataSourceTransactionManager(dataSource());
        return transactionManager;
    }

    @Bean
    public MailSender mailSender() {
        MailSender mailSender = new DummyMailSender();
        return mailSender;
    }
}
