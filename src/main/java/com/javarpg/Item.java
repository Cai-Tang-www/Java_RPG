package main.java.com.javarpg;
import java.util.*;

//道具接口
public interface Item {
    String getName();//获取道具名称
    Integer getCount();  // 修改返回类型为Integer
    void setCount(int count);
    void use(Character user);
}