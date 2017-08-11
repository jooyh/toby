package testpak;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.dao.UserDao;

import springbook.user.domain.User;
import springbook.user.domain.enumpak.Level;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by user on 2017-08-03.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
@DirtiesContext
public class UserDaoTest {

    @Autowired
    private UserDao dao;
    @Autowired
    private DataSource dataSource;



    private User user1;
    private User user2;
    private User user3;

    @Before
    public void setUp() {

        this.user1 = new User("gyumee", "박성철", "springno1", Level.BASIC, 1, 0,"aguitarj@naver.com");
        this.user2 = new User("leegw", "이길원", "springno2", Level.SILVER, 55, 10,"aguitarj@naver.com");
        this.user3 = new User("bumjin", "박범진", "springno3", Level.GOLD, 100, 40,"aguitarj@naver.com");


    }

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userget1 = dao.get(user1.getId());
        assertThat(user1.getName(), is(userget1.getName()));
        assertThat(user1.getPassword(), is(userget1.getPassword()));

        User userget2 = dao.get(user2.getId());
        assertThat(user2.getName(), is(userget2.getName()));
        assertThat(user2.getPassword(), is(userget2.getPassword()));

    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        assertThat(dao.getCount(), is(1));

        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        dao.add(user3);
        assertThat(dao.getCount(), is(3));


    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailure() throws SQLException, ClassNotFoundException {

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.get("unknown_id");
    }

    @Test
    public void getAll() throws SQLException, ClassNotFoundException {
        dao.deleteAll();

        dao.add(user1);
//        dao.add(user2);
//        dao.add(user3);
//        List<User> users1 = dao.getAll();
//        assertThat(users1.size(), is(3));

    }

//    @Test(expected = DataAccessException.class)
//    public void dataAccessException(){
//        dao.deleteAll();
//
//        try {
//            dao.add(user1);
//            dao.add(user1);
//        } catch (DuplicateKeyException ex) {
//
//            SQLException sqlEx =(SQLException)ex.getRootCause();
//
//            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
//
//            assertThat(set.translate(null,null,sqlEx),is(DuplicateKeyException.class));
//        }
//    }

    @Test
    public void update() {
        dao.deleteAll();
        dao.add(user1);
        dao.add(user2);

        user1.setName("오민규");
        user1.setPassword("spring6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);

        dao.update(user1);

        User user1Update = dao.get(user1.getId());
        checkSameUser(user1, user1Update);
        User user2same = dao.get(user2.getId());
        checkSameUser(user2, user2same);

    }


    //private method ================ *

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
        assertThat(user1.getLevel(), is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
    }

}
