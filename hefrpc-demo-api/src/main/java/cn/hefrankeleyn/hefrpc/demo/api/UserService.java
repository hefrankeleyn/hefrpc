package cn.hefrankeleyn.hefrpc.demo.api;

public interface UserService {

    User findById(int id);

    Integer findIdNum(int id);

    String findName(String userName);

}
