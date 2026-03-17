# Day 10: 二叉树层序遍历 (BFS)

## 🔥 算法热身

**题目**: LeetCode 102 - 二叉树的层序遍历
**链接**: https://leetcode.cn/problems/binary-tree-level-order-traversal/
**状态**: ✅ Pass
**代码位置**: `代码练习/homework/BinaryTreeLevelOrder.java`

**题目描述**:
给你二叉树的根节点 `root`，返回其节点值的**层序遍历**（即逐层地，从左到右访问所有节点）。

**示例**:
```
输入：root = [3,9,20,null,null,15,7]

       3
      / \
     9  20
       /  \
      15   7

输出：[[3], [9,20], [15,7]]
```

---

## 📝 算法解法详解

### 核心概念：BFS vs DFS

**DFS（深度优先遍历）**：
- 一直往深处走，走到底再回头
- 前序、中序、后序遍历都是 DFS
- 使用**栈**或**递归**实现

**BFS（广度优先遍历）**：
- 一层一层地遍历，先访问完当前层，再访问下一层
- 层序遍历就是 BFS
- 使用**队列**实现

---

### 解法：BFS + 队列

**核心思想**：
1. 使用队列实现"先进先出"（FIFO）
2. 每次处理一层前，先记录当前队列大小（当前层的节点数）
3. 处理完当前层后，队列中就是下一层的所有节点

**完整代码**：
```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) {
        return result;
    }

    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);  // 将根节点加入队列

    while (!queue.isEmpty()) {
        // 关键：记录当前层的节点数量
        int levelSize = queue.size();
        List<Integer> currentLevel = new ArrayList<>();

        // 处理当前层的所有节点
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();  // 取出队首节点
            currentLevel.add(node.val);    // 访问节点

            // 将子节点加入队列（为下一层做准备）
            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }

        // 将当前层的结果加入最终结果
        result.add(currentLevel);
    }

    return result;
}
```

**复杂度分析**:
- 时间复杂度: O(n) - 每个节点访问一次
- 空间复杂度: O(n) - 队列最多存储一层的节点（最坏情况：完全二叉树的最后一层有 n/2 个节点）

---

### 🔍 执行过程演示

**给定树**：
```
       3
      / \
     9  20
       /  \
      15   7
```

**执行过程**：

| 步骤 | 队列状态 | levelSize | 操作 | currentLevel | result |
|------|---------|-----------|------|--------------|--------|
| 初始 | [3] | - | 根节点入队 | - | [] |
| **第1层** | [3] | 1 | 开始处理第1层 | [] | [] |
| 1.1 | [] | - | 取出3，加入9,20 | [3] | [] |
| 1.2 | [9,20] | - | 第1层处理完毕 | [3] | [[3]] |
| **第2层** | [9,20] | 2 | 开始处理第2层 | [] | [[3]] |
| 2.1 | [20] | - | 取出9，无子节点 | [9] | [[3]] |
| 2.2 | [15,7] | - | 取出20，加入15,7 | [9,20] | [[3]] |
| 2.3 | [15,7] | - | 第2层处理完毕 | [9,20] | [[3],[9,20]] |
| **第3层** | [15,7] | 2 | 开始处理第3层 | [] | [[3],[9,20]] |
| 3.1 | [7] | - | 取出15，无子节点 | [15] | [[3],[9,20]] |
| 3.2 | [] | - | 取出7，无子节点 | [15,7] | [[3],[9,20]] |
| 3.3 | [] | - | 第3层处理完毕 | [15,7] | [[3],[9,20],[15,7]] |
| 结束 | [] | - | 队列为空 | - | [[3],[9,20],[15,7]] |

---

### ⚠️ 常见错误

**1. 忘记记录层的大小**
```java
// ❌ 错误代码：无法区分不同层
while (!queue.isEmpty()) {
    TreeNode node = queue.poll();
    result.add(node.val);  // 所有节点混在一起
    // ...
}

// ✅ 正确代码：先记录当前层大小
while (!queue.isEmpty()) {
    int levelSize = queue.size();  // 关键！
    List<Integer> currentLevel = new ArrayList<>();
    for (int i = 0; i < levelSize; i++) {
        // 处理当前层
    }
}
```

**2. 在循环内部调用 queue.size()**
```java
// ❌ 错误代码：size 会动态变化
for (int i = 0; i < queue.size(); i++) {  // queue.size() 在变化！
    TreeNode node = queue.poll();
    if (node.left != null) queue.offer(node.left);  // size 增加了
}

// ✅ 正确代码：先保存 size
int levelSize = queue.size();  // 固定值
for (int i = 0; i < levelSize; i++) {
    // ...
}
```

**3. 使用栈代替队列**
```java
// ❌ 错误：栈是"后进先出"，不适合层序遍历
Stack<TreeNode> stack = new Stack<>();  // 会导致顺序错误

// ✅ 正确：队列是"先进先出"
Queue<TreeNode> queue = new LinkedList<>();
```

---

## 🎯 今日技能评估

| 技能点 | 状态 | 说明 |
|--------|------|------|
| LeetCode 102 (层序遍历) | ✅ Proficient | 理解 BFS + 队列实现 |
| BFS vs DFS | ✅ Proficient | 理解两种遍历方式的区别 |
| 队列的应用 | ✅ Proficient | 掌握队列的"先进先出"特性 |

---

## 🚀 明日计划 (Day 10 续)

**注意**：Day 10 的学习尚未完成，还需要继续：

1. **MySQL MVCC 原理**：
   - 版本链（Undo Log）
   - ReadView 机制
   - 可重复读的实现

2. **Phase 1 复盘**：
   - 口述检验：HashMap、AQS、JVM

---

## 🧠 核心技术：MySQL MVCC 原理

### 一、什么是 MVCC？

**MVCC（Multi-Version Concurrency Control，多版本并发控制）**：
- InnoDB 实现事务隔离级别的核心机制
- 通过保存数据的多个版本，让不同事务看到不同的数据快照
- 解决"不可重复读"问题，实现"可重复读"隔离级别

---

### 二、版本链（Undo Log）

#### 2.1 核心概念

MySQL 通过 **Undo Log（回滚日志）** 保存数据的历史版本，形成**版本链**。

**每条记录的隐藏字段**：
- **trx_id**：最后修改这条记录的事务ID
- **roll_ptr**：回滚指针，指向 Undo Log 中的上一个版本

#### 2.2 版本链示意图

```
假设一条记录的变化历史：
1. 初始值：age = 20 (trx_id=99)
2. 事务101修改：age = 25
3. 事务102修改：age = 30

版本链：
┌─────────────────────────────────────────────────────────┐
│                                                         │
│  当前版本（最新，存储在表中）                            │
│  ┌──────────────────┐                                   │
│  │ age = 30         │                                   │
│  │ trx_id = 102     │ ← 事务102修改的                   │
│  │ roll_ptr ────────┼─┐                                 │
│  └──────────────────┘ │                                 │
│                       │                                 │
│  Undo Log（存储在回滚段中）                              │
│                       ↓                                 │
│  ┌──────────────────┐                                   │
│  │ age = 25         │                                   │
│  │ trx_id = 101     │ ← 事务101修改的                   │
│  │ roll_ptr ────────┼─┐                                 │
│  └──────────────────┘ │                                 │
│                       ↓                                 │
│  ┌──────────────────┐                                   │
│  │ age = 20         │                                   │
│  │ trx_id = 99      │ ← 初始版本                        │
│  │ roll_ptr = NULL  │                                   │
│  └──────────────────┘                                   │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

### 三、ReadView（读视图）

#### 3.1 核心概念

**ReadView**：事务在第一次执行查询时创建的快照，记录当前数据库的状态。

**ReadView 包含的信息**：
```
┌─────────────────────────────────────────────────────────┐
│                    ReadView 结构                         │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  m_ids: [101, 103, 105]  ← 当前活跃的事务ID列表         │
│         （这些事务还未提交）                             │
│                                                         │
│  min_trx_id: 101  ← 最小的活跃事务ID                    │
│                                                         │
│  max_trx_id: 106  ← 下一个要分配的事务ID                │
│                                                         │
│  creator_trx_id: 100  ← 创建这个ReadView的事务ID        │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

#### 3.2 可见性判断规则

当事务查询数据时，会遍历版本链，根据 ReadView 判断每个版本是否可见：

```
判断版本的 trx_id：

1. 如果 trx_id < min_trx_id
   → 说明这个版本是在所有活跃事务之前提交的
   → ✅ 可见

2. 如果 trx_id >= max_trx_id
   → 说明这个版本是在 ReadView 创建之后才开始的
   → ❌ 不可见

3. 如果 trx_id 在 m_ids 中
   → 说明这个版本是未提交的事务修改的
   → ❌ 不可见（除非是自己修改的）

4. 如果 min_trx_id <= trx_id < max_trx_id，且不在 m_ids 中
   → 说明这个版本是已提交的事务修改的
   → ✅ 可见
```

---

### 四、完整示例

#### 4.1 场景描述

```sql
-- T1: 初始数据
INSERT INTO user (id, age) VALUES (1, 20);  -- trx_id=99

-- T2: 事务A开始
BEGIN;  -- trx_id=100

-- T3: 事务B开始，修改但未提交
BEGIN;  -- trx_id=101
UPDATE user SET age = 25 WHERE id = 1;
-- 未提交

-- T4: 事务C开始，修改并提交
BEGIN;  -- trx_id=102
UPDATE user SET age = 30 WHERE id = 1;
COMMIT;

-- T5: 事务D开始，修改但未提交
BEGIN;  -- trx_id=103
UPDATE user SET age = 35 WHERE id = 1;
-- 未提交

-- T6: 事务A第一次查询（创建ReadView）
SELECT * FROM user WHERE id = 1;  -- 应该看到什么？
```

#### 4.2 事务A的ReadView

```
创建时间：T6
m_ids: [100, 101, 103]  ← 未提交的事务（包括自己）
min_trx_id: 100
max_trx_id: 104         ← 下一个要分配的ID
creator_trx_id: 100
```

#### 4.3 版本链状态

```
age=35, trx_id=103, roll_ptr → 下一个版本
  ↓
age=30, trx_id=102, roll_ptr → 下一个版本
  ↓
age=25, trx_id=101, roll_ptr → 下一个版本
  ↓
age=20, trx_id=99, roll_ptr=NULL
```

#### 4.4 可见性判断过程

| 版本 | trx_id | 判断逻辑 | 结果 |
|------|--------|----------|------|
| age=35 | 103 | 103 在 m_ids 中 | ❌ 不可见（未提交） |
| age=30 | 102 | 100 ≤ 102 < 104，且不在 m_ids 中 | ✅ 可见（已提交） |

**结果**：事务A 看到 **age=30**

---

### 五、可重复读的实现

#### 5.1 核心机制

**可重复读（REPEATABLE READ）**：
- 事务在第一次查询时创建 ReadView
- 后续所有查询都使用同一个 ReadView
- 保证事务内多次查询看到的数据一致

#### 5.2 示例

```sql
-- 事务A
BEGIN;  -- trx_id=100
SELECT * FROM user WHERE id = 1;  -- 创建ReadView，看到age=30

-- 此时事务B提交了新的修改
-- 事务B: UPDATE user SET age = 40 WHERE id = 1; COMMIT;

SELECT * FROM user WHERE id = 1;  -- 仍然使用之前的ReadView，看到age=30
COMMIT;
```

**关键点**：
- 事务A的 ReadView 在第一次查询时就固定了
- 即使事务B后续提交了修改，事务A仍然看到旧版本
- 这就是"可重复读"

---

### 六、RC vs RR 的区别

| 隔离级别 | ReadView 创建时机 | 特点 |
|---------|------------------|------|
| **READ COMMITTED (RC)** | 每次查询都创建新的 ReadView | 可以读到其他事务已提交的修改 |
| **REPEATABLE READ (RR)** | 第一次查询时创建，后续复用 | 事务内多次查询结果一致 |

**示例对比**：
```sql
-- 事务A (RC隔离级别)
BEGIN;
SELECT * FROM user WHERE id = 1;  -- 创建ReadView1，看到age=20

-- 事务B提交修改
UPDATE user SET age = 25 WHERE id = 1; COMMIT;

SELECT * FROM user WHERE id = 1;  -- 创建ReadView2，看到age=25 ✅

-- 事务A (RR隔离级别)
BEGIN;
SELECT * FROM user WHERE id = 1;  -- 创建ReadView，看到age=20

-- 事务B提交修改
UPDATE user SET age = 25 WHERE id = 1; COMMIT;

SELECT * FROM user WHERE id = 1;  -- 复用ReadView，看到age=20 ✅
```

---

### 七、常见面试题

#### Q1: 什么是MVCC？它解决了什么问题？

**标准答案**：
MVCC（多版本并发控制）是 InnoDB 实现事务隔离级别的核心机制。通过保存数据的多个版本（Undo Log），让不同事务根据自己的 ReadView 看到不同的数据快照，从而实现"可重复读"隔离级别，解决"不可重复读"问题。

---

#### Q2: MVCC 是如何实现可重复读的？

**标准答案**：
1. **版本链**：通过 Undo Log 保存数据的历史版本
2. **ReadView**：事务第一次查询时创建快照，记录当前活跃事务列表
3. **可见性判断**：根据 ReadView 判断版本链上哪些版本可见
4. **复用 ReadView**：事务内所有查询都使用同一个 ReadView，保证数据一致性

---

#### Q3: ReadView 包含哪些信息？

**标准答案**：
- **m_ids**：当前活跃的事务ID列表（未提交的事务）
- **min_trx_id**：最小的活跃事务ID
- **max_trx_id**：下一个要分配的事务ID
- **creator_trx_id**：创建这个 ReadView 的事务ID

---

#### Q4: RC 和 RR 隔离级别在 MVCC 实现上有什么区别？

**标准答案**：
- **RC（读已提交）**：每次查询都创建新的 ReadView，可以读到其他事务已提交的修改
- **RR（可重复读）**：第一次查询时创建 ReadView，后续查询复用，保证事务内多次查询结果一致

---

#### Q5: 为什么需要版本链？直接保存多个版本不行吗？

**标准答案**：
版本链通过 roll_ptr 指针连接，只需要在 Undo Log 中保存历史版本，当前版本存储在表中。这样设计的优势：
1. 节约存储空间（历史版本可以定期清理）
2. 查询效率高（先查当前版本，不可见再回溯）
3. 支持事务回滚（通过 Undo Log 恢复）

---

### 八、常见误区

#### ❌ 误区1：MVCC 会为每个事务复制一份完整数据

```
正确理解：
MVCC 不是为每个事务复制数据，而是通过版本链保存历史版本。
不同事务根据 ReadView 在版本链中选择可见的版本。
```

---

#### ❌ 误区2：MVCC 可以完全避免锁

```
正确理解：
MVCC 只解决读-写冲突，不解决写-写冲突。
写操作仍然需要加锁（行锁、Gap锁等）。
```

---

#### ❌ 误区3：ReadView 在事务开始时就创建

```
正确理解：
ReadView 在第一次执行查询时才创建，不是事务开始时。
这样可以避免长事务占用过多资源。
```

---

#### ❌ 误区4：所有隔离级别都使用 MVCC

```
正确理解：
- READ UNCOMMITTED：不使用 MVCC（直接读最新版本）
- READ COMMITTED：使用 MVCC（每次查询创建 ReadView）
- REPEATABLE READ：使用 MVCC（复用 ReadView）
- SERIALIZABLE：不使用 MVCC（使用锁）
```

---


## 🎯 Phase 1 复盘：HashMap

### 复盘问题：HashMap 的底层实现原理

**核心知识点**：

#### 1. 底层数据结构
- **JDK 1.7**：数组 + 链表
- **JDK 1.8**：数组 + 链表 + 红黑树

#### 2. 哈希冲突解决
- 使用**链表法**：哈希值相同的 key 存储在同一个链表下
- **JDK 1.8 优化**：当链表过长时，转换成红黑树

#### 3. 链表 → 红黑树转换条件
- 链表长度 ≥ 8
- **且** 数组长度 ≥ 64
- 如果数组长度 < 64，优先扩容而不是转红黑树

#### 4. 时间复杂度对比

| 操作 | 数组直接访问 | 链表查找 | 红黑树查找 |
|------|-------------|---------|-----------|
| **时间复杂度** | O(1) | O(n) | O(log n) |

#### 5. JDK 1.7 vs 1.8 对比

| 特性 | JDK 1.7 | JDK 1.8 |
|------|---------|---------|
| **数据结构** | 数组 + 链表 | 数组 + 链表 + 红黑树 |
| **哈希冲突** | 链表法 | 链表法 + 红黑树优化 |
| **最坏查找** | O(n) | O(log n) |
| **插入方式** | 头插法 | 尾插法 |

---

## 🎯 今日技能评估

| 技能点 | 状态 | 说明 |
|--------|------|------|
| LeetCode 102 (层序遍历) | ✅ Proficient | 理解 BFS + 队列实现 |
| MySQL MVCC 原理 | ✅ Proficient | 理解版本链、ReadView、可见性判断 |
| 可重复读实现 | ✅ Proficient | 理解 RC vs RR 的区别 |
| HashMap 原理 | ✅ Proficient | 理解 JDK 1.7/1.8 区别、红黑树优化 |

---

## 🎉 Phase 1 完成！

**已掌握的核心技能**：
- ✅ HashMap 1.7/1.8 源码
- ✅ ConcurrentHashMap 锁机制
- ✅ Synchronized 锁升级
- ✅ Volatile 内存语义
- ✅ ThreadPoolExecutor 7参数
- ✅ AQS 源码 (ReentrantLock)
- ✅ CAS & ABA 问题
- ✅ JVM 内存模型
- ✅ GC 算法 (CMS/G1)
- ✅ 类加载双亲委派
- ✅ MySQL MVCC 原理

**算法进度**：10/50 题 ✅

---

## 🚀 明日计划 (Day 11)

正式进入 Phase 2 - Middleware 深挖：

1. **MySQL 事务隔离级别**：
   - 脏读、不可重复读、幻读
   - 四种隔离级别的区别
   - 如何解决幻读

2. **算法**：LeetCode 46 - 全排列（回溯算法）

---
