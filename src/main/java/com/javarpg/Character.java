package main.java.com.javarpg;
//角色 类（包含怪物/人物/等等）
public abstract class Character{
    private String name;//name
    private int maxHP;//最大HP
    private int HP;//HP
    private int maxMP;//最大蓝
    private int MP;//蓝
    private int attack ;//攻击
    private int defense;//防御
    private int level;//等级          
    //方法定义
    public int getHP(){
        return HP;
    }
    public int getMP(){
        return MP;
    }
    public int getAttack(){
        return attack;
    }
    public int getDefense(){
        return defense;
    }
    public String getName(){
        return name;
    }
    public int getLevel(){
        return level;
    }
    public int getMaxHP(){
        return maxHP;
    }
    public int getMaxMP(){
        return maxMP;
    }
//预留调接口
    public void setHP(int HP) { this.HP = HP; }
    public void setMP(int MP) { this.MP = MP; }
    public void setAttack(int attack) { this.attack = attack; }
    public void setDefense(int defense) { this.defense = defense; }
    public void setLevel(int level) { this.level = level; }
    public void setMaxHP(int maxHP) { this.maxHP = maxHP; }
    public void setMaxMP(int maxMP) { this.maxMP = maxMP; }


    public Character(String name, int maxHP, int maxMP, int attack, int defense, int level) {
        this.name = name;
        this.maxHP = maxHP;
        this.HP = maxHP; // 初始HP等于MaxHP
        this.maxMP = maxMP;
        this.MP = maxMP; // 初始MP等于MaxMP
        this.attack = attack;
        this.defense = defense;
        this.level = level;
    }

    //公共方法
    public void takeDamage(int damage){
        int actualDamage = damage - defense;
        if(actualDamage <= 0){
            actualDamage = 1;
        }
        HP -= actualDamage;
    }

    public boolean isAlive(){
        return HP > 0;
    }
    public abstract void displayStatus();

    
}