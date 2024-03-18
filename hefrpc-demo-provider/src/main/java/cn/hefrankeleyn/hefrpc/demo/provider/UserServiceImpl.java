package cn.hefrankeleyn.hefrpc.demo.provider;

import cn.hefrankeleyn.hefrpc.core.annotation.HefProvider;
import cn.hefrankeleyn.hefrpc.demo.api.User;
import cn.hefrankeleyn.hefrpc.demo.api.UserService;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Date 2024/3/7
 * @Author lifei
 */
@Service
@HefProvider
public class UserServiceImpl implements UserService {

    @Override
    public User findById(int id) {
        String name = "hef-" + System.currentTimeMillis();
        return new User(id, name);
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, name);
    }

    @Override
    public Integer findIdNum(int id) {
        return id;
    }

    @Override
    public Long findIdNum(long id) {
        return id;
    }

    @Override
    public Integer findIdNum(Double id) {
        return id.intValue();
    }

    @Override
    public String findName(String userName) {
        if (Objects.nonNull(userName)) {
            return userName;
        }
        return "unknown";
    }
}
