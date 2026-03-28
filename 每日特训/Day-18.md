# Day 18: 腐烂的橘子（多源 BFS）+ AOP 深度补强

## 🔥 算法热身

### 题目：LeetCode 994 - 腐烂的橘子 (Rotting Oranges)
**链接**: https://leetcode.cn/problems/rotting-oranges/
**状态**: ✅ 通过
**代码位置**: `代码练习/homework/RottingOranges.java`

**题目描述**:
在一个 `m x n` 的网格中，每个格子有三种值：
- `0` — 空格
- `1` — 新鲜橘子
- `2` — 腐烂橘子

每一分钟，腐烂橘子会把上下左右相邻的新鲜橘子感染。返回所有新鲜橘子都变腐烂的**最少分钟数**，如果不可能全部腐烂返回 `-1`。

**示例**:
```
输入：grid = [
  [2, 1, 1],
  [1, 1, 0],
  [0, 1, 1]
]
输出：4

过程：
第0分钟：        第1分钟：        第2分钟：        第3分钟：        第4分钟：
[2, 1, 1]      [2, 2, 1]      [2, 2, 2]      [2, 2, 2]      [2, 2, 2]
[1, 1, 0]      [2, 1, 0]      [2, 2, 0]      [2, 2, 0]      [2, 2, 0]
[0, 1, 1]      [0, 1, 1]      [0, 1, 1]      [0, 2, 1]      [0, 2, 2]
```

---

### 解法一：多源 BFS（Multi-source BFS）✅ 推荐

**核心思想**：
- 与岛屿题不同，本题所有腐烂橘子**同时**向外扩散，天然匹配 BFS 层序遍历
- **多源**：一次性把所有初始腐烂橘子（值为 2）全部入队作为起点
- BFS 每扩一层 = 1 分钟，用 `size = queue.size()` 控制层边界
- 用 `freshCount` 记录剩余新鲜橘子数，BFS 结束后若 > 0 则返回 -1

**完整代码**：
```java
public int orangesRotting(int[][] grid) {
    int rows = grid.length;
    int cols = grid[0].length;
    Queue<int[]> queue = new LinkedList<>();
    int freshCount = 0;

    // Step 1: 所有腐烂橘子入队，统计新鲜橘子数量
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            if (grid[i][j] == 2) {
                queue.offer(new int[]{i, j});
            } else if (grid[i][j] == 1) {
                freshCount++;
            }
        }
    }

    // Step 2: 没有新鲜橘子，直接返回 0
    if (freshCount == 0) {
        return 0;
    }

    int minutes = 0;
    int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

    // Step 3: 多源 BFS 层序遍历
    while (!queue.isEmpty()) {
        int size = queue.size();
        boolean infected = false;

        for (int k = 0; k < size; k++) {
            int[] cur = queue.poll();
            for (int[] d : dirs) {
                int nr = cur[0] + d[0];
                int nc = cur[1] + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                    grid[nr][nc] = 2;
                    freshCount--;
                    infected = true;
                    queue.offer(new int[]{nr, nc});
                }
            }
        }

        if (infected) {
            minutes++;
        }
    }

    // Step 4: 还有新鲜橘子则返回 -1
    return freshCount == 0 ? minutes : -1;
}
```

**复杂度分析**：
| | 时间复杂度 | 空间复杂度 |
|------|-----------|-----------|
| 多源 BFS | O(m × n) | O(m × n)（队列最坏情况） |

### 解法二：DFS（不推荐）

**为什么 DFS 不适合本题**：
- DFS 是一条路走到底，无法模拟"所有腐烂橘子同时扩散"的行为
- 若强行用 DFS，需要对每个腐烂橘子单独跑 DFS 并记录到达每个格子的最短时间，取最大值
- 实现复杂，且不如 BFS 直观

### 与岛屿系列对比

| | LeetCode 200 岛屿数量 | LeetCode 695 最大面积 | LeetCode 994 腐烂橘子 |
|------|----------------------|---------------------|---------------------|
| 目标 | 计数（count++） | 求最大值（Math.max） | 求最少时间（层数） |
| 搜索方式 | DFS/BFS 均可 | DFS/BFS 均可 | **多源 BFS** |
| 起点 | 遍历时遇到 1 | 遍历时遇到 1 | 所有初始的 2 |
| 核心区别 | 单源、不关心距离 | 单源、累加面积 | 多源、关心层数（时间） |

### 多源 BFS vs 单源 BFS

```
单源 BFS：                    多源 BFS：
队列初始 = [一个起点]          队列初始 = [所有起点]
     S                          S1    S2    S3
    /|\                         |      |      |
   1 1 1   ← 第1层             1  1   1  1   1  1  ← 第1层（同时扩散）
  /|   |\                      |      |      |
 2  2  2 2 ← 第2层             2  2   2  2   2  2  ← 第2层
```

### 常见错误

1. **忘记将新感染的橘子入队**：导致 BFS 无法继续扩散，只能感染第一层
2. **每个腐烂橘子单独跑 BFS**：这是单源思路，无法模拟"同时扩散"，且时间复杂度退化为 O(m²n²)
3. **没有 `infected` 标记直接 minutes++**：队列中可能有上一轮的腐烂橘子但周围没有新鲜橘子，这一层不应该计时
4. **忘记处理 freshCount == 0 的边界**：没有新鲜橘子时应直接返回 0，而非进入 BFS

### 修改原数组 vs visited 数组

| 方案 | 优点 | 缺点 |
|------|------|------|
| 修改原数组（grid[nr][nc] = 2） | 不需要额外空间 | 破坏输入数据 |
| boolean[][] visited | 保留原数据 | 多 O(m×n) 空间 |

面试时两种方案都要提到，说明 trade-off。

---

## 🧠 核心技术：AOP 深度补强

### Why：从场景出发理解 AOP

**原始问题**：20 个方法都要加耗时日志
```java
// ❌ 不用 AOP：每个方法都写一遍
public void createOrder(Order order) {
    long start = System.currentTimeMillis();
    // ... 业务逻辑 ...
    log.info("耗时: {}ms", System.currentTimeMillis() - start);
}
// payOrder、cancelOrder、refundOrder... 再重复 19 遍
```

**两大痛点**：
1. **代码重复** — 同样的耗时逻辑 copy 20 遍
2. **高耦合** — 改日志格式要改 20 处，业务代码和日志逻辑混在一起

**AOP 解决** = 只写一次耗时逻辑（Advice），告诉框架对哪些方法生效（Pointcut）

### How：AOP 代理创建的完整链路

```
Spring 容器启动
      │
      ▼
解析 @Aspect 类 → 提取 Pointcut + Advice 规则
      │
      ▼
创建 Bean（以 OrderService 为例）
      │
      ▼
属性注入 → init 方法执行（@PostConstruct 等）
      │
      ▼
BeanPostProcessor.postProcessAfterInitialization()
      │
      ▼
AbstractAutoProxyCreator 检查：这个 Bean 是否命中 Pointcut？
      │
      ├── 命中 → 创建代理对象（CGLIB），放入容器替换原始 Bean
      │
      └── 未命中 → 原始 Bean 直接放入容器
      │
      ▼
Controller @Autowired OrderService → 拿到的是代理对象
      │
      ▼
调用 orderService.createOrder() → 代理拦截 → Around Advice → proceed() → 目标方法
```

**关键点**：代理在 `postProcessAfterInitialization()` 阶段创建，因为必须等 Bean 完全初始化（属性注入 + init 方法）之后，才能用代理包装一个完整的对象。

### 同类内部调用不走代理（高频考点）

**问题代码**：
```java
public class OrderService {
    public void createOrder(Order order) {
        // ...
        this.sendNotification(order);  // ← 内部调用
    }

    @LogTime
    public void sendNotification(Order order) {
        // ...
    }
}
```

**`sendNotification` 上的切面不会生效！**

**根本原因**：

```
Controller → 代理对象.createOrder() → proceed() → 原始对象.createOrder()
                                                        │
                                                        └→ this.sendNotification()
                                                             ↑
                                                        this = 原始对象，不是代理！
```

`proceed()` 执行的是原始对象的方法，`this` 指向原始对象，内部调用直接走原始对象，代理对象根本不知道这次调用发生了。

### 两种解决方案

#### 方案一：AopContext.currentProxy()

```java
@EnableAspectJAutoProxy(exposeProxy = true)  // 必须开启

public void createOrder(Order order) {
    // ...
    ((OrderService) AopContext.currentProxy()).sendNotification(order);
}
```

**缺点**：业务代码侵入了 Spring AOP 框架 API，违背了 AOP"业务无感知"的设计初衷。

#### 方案二：抽到独立 Bean（推荐）

```java
@Service
public class OrderService {
    @Autowired
    private NotificationService notificationService;  // 注入的是代理对象

    public void createOrder(Order order) {
        // ...
        notificationService.sendNotification(order);  // 外部调用，走代理
    }
}

@Service
public class NotificationService {
    @LogTime
    public void sendNotification(Order order) {
        // ...
    }
}
```

**优点**：零侵入，纯靠 Spring 注入机制，业务代码不依赖任何框架 API。

### 面试 Q&A（可直接背诵）

**Q1: AOP 代理对象是在 Bean 生命周期的哪个阶段创建的？**
> 在 `BeanPostProcessor.postProcessAfterInitialization()` 阶段。AbstractAutoProxyCreator 检查 Bean 是否命中 Pointcut 规则，命中则用 CGLIB 生成代理对象替换原始 Bean。必须在初始化完成后才能代理，否则包装的是半成品。

**Q2: 为什么同类内部调用 AOP 不生效？**
> 因为 `proceed()` 执行的是原始对象的方法，方法内部 `this` 指向原始对象而非代理对象，所以 `this.xxx()` 是直接调用，不经过代理，切面无法拦截。

**Q3: 内部调用不走代理怎么解决？**
> 两种方案：① `AopContext.currentProxy()` 获取代理对象后调用，但会导致业务代码和框架耦合；② 将被调用方法抽到独立 Bean，通过注入调用（推荐），利用 Spring 注入的就是代理对象这一特性。

**Q4: 三级缓存和 AOP 代理有什么关系？**
> 正常情况下代理在 `postProcessAfterInitialization` 创建。但发生循环依赖时，三级缓存的 ObjectFactory 会提前调用 `getEarlyBeanReference()`，在 Bean 未完全初始化时就创建代理并暴露，以解决循环依赖。

**Q5: 为什么 postProcessAfterInitialization 而不是 Before？**
> 必须等 Bean 完全初始化好（属性注入完毕、init 方法执行完毕）再包装代理。如果在 Before 阶段创建代理，包装的是一个未初始化完的半成品对象。

### 常见误区

1. **误以为 AOP 代理是在 Bean 实例化时创建的**：实例化（new）和代理创建是两个阶段，中间还有属性注入和初始化。代理发生在最后的 BeanPostProcessor 阶段。
2. **误以为 `this` 指向代理对象**：在目标方法内部，`this` 永远是原始对象。只有通过 Spring 容器注入拿到的引用才是代理。
3. **误以为三级缓存就是为了创建 AOP 代理**：三级缓存是为了解决循环依赖。只是在循环依赖场景下，ObjectFactory 会顺便判断是否需要提前创建代理。

---

## ⚖️ 今日总结

### 技能评估

| 技能点 | 状态 | 备注 |
|--------|------|------|
| 多源 BFS | ✅ Proficient | 多起点同时入队，层序遍历计时 |
| AOP 代理创建时机 | ✅ Proficient | BeanPostProcessor → postProcessAfterInitialization |
| AOP 内部调用失效 | ✅ Proficient | this = 原始对象，两种解决方案 |
| 修改原数组 vs visited | ✅ Familiar | 两种方案 trade-off |

### 技术债务
- [x] ~~AOP 同类内部调用~~ — 已清除
- [ ] Spring Bean 完整生命周期 — PostConstruct、InitializingBean、BeanPostProcessor 待深入

### 明日计划
- **算法**: BFS 专项继续（如 LeetCode 542 01矩阵 或 LeetCode 127 单词接龙）
- **技术**: Phase 3 正式启动 — Netty NIO & Reactor 模型
