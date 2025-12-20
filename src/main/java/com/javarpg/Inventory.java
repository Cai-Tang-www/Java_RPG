package main.java.com.javarpg;
import java.util.*;
import java.util.List;
import java.io.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;


public class Inventory{
    private List<Item> items = new ArrayList<>();

    private Runnable combatUIupdateCallback;

    private JFrame inventoryFrame;
    private File inventoryFile=new File("./main/resources/Inventory.txt");
    public Inventory() {
        try(Scanner scanner=new Scanner(inventoryFile)){
            while(scanner.hasNextLine()){
                String line=scanner.nextLine();
                System.out.println("处理的行: " + line);
                String[] parts=line.split(",");
                String ItemName=parts[0];
                int Itemcount=Integer.parseInt(parts[1]);
                if(ItemName.equals("HP")){
                    items.add(new HP(ItemName,Itemcount));
                }
                else if(ItemName.equals("MP")){
                    items.add(new MP(ItemName,Itemcount));
                }
            }
        }catch(IOException e){
            System.out.println("文件未找到");
        }
    }

    public void saveInventory(){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter(inventoryFile))){
            for(Item item:items){
                writer.write(item.getName()+","+item.getCount()+"\n");
            }
        }catch(IOException e){
            System.out.println("文件未找到");
        }
    }
    private int findItem(String itemName){
        for(int i=0;i<items.size();i++){
            if(items.get(i).getName().equals(itemName)){
                return i;
            }
        }
        return -1;
    }
    public void addItem(String itemName,int count) {
        if(itemName.equals("HP")){
            int index=findItem(itemName);
            if(index==-1){
                items.add(new HP(itemName,count));
            }
            else{
                items.get(index).setCount(count);
            }
        }
        else if(itemName.equals("MP")){
            int index=findItem(itemName);
            if(index==-1){
                items.add(new MP(itemName,count));
            }
            else{
                items.get(index).setCount(count);
            }
        }
    }
    
    public void useItem(Character user,String Itemname) {
        int index=findItem(Itemname);
        if(index!=-1){
            items.get(index).use(user);
            if(items.get(index).getCount().equals(0)){
                items.remove(index);
            }
        }
        else{
            System.out.println("道具未获得");
        }
    }
    public void displayItems() {
        System.out.println("背包道具如下:");
        for(int i=0;i<items.size();i++){
            System.out.println(items.get(i).getName()+"*"+items.get(i).getCount());
        }
    }

    //接受回调方法
    public void setCombatUIupdateCallback(Runnable callback){
        this.combatUIupdateCallback=callback;
    }


    //背包窗口
    public void showInventory(Magician user,JFrame Parent){
        inventoryFrame=new JFrame("背包道具栏(按esc退出)");
        inventoryFrame.setSize(300,200);
        inventoryFrame.setLocationRelativeTo(Parent);
        inventoryFrame.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        inventoryFrame.setAlwaysOnTop(true);
        inventoryFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        inventoryFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // 窗口关闭后，将焦点返回给父窗口
                if (Parent != null) {
                    Parent.requestFocusInWindow();
                    Parent.toFront();  // 将父窗口带到前面
                }
            }
        });
        
        inventoryFrame.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ESCAPE){
                    inventoryFrame.dispose();
                }
            }
        });
        //获取窗口的焦点，确保键盘事件能够被正确捕获
        inventoryFrame.setFocusable(true);
        inventoryFrame.requestFocusInWindow();
        //绘制背包窗口
        repaintInventory(user);
        inventoryFrame.setVisible(true);
        

        
    }


    public void repaintInventory(Magician user){
        inventoryFrame.getContentPane().removeAll();
        
        for(Item item:items){
            JButton itemButton=new JButton(item.getName()+"*"+item.getCount());
            if (!item.getCount().equals(0)){
                itemButton.addActionListener(e->FrameUse(user,item));
                inventoryFrame.getContentPane().add(itemButton);
            }
        }
        inventoryFrame.getContentPane().validate();
        inventoryFrame.getContentPane().repaint();



    }

    public void FrameUse(Magician user,Item item){
        item.use(user);
        if(combatUIupdateCallback!=null){

            combatUIupdateCallback.run();
            System.out.println("回调结束");
        }
        repaintInventory(user);
    }


}