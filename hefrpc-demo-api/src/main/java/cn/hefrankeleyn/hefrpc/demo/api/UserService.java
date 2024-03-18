package cn.hefrankeleyn.hefrpc.demo.api;

public interface UserService {

    User findById(int id);
    User findById(int id, String name);

    Integer findIdNum(int id);
    Long findIdNum(long id);
    Integer findIdNum(Double id);


    String findName(String userName);



}
