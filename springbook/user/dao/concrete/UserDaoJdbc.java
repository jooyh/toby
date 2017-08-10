package springbook.user.dao.concrete;

import springbook.user.dao.UserDao;
import springbook.user.enumpak.Level;
import springbook.user.exception.DuplicateUserIdException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

/**
 * Created by user on 2017-08-02.
 */

public class UserDaoJdbc implements UserDao {

    // set DataSource ================== *

    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    // =================== concrete method *

    public void add(final User user) throws DuplicateUserIdException {

        try {
            this.jdbcTemplate.update("INSERT INTO users(id, name, password,level,login,recommend,email) VALUES (?, ?, ?, ?, ?, ?,?)",
                    user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(),user.getEmail());
        } catch (DuplicateUserIdException e) {
            throw new DuplicateUserIdException(e);
        }
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?", new Object[]{id}, this.userMapper);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users ORDER BY id", this.userMapper);
    }

    public void deleteAll() {
        this.jdbcTemplate.update("DELETE FROM users");
    }

    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users", (resultSet, i) -> resultSet.getInt(1));
    }

    @Override
    public void update(User user) {
        this.jdbcTemplate.update("update users set name=?,password=?,level=?,login=?,recommend=?,email=? where id=?",
                user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(),user.getEmail(),user.getId());
    }

    //private Method ============== *

    private RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            User user = new User();
            user.setId(resultSet.getString("id"));
            user.setName(resultSet.getString("name"));
            user.setPassword(resultSet.getString("password"));
            user.setLevel(Level.valueOf(resultSet.getInt("level")));
            user.setLogin(resultSet.getInt("login"));
            user.setRecommend(resultSet.getInt("recommend"));
            user.setEmail(resultSet.getString("email"));
            return user;
        }
    };
}
