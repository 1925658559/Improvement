# Day 16: 岛屿数量（DFS）+ Spring Bean 生命周期 & 三级缓存

## 🔥 算法热身

### 题目：LeetCode 200 - 岛屿数量 (Number of Islands)
**链接**: https://leetcode.cn/problems/number-of-islands/
**状态**: ✅ 通过
**代码位置**: `代码练习/homework/NumberOfIslands.java`

**题目描述**:
给你一个由 `'1'`（陆地）和 `'0'`（水）组成的二维网格，请你计算网格中岛屿的数量。
岛屿总是被水包围，并且每座岛屿只能由水平方向和/或竖直方向上相邻的陆地连接形成。

**示例**:
```
输入：grid = [
  ["1","1","0","0","0"],
  ["1","1","0","0","0"],
  ["0","0","1","0","0"],
  ["0","0","0","1","1"]
]
输出：3
```

---

### 解法一：DFS（深度优先搜索）✅ 推荐

**核心思想**：
- 遍历整个网格，遇到 `'1'` 时岛屿数量+1
- 通过 DFS 将当前岛屿所有相连的 `'1'` 标记为 `'0'`，避免重复计数
- 递归访问上下左右四个方向

**完整代码**：
```java
public class NumberOfIslands {

    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int count = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == '1') {
                    count++;
                    dfs(grid, row, col);
                }
            }
        }
        return count;
    }

    private void dfs(char[][] grid, int row, int col) {
        // 递归终止条件：越界 或 遇到'0'
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[row].length || grid[row][col] == '0') {
            return;
        }

        // 标记当前位置为'0'（已访问）
        grid[row][col] = '0';

        // 递归四个方向
        dfs(grid, row + 1, col);  // 下
        dfs(grid, row - 1, col);  // 上
        dfs(grid, row, col + 1);  // 右
        dfs(grid, row, col - 1);  // 左
    }
}
```

**遍历过程演示**（以示例为例）：

```
初始状态:              遇到(0,0)='1', count=1, DFS标记:
1 1 0 0 0             0 0 0 0 0
1 1 0 0 0     →       0 0 0 0 0
0 0 1 0 0             0 0 1 0 0
0 0 0 1 1             0 0 0 1 1

遇到(2,2)='1', count=2:     遇到(3,3)='1', count=3:
0 0 0 0 0                    0 0 0 0 0
0 0 0 0 0                    0 0 0 0 0
0 0 0 0 0             →      0 0 0 0 0
0 0 0 1 1                    0 0 0 0 0

最终 count = 3
```

**复杂度分析**：
- 时间复杂度：O(m × n)，每个格子最多访问 1 次（访问后标记为'0'不会再访问）
- 空间复杂度：O(m × n)，递归调用栈最坏深度（整个网格都是'1'的蛇形路径）

---

### 解法二：BFS（广度优先搜索）

**核心思想**：
- 遍历网格，遇到 `'1'` 时岛屿数量+1
- 用**队列**存储当前岛屿的坐标，逐层扩展
- 每次从队列中取出一个坐标，检查上下左右四个方向

**完整代码**：
```java
import java.util.LinkedList;
import java.util.Queue;

public class NumberOfIslandsBFS {

    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int count = 0;
        int rows = grid.length;
        int cols = grid[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    grid[i][j] = '0';  // 标记为已访问
                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[]{i, j});

                    while (!queue.isEmpty()) {
                        int[] curr = queue.poll();
                        int row = curr[0], col = curr[1];
                        // 上下左右四个方向
                        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
                        for (int[] dir : dirs) {
                            int newRow = row + dir[0];
                            int newCol = col + dir[1];
                            if (newRow >= 0 && newRow < rows
                                && newCol >= 0 && newCol < cols
                                && grid[newRow][newCol] == '1') {
                                grid[newRow][newCol] = '0';
                                queue.offer(new int[]{newRow, newCol});
                            }
                        }
                    }
                }
            }
        }
        return count;
    }
}
```

**复杂度分析**：
- 时间复杂度：O(m × n)
- 空间复杂度：O(min(m, n))，队列最多存储边界元素

---

### 两种解法对比

| 对比项 | DFS（递归） | BFS（队列） |
|--------|-----------|-----------|
| 时间复杂度 | O(m × n) | O(m × n) |
| 空间复杂度 | O(m × n) 递归栈 | O(min(m, n)) 队列 |
| 代码量 | 少（递归简洁） | 多（需维护队列） |
| 适用场景 | 网格不太大时 | 网格很大、担心栈溢出时 |
| 实现难度 | ⭐ 简单 | ⭐⭐ 中等 |

---

### 常见错误

**错误1**：DFS中先递归再标记，导致无限递归
```java
// ❌ 错误：先递归再标记
private void dfs(char[][] grid, int row, int col) {
    dfs(grid, row + 1, col);  // A→B→A→B... 无限递归！
    grid[row][col] = '0';     // 标记太晚了
}

// ✅ 正确：进入DFS后立即标记
private void dfs(char[][] grid, int row, int col) {
    grid[row][col] = '0';     // 先标记
    dfs(grid, row + 1, col);  // 再递归
}
```

**错误2**：忘记检查边界条件
```java
// ❌ 错误：没有检查越界
private void dfs(char[][] grid, int row, int col) {
    if (grid[row][col] == '0') return;  // ArrayIndexOutOfBoundsException!
}

// ✅ 正确：先检查越界，再检查值
private void dfs(char[][] grid, int row, int col) {
    if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length || grid[row][col] == '0') {
        return;
    }
}
```

**错误3**：使用额外的 boolean 数组但忘记标记
```java
// ❌ 错误：忘记在BFS入队时就标记visited
if (grid[newRow][newCol] == '1') {
    queue.offer(new int[]{newRow, newCol});
    // 忘记 visited[newRow][newCol] = true → 同一个点会被重复入队
}
```

---

## 🧠 核心技术：Spring Bean 生命周期 & 三级缓存

### 1. Spring 容器与 Bean 基础概念

**传统方式 vs Spring 方式**：

```
传统方式：自己 new 对象
┌──────────────────────────────────┐
│ class UserController {           │
│   UserService s = new UserService(); ← 到处 new，改实现要改 N 处
│ }                                │
└──────────────────────────────────┘

Spring 方式：容器统一管理
┌──────────────────────────────────┐
│ class UserController {           │
│   @Autowired                     │
│   UserService s;  ← 容器自动注入，换实现只改配置
│ }                                │
└──────────────────────────────────┘
```

**核心概念**：
- **Spring 容器** = 对象工厂/管理者，负责创建和管理所有对象
- **Bean** = Spring 容器创建和管理的对象

---

### 2. 循环依赖问题

**场景**：A 依赖 B，B 依赖 A

```java
@Component
class A {
    @Autowired
    private B b;
}

@Component
class B {
    @Autowired
    private A a;
}
```

**问题推演**：
```
创建A → 需要B → 创建B → 需要A → 又去创建A → ...  死循环！
```

**解决核心思想**：**先创建对象，后注入属性**

```
T1: new A()           ← 对象已存在（属性为null）
T2: 提前暴露"半成品A"
T3: 注入属性b → 发现需要B → 去创建B
T4: new B()
T5: B注入属性a → 从缓存拿到"半成品A" ← 引用！
T6: B创建完成
T7: 回到A，注入B → A创建完成
T8: B持有A的引用，A后续属性变化B能看到 ← Java引用特性
```

**关键点**：B持有的是A的**引用**（内存地址），不是拷贝。A后续属性注入完成后，B通过引用看到的是完整的A。

---

### 3. 三级缓存机制

**为什么需要三级缓存？**

如果只有两级缓存（完整Bean + 半成品Bean），当Bean需要AOP代理时会有问题：

```
正常流程：代理在Bean完全初始化后才创建
循环依赖：B在A初始化前就需要A的引用

矛盾：B应该拿到代理A，但代理A还没创建！
```

**三级缓存各自职责**：

```
┌─────────────────────────────────────────────────────────┐
│ 一级缓存 singletonObjects                               │
│ 存放：完全初始化好的Bean（可直接使用）                      │
│ 时机：Bean创建全部完成后放入                               │
├─────────────────────────────────────────────────────────┤
│ 二级缓存 earlySingletonObjects                           │
│ 存放：半成品Bean（已实例化，未注入属性）                     │
│ 来源：三级缓存的工厂被调用后，结果放在这里                   │
├─────────────────────────────────────────────────────────┤
│ 三级缓存 singletonFactories                              │
│ 存放：ObjectFactory 工厂                                 │
│ 作用：按需决定返回原始对象还是代理对象                       │
└─────────────────────────────────────────────────────────┘
```

**完整流程（A和B循环依赖 + A需要AOP代理）**：

```
Step 1: 创建A
  new A() → 把A的工厂放入 [三级缓存]

Step 2: A注入属性 → 发现需要B → 去创建B

Step 3: 创建B
  new B() → 把B的工厂放入 [三级缓存]

Step 4: B注入属性 → 发现需要A → 查缓存
  [一级]? ❌ 没有
  [二级]? ❌ 没有
  [三级]? ✅ 找到A的工厂!

Step 5: 调用A的工厂
  A需要代理? → 提前创建代理A → 返回代理A
  A不需要代理? → 返回原始A
  结果放入 [二级缓存]，删除 [三级缓存] 中的工厂

Step 6: B拿到A的引用 → B初始化完成 → 放入 [一级缓存]

Step 7: 回到A → 注入B → A初始化完成 → 放入 [一级缓存]
```

**没有循环依赖时**：Bean 直接走 创建→注入→初始化→放入一级缓存，三级缓存不会被触发。

---

### 面试 Q&A（5题）

**Q1: 什么是Spring容器？什么是Bean？**
> Spring容器是一个对象工厂/管理者，负责创建和管理应用中的对象。Bean就是由Spring容器创建和管理的对象。通过容器统一管理对象，可以实现依赖注入，避免到处 new 对象，降低耦合。

**Q2: Spring如何解决循环依赖？**
> 通过"先创建对象，后注入属性"的方式。new出对象后，提前暴露半成品引用到缓存中。当另一个Bean需要它时，从缓存中拿到引用。由于Java的引用特性，后续属性注入完成后，持有引用的地方都能看到完整对象。

**Q3: 为什么需要三级缓存？两级不够吗？**
> 两级缓存可以解决简单的循环依赖，但如果Bean需要AOP代理，就不够了。正常流程中代理在初始化后才创建，但循环依赖要求提前暴露。三级缓存存放ObjectFactory工厂，可以在需要时按需判断是否创建代理对象，保证其他Bean拿到的是正确的代理对象。

**Q4: 构造器注入能解决循环依赖吗？**
> 不能。三级缓存的前提是"先创建对象，后注入属性"（setter注入/字段注入）。构造器注入时，对象还没有被new出来，无法提前暴露引用，所以Spring会抛出 `BeanCurrentlyInCreationException`。

**Q5: 三级缓存中各级分别存什么？**
> - 一级缓存（singletonObjects）：完全初始化好的Bean
> - 二级缓存（earlySingletonObjects）：半成品Bean（提前暴露的引用）
> - 三级缓存（singletonFactories）：ObjectFactory工厂，按需决定返回原始对象还是代理对象

---

### 常见误区

**误区1**：三级缓存是为了提高查找速度
> ❌ 错误。三个Map的查找速度都是O(1)。三级缓存是为了解决AOP代理在循环依赖中的提前创建问题。

**误区2**：所有循环依赖都能被Spring解决
> ❌ 错误。构造器注入的循环依赖无法解决，因为对象还没有new出来，无法提前暴露引用。

**误区3**：没有循环依赖也需要三级缓存
> ❌ 错误。没有循环依赖时，Bean直接创建完成后放入一级缓存，二级和三级缓存不会被使用。

---

## ⚖️ 今日总结

### 技能评估

| 技能点 | 状态 | 备注 |
|--------|------|------|
| DFS 遍历二维网格 | ✅ Proficient | 递归解法、标记已访问、四方向遍历 |
| BFS 层序遍历应用 | ✅ Familiar | 队列实现、空间更优 |
| Spring 容器/Bean 概念 | ✅ Familiar | 对象工厂/管理者、依赖注入 |
| 三级缓存解决循环依赖 | ✅ Familiar | 先创建后注入、提前暴露引用、AOP代理 |

### 技术债务
- [ ] Spring Bean 完整生命周期（PostConstruct、InitializingBean、BeanPostProcessor 等）待深入
- [ ] AOP 代理机制（JDK vs CGLIB）待学习

### 明日计划 (Day-17)
- **算法**: DFS/BFS 专项（岛屿类变体题）
- **技术**: Spring AOP 代理机制（JDK 动态代理 vs CGLIB）
