package main.java.com.javarpg;
import java.util.*;

public class MP implements Item{
    private String name;
    private int count=1;
    public MP(String name,int count) {
        this.name = name;
        this.count=count;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public void use(Character user) {
        if(count<=0){
            System.out.println("道具已用完");
            return;
        }
        user.setMP(user.getMP() + 10);
        System.out.println(user.getName() + " 恢复了 10 MP"+",当前MP:"+user.getMP());
        count--;
    }
    @Override
    public Integer getCount(){  // 修改返回类型为Integer
        return Integer.valueOf(count);
    }
    @Override
    public void setCount(int count){
        this.count+=count;
    }
}