package cn.hefrankeleyn.hefrpc.demo.provider;

import cn.hefrankeleyn.hefrpc.core.annotation.HefProvider;
import cn.hefrankeleyn.hefrpc.demo.api.User;
import cn.hefrankeleyn.hefrpc.demo.api.UserService;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @Date 2024/3/7
 * @Author lifei
 */
@Service
@HefProvider
public class UserServiceImpl implements UserService {

    @Resource
    private Environment environment;
    @Override
    public User findById(int id) {
        String port = environment.getProperty("server.port");
        String name = "hef-V03-" +port+"-" +System.currentTimeMillis();
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

    @Override
    public User ex(boolean flag) {
        if (flag) {
            throw new RuntimeException("just throw an exception");
        }
        String port = environment.getProperty("server.port");
        return new User(100, "hefrpc-" + port + "-" + System.currentTimeMillis());
    }

    private String timoutPortList = "8081";

    @Override
    public void updateTimeoutPorts(String timeoutPorts) {
        this.timoutPortList = timeoutPorts;
    }

    @Override
    public User findTimeOut(int timeout) {
        String port = environment.getProperty("server.port");
        Set<String> timeoutPortSet = Sets.newHashSet(Splitter.on(",").split(timoutPortList));
        if (timeoutPortSet.contains(port)) {
            try {
                Thread.sleep(timeout);
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return new User(121, "timeout-"+port);
    }
}
