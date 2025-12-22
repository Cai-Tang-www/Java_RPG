# 这是伟大帝国理工悲催学生的 

#  JAVA 大作业项目之RPG打怪游戏

### **贡献者** : **Cai_Tang  mzj001  盛夏的她  cyxx**

**开发环境**：JDK 1.8+ / IntelliJ IDEA /  Trae

## 要求可见于 java_project.pdf 中



##### 本项目已上传至www.github.com/Cai-Tang-www/Java-RPG     权限为公开

有完整的上传历史和哈希确保不是ai一次性的应付（）

可能确实简陋些但能确保ai率小于20%并且都学习并理解

那么接下来请看ai的README.md     (诚实orz)



## 1. 项目简介 (Introduction)

本项目是一款基于 **Java Swing** 开发的沉浸式 2D 角色扮演游戏（RPG）。玩家将扮演年轻的魔法师 `cyxxx`，肩负拯救世界的使命。游戏包含完整的剧情线，玩家需要在村庄、森林和洞穴等多个场景中自由探索，与 NPC 对话获取线索，在回合制战斗中击败怪物（如史莱姆、哥布林）以及最终 Boss 哥斯拉，最终让世界重见光明。

## 2. 核心功能 (Features)

- **多场景地图系统**：支持多个地图（Village, Forest, Cave）的无缝切换。地图包含障碍物、传送点、NPC 交互区和 Boss 刷新点。
- **策略回合制战斗**：
  - 玩家拥有普通攻击、小技能（日之呼吸）、大招（爆裂魔法）等多种攻击手段。
  - 怪物拥有简单的 AI，Boss“哥斯拉”在低血量时会进入“红温（狂暴）”状态。
- **完善的 UI 交互**：
  - **主菜单**：支持新游戏、读取存档、音量调节。
  - **战斗界面**：实时显示 HP/MP 条，支持战斗中打开背包使用道具。
- **双重持久化存储**：
  - **背包系统**：支持从文本文件读取初始配置，并能动态增删道具。
  - **全局存档**：利用序列化技术保存玩家当前坐标、属性、背包状态和所在地图。
- **NPC 与事件系统**：地图上分布着 NPC，交互可触发随机对话；特定图块会触发随机遇敌或 Boss 战。

## 3. 系统架构与实现逻辑 (Architecture & Implementation)

### 3.1 核心引擎与地图 (Game Engine & Map)

- **`GameMap.java`** (核心控制器):
  - **地图渲染**: 使用 `Map<String, String[][]>` 存储不同场景的二维数组地形。通过 `addGameMap()` 方法根据当前坐标动态重绘界面（玩家显示为 `^`，墙壁为 `#`）。
  - **移动逻辑**: 监听 `WASD` 键盘事件，处理碰撞检测（不能穿过 `#`）。
  - **场景切换**: 当玩家击败特定 Boss 或达到特定条件时，修改 `mapName` 并重置坐标以切换场景。
  - **事件触发**: 移动时检测地形符号，`N` 触发 NPC 对话，`.` 随机遇敌，`!` 触发 Boss 战。

### 3.2 角色体系 (Character System)

- **`Character.java` (抽象基类)**: 定义了 HP, MP, Attack, Defense 等通用属性。
- **`Player.java` & `Magician.java`**:
  - 实现了经验值系统 (`gainExp`) 和升级逻辑 (`levelUp`)，升级后自动提升属性上限。
  - 战斗技能通过委托模式调用 `BattleEngine` 进行计算。
- **`Enemy.java` & 子类**:
  - **`Monster`**: 普通怪物，包含随机逃跑机制 (`flee`)。
  - **`Godzila` (Boss)**: 覆写 `actInBattle`，当 HP < 30% 时有概率触发双倍伤害的狂暴攻击。

### 3.3 数据持久化 (Data Persistence)

- **`GameSaver.java` & `SaveData.java`**:
  - 利用 `ObjectOutputStream` 将 `SaveData` 对象（包含 Player 对象、Inventory 对象、XY坐标、地图名）序列化到 `savegame.dat` 文件中，实现完整的游戏进度保存。
- **`Inventory.java`**:
  - **混合存储**: 构造时通过 `Scanner` 读取 `Inventory.txt` 初始化道具；同时实现了 `Serializable` 接口，随全局存档一起被序列化保存。
  - **回调机制**: 使用 `combatUIupdateCallback` 接口，确保在战斗中使用道具后，能即时刷新战斗窗口的 UI 数值。

### 3.4 交互界面 (User Interface)

- **`GameMenu.java`**:
  - 使用 `SwingUtilities.invokeLater` 确保线程安全。
  - 实现了自定义图片按钮（`createImageButton`），并集成了背景音乐播放器 (`AudioPlayer`)。

------

## 4. 团队合作目标 (Teamwork Objectives)

作为四人小组，我们在项目中采用了模块化分工，旨在模拟真实的软件开发流程：



1. **分层架构与解耦**:
   - **成员 A (UI & 交互)**: 负责 `GameMenu`、NPC 对话逻辑及图片/音频资源整合。目标是提升用户体验。
   - **成员 B (核心逻辑)**: 负责 `GameMap` 的移动算法、地图数组设计及场景切换逻辑。目标是构建稳定的游戏循环。
   - **成员 C (战斗系统)**: 负责 `Character` 继承体系、怪物 AI (`Godzila`) 及 `BattleEngine` 数值计算。目标是实现面向对象的多态性。
   - **成员 D (数据管理)**: 负责 `Inventory` 背包系统及 `GameSaver` 存档机制。目标是掌握 Java I/O 流与序列化。
2. **接口契约**: 团队约定了 `Item` 接口和 `Character` 抽象类，确保负责战斗的成员和负责背包的成员可以并行开发，互不阻塞。
3. **代码规范与集成**: 统一包路径 (`main.java.com.javarpg`)，定期进行代码合并，解决冲突，确保各模块在 `GameApp` 入口处能正常协同工作。

------

## 5. 学习结果与收获 (Learning Outcomes)

通过本项目的开发，团队成员在以下方面取得了显著进步：

- **深入理解面向对象 (OOP)**:
  - 通过 `Item` 接口（实现类 `HP`, `MP`）和 `Character` 继承树，深刻体会了**多态**和**封装**在减少代码冗余方面的作用。
- **Java Swing 图形编程**:
  - 掌握了 `JFrame`, `JPanel`, `JDialog` 的使用，以及如何通过 `KeyListener` 处理实时用户输入。
  - 解决了 Swing 单线程模型下的 UI 刷新问题（使用 `revalidate/repaint`）。
- **数据流与异常处理**:
  - 熟练运用 `File I/O` 读写配置文件，并掌握了通过 `Serialization` 保存复杂对象图（Object Graph）的高级技巧。
  - 在加载资源（图片/音频/存档）时处理了 `IOException` 和 `ClassNotFoundException`，提升了程序的健壮性。
- **复杂逻辑处理**:
  - 实现了基于二维数组的地图碰撞检测算法。
  - 设计了基于概率的随机遇敌和怪物 AI 逻辑。

------

## 6. 如何运行 (How to Run)

1. 确保已安装 JDK 8 或更高版本。

2. 确保项目根目录下存在 `Inventory.txt` 配置文件以及 `images/` 和 `music/` 资源文件夹。

3. 编译并运行入口文件：

   Bash

   ```
   javac -d out src/main/java/com/javarpg/*.java
   java -cp out main.java.com.javarpg.GameApp
   ```

   *(注：或直接在 IDEA / Eclipse 中运行 `GameApp.main()`)*

 燃尽了  晚安

目前在  **Week 4  **

​                                                                                                                                      **----Cai-Tang-www**
