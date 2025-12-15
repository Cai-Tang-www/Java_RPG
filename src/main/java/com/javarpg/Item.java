package main.java.com.javarpg;
import java.util.*;

public interface Item {
    String getName();
    Integer getCount();  // 修改返回类型为Integer
    void setCount(int count);
    void use(Character user);
}