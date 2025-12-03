public class Magician extends Role{
    public void a(){
        System.out.println("使用了普攻");
        this.MP=this.MP+1;
    }
    public void smailSkill(){
        System.out.println("使用了小技能");
        this.MP=this.MP-2;
    }

    public void bigSkill(){
        System.out.println("使用了大招");
        this.MP=this.MP-4;
    }
}
