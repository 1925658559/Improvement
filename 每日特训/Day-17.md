# Day 17: 岛屿最大面积（DFS）+ AOP 代理机制

## 🔥 算法热身

### 题目：LeetCode 695 - 岛屿的最大面积 (Max Area of Island)
**链接**: https://leetcode.cn/problems/max-area-of-island/
**状态**: ✅ 通过
**代码位置**: `代码练习/homework/MaxAreaOfIsland.java`

**题目描述**:
给你一个大小为 `m x n` 的二进制矩阵 `grid`，岛屿是由一些相邻的 `1`（土地）构成的组合（水平或竖直方向相邻）。返回 `grid` 中最大的岛屿面积，没有岛屿则返回 `0`。

**示例**:
```
输入：grid = [
  [0,0,1,0,0,0,0,1,0,0,0,0,0],
  [0,0,0,0,0,0,0,1,1,1,0,0,0],
  [0,1,1,0,1,0,0,0,0,0,0,0,0],
  [0,1,0,0,1,1,0,0,1,0,1,0,0],
  [0,1,0,0,1,1,0,0,1,1,1,0,0],
  [0,0,0,0,0,0,0,0,0,0,1,0,0],
  [0,0,0,0,0,0,0,1,1,1,0,0,0],
  [0,0,0,0,0,0,0,1,1,0,0,0,0]
]
输出：6
```

---

### 解法一：DFS（深度优先搜索）✅ 推荐

**核心思想**：
- 与 LeetCode 200（岛屿数量）同一框架：遍历 + DFS 沉岛标记
- 关键改动：DFS 函数**返回面积值**而非 void
- `return 1 + dfs(上) + dfs(下) + dfs(左) + dfs(右)`，1 代表当前格子
- 主函数用 `Math.max()` 记录最大面积

**完整代码**：
```java
public class MaxAreaOfIsland {

    public int maxAreaOfIsland(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int maxArea = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == 1) {
                    maxArea = Math.max(maxArea, dfs(grid, row, col));
                }
            }
        }

        return maxArea;
    }

    private int dfs(int[][] grid, int row, int col) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[row].length || grid[row][col] == 0) {
            return 0;
        }

        grid[row][col] = 0;

        return 1 + dfs(grid, row + 1, col) + dfs(grid, row - 1, col) + dfs(grid, row, col + 1) + dfs(grid, row, col - 1);
    }
}
```

**复杂度分析**：
| | 时间复杂度 | 空间复杂度 |
|------|-----------|-----------|
| DFS | O(m × n) | O(m × n)（递归栈最坏） |

### 解法二：BFS（广度优先搜索）

**核心思想**：
- 用队列代替递归栈，遇到 `1` 入队并标记
- 每次出队 +1 计数，将相邻的 `1` 入队
- 队列清空时得到当前岛屿面积

```java
public int maxAreaOfIsland_BFS(int[][] grid) {
    int maxArea = 0;
    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};

    for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[i][j] == 1) {
                int area = 0;
                Queue<int[]> queue = new LinkedList<>();
                queue.offer(new int[]{i, j});
                grid[i][j] = 0;
                while (!queue.isEmpty()) {
                    int[] cur = queue.poll();
                    area++;
                    for (int[] d : dirs) {
                        int nr = cur[0] + d[0], nc = cur[1] + d[1];
                        if (nr >= 0 && nr < grid.length && nc >= 0 && nc < grid[0].length && grid[nr][nc] == 1) {
                            grid[nr][nc] = 0;
                            queue.offer(new int[]{nr, nc});
                        }
                    }
                }
                maxArea = Math.max(maxArea, area);
            }
        }
    }
    return maxArea;
}
```

### 两种解法对比

| | DFS | BFS |
|------|-----|-----|
| 时间复杂度 | O(m×n) | O(m×n) |
| 空间复杂度 | O(m×n) 递归栈 | O(min(m,n)) 队列 |
| 代码简洁度 | 更简洁 | 稍复杂 |
| 栈溢出风险 | 有（超大网格） | 无 |
| 适用场景 | 一般情况 | 网格极大时更安全 |

### 与 LeetCode 200 对比

| | LeetCode 200 岛屿数量 | LeetCode 695 最大面积 |
|------|----------------------|---------------------|
| 目标 | 计数（count++） | 求最大值（Math.max） |
| DFS 返回值 | void | int（面积） |
| 核心改动 | 遇到 1 就 count++ | DFS 返回 1 + 四方向 |

### 常见错误

1. **混淆 char[][] 和 int[][]**：LeetCode 200 是 `char '1'`，695 是 `int 1`，比较符号不同
2. **忘记沉岛标记**：不标记会导致同一个格子被重复计算，面积无限大
3. **返回值忘记 +1**：`return dfs(上)+dfs(下)+dfs(左)+dfs(右)` 少了当前格子的 1
4. **边界条件不完整**：漏掉 `grid[row][col] == 0` 的判断，导致水域也被计入面积

---

## 🧠 核心技术：AOP 代理机制

### Why：为什么需要 AOP？

**问题场景**：30 个方法都要加日志、权限、事务——直接写导致：
1. **重复代码**：同样逻辑 copy 30 遍，改日志格式要改 30 处
2. **职责混乱**：业务代码和横切逻辑搅在一起

**解决方案**：AOP 把 **Cross-Cutting Concerns（横切关注点）** 抽到一个地方统一管理

```
         UserService    OrderService    PayService
              │              │              │
  日志 ───────┼──────────────┼──────────────┼──── 横切
              │              │              │
  事务 ───────┼──────────────┼──────────────┼──── 横切
              │              │              │
  权限 ───────┼──────────────┼──────────────┼──── 横切
              │              │              │
```

### How：底层原理 —— 动态代理

AOP 底层通过 **Proxy（代理模式）** 实现，运行时生成代理对象，方法调用经过代理时在前后插入横切逻辑。

```
调用方  →  代理对象（加日志/权限）  →  真实对象（业务逻辑）
```

#### 两种代理方式对比

| | JDK 动态代理 | CGLIB 代理 |
|------|-------------|-----------|
| **机制** | 实现相同接口 | 继承目标类（生成子类） |
| **前提** | 目标类必须有接口 | 目标类不能是 final |
| **性能** | 创建快，调用稍慢 | 创建慢，调用快 |
| **Spring Boot 默认** | — | ✅ 默认使用 CGLIB |

**Spring Boot 2.x 默认配置**：
```properties
spring.aop.proxy-target-class=true  # 统一用 CGLIB，无论是否有接口
```

**原因**：如果用 JDK 代理，注入时必须用接口类型；用 CGLIB 则用实现类注入也不会报错。

#### JDK 动态代理原理图

```
                    ┌──────────────────────────┐
                    │      UserService         │
                    │      (接口)              │
                    └──────────┬───────────────┘
                               │ implements
              ┌────────────────┼────────────────┐
              │                │                │
   ┌──────────▼──────┐  ┌─────▼────────────┐   │
   │  $Proxy0        │  │ UserServiceImpl  │   │
   │  (代理对象)      │  │ (真实对象)        │   │
   │                  │  └──────────────────┘   │
   │ invoke() {       │           ▲              │
   │   前置日志        │           │              │
   │   method.invoke──┼───────────┘              │
   │   后置日志        │     反射调用             │
   │ }                │                          │
   └──────────────────┘                          │
```

#### Proxy.newProxyInstance() 三个参数

| 参数 | 类型 | 作用 |
|------|------|------|
| ClassLoader | `ClassLoader` | 定义代理类由谁来加载 |
| Interfaces | `Class<?>[]` | 告诉 JVM 代理对象要实现哪些接口 |
| InvocationHandler | `InvocationHandler` | 定义方法被调用时执行什么逻辑 |

**代码位置**: `代码练习/homework/JdkProxyDemo.java`

### AOP 四大核心术语

| 术语 | 含义 | 类比 |
|------|------|------|
| **Aspect（切面）** | 封装横切逻辑的类（日志、事务、权限） | LogInvocationHandler 整个类 |
| **Advice（通知）** | 切面在某个时机执行的具体动作 | invoke() 里的前后日志逻辑 |
| **Pointcut（切入点）** | 实际选择拦截的方法（表达式筛选） | "对哪些方法生效"的规则 |
| **JoinPoint（连接点）** | 所有可以被拦截的点（每一个方法） | 被代理的具体方法 |

**Pointcut vs JoinPoint**：JoinPoint 是全校所有学生，Pointcut 是你筛选出来参加比赛的那批人。

### 5 种 Advice 类型

| 注解 | 含义 | 执行时机 |
|------|------|---------|
| `@Before` | 前置通知 | 方法执行**前** |
| `@After` | 后置通知 | 方法执行**后**（无论成功失败） |
| `@AfterReturning` | 返回通知 | 方法**正常返回**后 |
| `@AfterThrowing` | 异常通知 | 方法**抛异常**后 |
| `@Around` | 环绕通知 | 方法前后都能控制，**最强大** |

### @Around 的独有能力（vs @Before + @After）

`@Around` 通过 `proceed()` 方法实现：
1. **控制目标方法是否执行**（权限拦截、熔断）
2. **修改传入参数**（调用前改 args）
3. **修改返回值**（调用后改 result）

`@Before` 和 `@After` 只能"旁观"，**无法干预方法执行流程**。

### 面试 Q&A（可直接背诵）

**Q1: Spring AOP 底层用了哪两种代理？**
> JDK 动态代理（目标类有接口）和 CGLIB（目标类无接口，通过继承生成子类）。Spring Boot 2.x 默认 `proxy-target-class=true`，统一用 CGLIB。

**Q2: JDK 动态代理 vs CGLIB 的区别？**
> JDK 基于接口，用 `Proxy.newProxyInstance()` + `InvocationHandler`；CGLIB 基于继承，生成目标类的子类。CGLIB 不能代理 final 类/方法。

**Q3: AOP 的核心概念有哪些？**
> Aspect（切面，封装横切逻辑）、Advice（通知，具体执行的动作）、Pointcut（切入点，筛选拦截的方法）、JoinPoint（连接点，所有可拦截的点）。

**Q4: @Around 和 @Before+@After 的区别？**
> @Around 通过 proceed() 可以控制目标方法是否执行、修改入参和返回值；@Before/@After 只能在前后做附加操作，无法干预执行流程。

**Q5: 什么是 Cross-Cutting Concerns？**
> 横切关注点，指横跨多个模块的通用逻辑（日志、事务、权限、性能统计），AOP 就是为了解决这类问题，把它们抽到切面统一管理，避免代码重复和职责混乱。

### 常见误区

1. **误以为 Spring AOP 能拦截所有方法**：Spring AOP 基于代理，只能拦截 Spring Bean 的方法。同一个类内部方法调用（this.method()）不会走代理，切面不生效。
2. **误以为 JDK 代理性能一定差**：JDK 代理创建快，CGLIB 创建慢但调用快。短生命周期对象用 JDK 可能更好。
3. **混淆 Pointcut 和 JoinPoint**：JoinPoint 是所有可拦截的点，Pointcut 是从中筛选出来的子集。

---

## ⚖️ 今日总结

### 技能评估

| 技能点 | 状态 | 备注 |
|--------|------|------|
| DFS 沉岛模式 | ✅ Proficient | 从 200 迁移到 695，返回值型 DFS |
| AOP 底层原理 | ✅ Proficient | 动态代理、JDK vs CGLIB |
| AOP 核心术语 | ✅ Proficient | Aspect/Advice/Pointcut/JoinPoint |
| 手写 JDK 动态代理 | ✅ Proficient | Proxy.newProxyInstance 三参数 |

### 🎉 Phase 2 完成！

Phase 2 (Day 11-20) 技能点已全部覆盖：
- ✅ MySQL 事务隔离级别 + B+树索引 + MVCC + 索引失效 + EXPLAIN
- ✅ Redis 数据结构 + 跳表 + RDB/AOF + 缓存三灾
- ✅ Spring Bean 生命周期 + AOP 代理机制

### 技术债务
- [x] ~~AOP 代理机制~~ — 已完成
- [ ] Spring Bean 完整生命周期 — PostConstruct、InitializingBean、BeanPostProcessor 待深入
- [ ] AOP 同类内部调用不走代理的解决方案（`AopContext.currentProxy()`）

### 明日计划
- **算法**: DFS/BFS 专项继续
- **技术**: Phase 2 复盘 + Phase 3 启动准备（Netty NIO & Reactor 预习）
