package main.java.com.javarpg;
import java.util.*;

public class HP implements Item{
    private String name;
    private int count=1;
    public HP(String name,int count) {
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
        user.setHP(user.getHP() + 10);
        System.out.println(user.getName() + " 恢复了 10 HP"+",当前HP:"+user.getHP());
        count--;
    }
    @Override
    public Integer getCount(){
        return Integer.valueOf(count);
    }
    @Override
    public void setCount(int count){
        this.count+=count;
    }
}