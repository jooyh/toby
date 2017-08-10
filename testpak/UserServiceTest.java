package testpak;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.context.Context;
import springbook.user.dao.UserDao;
import springbook.user.domain.User;
import springbook.user.enumpak.Level;
import springbook.user.exception.TestUserServiceException;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceTx;
import springbook.user.service.forTest.MockMailSender;
import springbook.user.service.forTest.TestUserService;
import springbook.user.service.UserServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import static org.junit.Assert.fail;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOEND_FOR_GOLD;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Context.class)
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    UserDao dao;
    @Autowired
    MailSender mailSender;


    List<User> users;

    @Test
    public void bean() {
        assertThat(this.userServiceImpl, is(notNullValue()));
    }

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("bunjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "aguitarj@naver.com"),
                new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "aguitarj@naver.com"),
                new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOEND_FOR_GOLD - 1, "aguitarj@naver.com"),
                new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOEND_FOR_GOLD, "aguitarj@naver.com"),
                new User("green", "오민규", "p5", Level.GOLD, 49, 0, "aguitarj@naver.com")
        );
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() throws Exception {

        dao.deleteAll();
        for (User user : users) {
            dao.add(user);
        }
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(),is(2));
        assertThat(request.get(0),is(users.get(1).getEmail()));
        assertThat(request.get(1),is(users.get(3).getEmail()));

    }

    @Test
    public void add() {
        dao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLeve = users.get(0);
        userWithoutLeve.setLevel(null);

        userServiceImpl.add(userWithLevel);
        userServiceImpl.add(userWithoutLeve);

        User userWithLevelRead = dao.get(userWithLevel.getId());
        User userWithOutLevelRead = dao.get(userWithoutLeve.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithOutLevelRead.getLevel(), is(Level.BASIC));
    }

    @Test
    public void upgradeAllOrNothing() throws IllegalAccessException {

        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(dao);
        testUserService.setMailSender(mailSender);

        UserServiceTx txUserService = new UserServiceTx();
        txUserService.setTransactionManager(transactionManager);
        txUserService.setUserService(testUserService);

        dao.deleteAll();
        for (User user : users) dao.add(user);

        try {
            testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {
        }


        checkLevelUpgraded(users.get(1), false);

    }


    //    private method =============== *
    private void checkLevel(User user, Level expectedLevel) {
        User userUpdated = dao.get(user.getId());
        assertThat(userUpdated.getLevel(), is(expectedLevel));
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = dao.get(user.getId());
        if (upgraded) {
            assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        } else {
            assertThat(userUpdate.getLevel(), is(user.getLevel()));
        }
    }
}
