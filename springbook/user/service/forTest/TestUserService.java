package springbook.user.service.forTest;

import springbook.user.domain.User;
import springbook.user.exception.TestUserServiceException;
import springbook.user.service.UserServiceImpl;

public class TestUserService extends UserServiceImpl {

    private String id;

    public TestUserService(String id) {
        this.id = id;
    }

    @Override
    protected void upgradeLevel(User user) throws IllegalAccessException {
        if (user.getId().equals(this.id)) throw new TestUserServiceException();
        super.upgradeLevel(user);
    }

}
