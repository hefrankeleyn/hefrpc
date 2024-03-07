package cn.hefrankeleyn.hefrpc.demo.provider;

import cn.hefrankeleyn.hefrpc.core.annotation.HefProvider;
import cn.hefrankeleyn.hefrpc.demo.api.User;
import cn.hefrankeleyn.hefrpc.demo.api.UserService;
import org.springframework.stereotype.Service;

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

}