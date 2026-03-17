# Day 11: 回溯算法 + MySQL 事务隔离级别

## 🔥 算法热身

**题目**: LeetCode 46 - 全排列
**链接**: https://leetcode.cn/problems/permutations/
**状态**: ✅ Pass
**代码位置**: `代码练习/homework/Permutations.java`

**题目描述**:
给定一个不含重复数字的数组 `nums`，返回其所有可能的全排列。

**示例**:
```
输入：nums = [1,2,3]
输出：[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
```

---

## 📝 算法解法详解

### 核心思想：回溯算法（Backtracking）

**回溯算法三步曲**：
1. **做选择**：选择一个数字
2. **递归**：继续处理下一个位置
3. **撤销选择**：回溯，尝试其他选择

**类比**：就像走迷宫，走到死路就退回来，换一条路继续走。

---

### 解法：回溯 + 标记数组

**核心思想**：
1. 使用 `boolean[] used` 标记哪些数字已经被使用
2. 使用 `List<Integer> path` 存储当前正在构建的排列
3. 递归终止条件：`path.size() == nums.length`
4. 遍历所有数字，尝试每一个未使用的数字

**完整代码**：
```java
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    boolean[] used = new boolean[nums.length];
    List<Integer> path = new ArrayList<>();

    backtrack(nums, used, path, result);
    return result;
}

private void backtrack(int[] nums, boolean[] used,
                       List<Integer> path, List<List<Integer>> result) {
    // 1. 递归终止条件
    if (path.size() == nums.length) {
        result.add(new ArrayList<>(path));  // 必须创建新的 ArrayList！
        return;
    }

    // 2. 遍历所有数字，尝试每一个选择
    for (int i = 0; i < nums.length; i++) {
        // 3. 如果这个数字已经用过了，跳过
        if (used[i]) {
            continue;
        }

        // 4. 做选择
        path.add(nums[i]);
        used[i] = true;

        // 5. 递归
        backtrack(nums, used, path, result);

        // 6. 撤销选择（回溯）
        path.remove(path.size() - 1);
        used[i] = false;
    }
}
```

**复杂度分析**:
- 时间复杂度: **O(n × n!)** - n! 个排列，每个需要 O(n) 时间生成
- 空间复杂度: **O(n × n!)** - 结果集存储 n! 个排列，每个有 n 个元素
- 递归深度: O(n)
- 辅助空间: O(n) - used 数组 + path 列表

---

### 🔍 关键知识点

#### 1. 为什么要 `new ArrayList<>(path)`？

**错误代码**：
```java
result.add(path);  // ❌ 错误！
```

**问题**：
- `path` 是一个引用（指针）
- 如果直接 `add(path)`，`result` 中所有元素都指向同一个 `path` 对象
- 当递归结束时，`path` 被清空了
- 所以 `result` 中所有列表都会显示为空列表 `[]`

**正确代码**：
```java
result.add(new ArrayList<>(path));  // ✅ 正确！创建副本
```

**验证**：
```
输入: [1,2,3]
错误输出: [[], [], [], [], [], []]  // 所有列表都是空的
正确输出: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
```

---

#### 2. 回溯算法的本质

**回溯 = DFS + 撤销选择**

```
决策树：
                    []
          /         |         \
        [1]        [2]        [3]
       /   \      /   \      /   \
    [1,2] [1,3] [2,1] [2,3] [3,1] [3,2]
      |     |     |     |     |     |
   [1,2,3][1,3,2][2,1,3][2,3,1][3,1,2][3,2,1]
```

**遍历过程**：
1. 选择 1 → 选择 2 → 选择 3 → 得到 [1,2,3] → 回溯
2. 选择 1 → 选择 3 → 选择 2 → 得到 [1,3,2] → 回溯
3. 选择 2 → 选择 1 → 选择 3 → 得到 [2,1,3] → 回溯
4. ...

---

### ⚠️ 常见错误

**1. 忘记创建新的 ArrayList**
```java
// ❌ 错误
result.add(path);

// ✅ 正确
result.add(new ArrayList<>(path));
```

**2. 在循环内部调用 queue.size()**
```java
// ❌ 错误：size 会动态变化
for (int i = 0; i < used.length && !used[i]; i++) {
    // 逻辑错误
}

// ✅ 正确：先判断是否使用
if (used[i]) {
    continue;
}
```

**3. 忘记回溯（撤销选择）**
```java
// ❌ 错误：忘记撤销选择
path.add(nums[i]);
used[i] = true;
backtrack(nums, used, path, result);
// 忘记了 path.remove() 和 used[i] = false

// ✅ 正确：必须撤销选择
path.add(nums[i]);
used[i] = true;
backtrack(nums, used, path, result);
path.remove(path.size() - 1);  // 撤销
used[i] = false;                // 撤销
```

**4. 使用队列代替递归**
```java
// ❌ 错误：回溯算法是 DFS，不适合用队列（BFS）
Queue<Integer> queue = new LinkedList<>();

// ✅ 正确：使用递归（DFS）
backtrack(nums, used, path, result);
```

---

## 🧠 核心技术：MySQL 事务隔离级别

### 一、四种事务隔离级别

| 隔离级别 | 脏读 | 不可重复读 | 幻读 | ReadView 创建时机 | 默认数据库 |
|---------|------|-----------|------|------------------|-----------|
| **READ UNCOMMITTED**<br>（读未提交） | ❌ 可能 | ❌ 可能 | ❌ 可能 | 不使用 MVCC | 几乎不用 |
| **READ COMMITTED**<br>（读已提交） | ✅ 解决 | ❌ 可能 | ❌ 可能 | 每次查询创建 | Oracle、PostgreSQL |
| **REPEATABLE READ**<br>（可重复读） | ✅ 解决 | ✅ 解决 | ⚠️ 部分解决 | 第一次查询创建，后续复用 | **MySQL** |
| **SERIALIZABLE**<br>（串行化） | ✅ 解决 | ✅ 解决 | ✅ 解决 | 不使用 MVCC，完全串行化 | 性能最差 |

---

### 二、三种并发问题

#### 1. 脏读（Dirty Read）

**定义**：读到了其他事务**未提交**的数据（可能被回滚）

**示例**：
```sql
-- 事务A
BEGIN;
UPDATE user SET age = 25 WHERE id = 1;  -- 修改但未提交

-- 事务B
BEGIN;
SELECT age FROM user WHERE id = 1;  -- 读到 age=25（脏数据！）

-- 事务A
ROLLBACK;  -- 回滚了！age 实际上还是 20
```

**问题**：事务B 读到了 age=25，但事务A 回滚了，age 实际上还是 20。

---

#### 2. 不可重复读（Non-Repeatable Read）

**定义**：同一个事务中，**两次读取同一条数据**，结果不一致

**示例**：
```sql
-- 事务B
BEGIN;
SELECT age FROM user WHERE id = 1;  -- 第一次读：age=20

-- 事务A（在事务B的两次读取之间）
BEGIN;
UPDATE user SET age = 25 WHERE id = 1;
COMMIT;  -- 提交了

-- 事务B
SELECT age FROM user WHERE id = 1;  -- 第二次读：age=25（变了！）
COMMIT;
```

**问题**：事务B 在同一个事务中，两次读取 id=1 的数据，第一次是 20，第二次是 25。

---

#### 3. 幻读（Phantom Read）

**定义**：同一个事务中，**两次查询**，结果集的**行数**不一致

**示例**：
```sql
-- 事务B
BEGIN;
SELECT * FROM user WHERE age > 18;  -- 第一次查：1 条记录

-- 事务A（在事务B的两次查询之间）
BEGIN;
INSERT INTO user (id, name, age) VALUES (2, '李四', 22);
COMMIT;  -- 提交了

-- 事务B
SELECT * FROM user WHERE age > 18;  -- 第二次查：2 条记录（多了一条！）
COMMIT;
```

**问题**：事务B 在同一个事务中，两次查询，第一次 1 条记录，第二次 2 条记录。

---

### 三、MVCC 如何实现隔离级别

#### 1. READ COMMITTED（读已提交）

**机制**：
- **每次查询都创建新的 ReadView**
- ReadView 会记录当前活跃的事务（未提交的）

**效果**：
- ✅ **解决脏读**：读不到未提交的数据（因为 ReadView 会过滤掉活跃事务的修改）
- ❌ **不能解决不可重复读**：可以读到已提交的数据（因为每次查询都创建新的 ReadView）

**示例**：
```sql
-- 事务B (READ COMMITTED)
BEGIN;
SELECT age FROM user WHERE id = 1;
-- 创建 ReadView1，事务A在活跃列表中 → 看不到事务A的修改

-- 事务A
UPDATE user SET age = 25 WHERE id = 1;
COMMIT;  -- 提交了

-- 事务B
SELECT age FROM user WHERE id = 1;
-- 创建 ReadView2，事务A不在活跃列表中 → 可以看到事务A的修改
-- 结果：两次查询结果不同 ❌
```

---

#### 2. REPEATABLE READ（可重复读）

**机制**：
- **第一次查询时创建 ReadView，后续复用**
- 使用同一个 ReadView 进行可见性判断

**效果**：
- ✅ **解决脏读**：读不到未提交的数据
- ✅ **解决不可重复读**：事务内多次查询结果一致

**示例**：
```sql
-- 事务B (REPEATABLE READ)
BEGIN;
SELECT age FROM user WHERE id = 1;
-- 创建 ReadView，事务A在活跃列表中

-- 事务A
UPDATE user SET age = 25 WHERE id = 1;
COMMIT;  -- 提交了

-- 事务B
SELECT age FROM user WHERE id = 1;
-- 复用旧的 ReadView，事务A仍然在活跃列表中
-- 所以事务A的修改对事务B不可见
-- 结果：两次查询看到相同的数据 ✅
```

---

### 四、快照读 vs 当前读

#### 1. 快照读（Snapshot Read）

**定义**：普通的 `SELECT` 语句，使用 MVCC 机制

**特点**：
- 使用 ReadView 进行可见性判断
- 读取历史版本的数据
- ✅ 可以避免幻读

**示例**：
```sql
SELECT * FROM user WHERE age > 18;
```

---

#### 2. 当前读（Current Read）

**定义**：读取最新版本的数据，不使用 MVCC

**包括**：
- `SELECT ... FOR UPDATE`
- `SELECT ... LOCK IN SHARE MODE`
- `UPDATE`、`DELETE`、`INSERT`

**特点**：
- 读取最新版本的数据（已提交的）
- 不使用 MVCC
- ❌ 可能出现幻读

**示例**：
```sql
SELECT * FROM user WHERE age > 18 FOR UPDATE;
```

---

### 五、REPEATABLE READ 对幻读的"部分解决"

| 读取方式 | 是否使用 MVCC | 能否避免幻读 | 示例 SQL |
|---------|--------------|-------------|----------|
| **快照读** | ✅ 使用 | ✅ 可以避免 | `SELECT ...` |
| **当前读** | ❌ 不使用 | ❌ 可能出现 | `SELECT ... FOR UPDATE` |

**原因**：
- **快照读**：使用 MVCC 的 ReadView，会过滤掉在 ReadView 创建之后插入的记录
- **当前读**：读取最新版本的数据，不使用 MVCC，所以会看到新插入的记录

---

### 六、Next-Key Lock（间隙锁 + 行锁）

**定义**：MySQL 用来解决"当前读"幻读问题的锁机制

**工作原理**：
- 不仅锁定查询到的行（**行锁**）
- 还锁定这些行之间的**间隙**（**间隙锁**）
- 防止其他事务在这些间隙中插入新记录

**示例**：
```sql
-- 假设表中有记录：id=1, id=5, id=10

-- 事务B
BEGIN;
SELECT * FROM user WHERE id > 3 AND id < 8 FOR UPDATE;
-- 查询到：id=5
-- 锁定：
--   1. 行锁：id=5 这一行
--   2. 间隙锁：(3, 5) 和 (5, 8) 这两个间隙

-- 事务A
INSERT INTO user (id, name) VALUES (6, '李四');
-- ❌ 被阻塞！因为 id=6 在间隙 (5, 8) 中
```

**效果**：✅ 完全解决了幻读问题

---

### 七、常见面试题

#### Q1: MySQL 默认的事务隔离级别是什么？为什么选择这个级别？

**标准答案**：
MySQL 默认的事务隔离级别是 **REPEATABLE READ（可重复读）**。

**原因**：
1. **平衡性能和一致性**：比 SERIALIZABLE 性能好，比 READ COMMITTED 一致性强
2. **解决大部分并发问题**：解决脏读、不可重复读，通过 MVCC + Next-Key Lock 解决幻读
3. **历史原因**：MySQL 早期的主从复制基于 binlog 的 STATEMENT 格式，REPEATABLE READ 可以保证主从一致性

---

#### Q2: READ COMMITTED 和 REPEATABLE READ 在 MVCC 实现上有什么区别？

**标准答案**：
- **READ COMMITTED**：每次查询都创建新的 ReadView，可以读到其他事务已提交的修改
- **REPEATABLE READ**：第一次查询时创建 ReadView，后续查询复用，保证事务内多次查询结果一致

---

#### Q3: 什么是幻读？REPEATABLE READ 能完全解决幻读吗？

**标准答案**：
**幻读**：同一个事务中，两次查询，结果集的行数不一致。

**REPEATABLE READ 部分解决幻读**：
- **快照读**（普通 SELECT）：使用 MVCC，可以避免幻读 ✅
- **当前读**（SELECT ... FOR UPDATE）：不使用 MVCC，需要 Next-Key Lock 避免幻读 ⚠️

---

#### Q4: 什么是 Next-Key Lock？它如何解决幻读问题？

**标准答案**：
**Next-Key Lock = 行锁 + 间隙锁**

**工作原理**：
- 锁定查询到的行（行锁）
- 锁定这些行之间的间隙（间隙锁）
- 防止其他事务在间隙中插入新记录

**效果**：完全解决"当前读"的幻读问题。

---

#### Q5: 快照读和当前读有什么区别？分别在什么场景下使用？

**标准答案**：

| 特性 | 快照读 | 当前读 |
|------|--------|--------|
| **SQL** | `SELECT ...` | `SELECT ... FOR UPDATE` |
| **MVCC** | 使用 | 不使用 |
| **读取数据** | 历史版本 | 最新版本 |
| **加锁** | 不加锁 | 加锁 |
| **场景** | 普通查询 | 需要修改数据前先查询 |

---

### 八、常见误区

#### ❌ 误区1：REPEATABLE READ 完全解决了幻读

```
正确理解：
REPEATABLE READ 只在"快照读"时可以避免幻读。
"当前读"仍然需要 Next-Key Lock 来解决幻读。
```

---

#### ❌ 误区2：READ COMMITTED 不能解决脏读

```
正确理解：
READ COMMITTED 可以解决脏读（读不到未提交的数据）。
但不能解决不可重复读（可以读到已提交的修改）。
```

---

#### ❌ 误区3：隔离级别越高越好

```
正确理解：
隔离级别越高，一致性越强，但性能越差。
需要根据业务场景选择合适的隔离级别。
- 金融系统：SERIALIZABLE
- 普通业务：REPEATABLE READ 或 READ COMMITTED
```

---

#### ❌ 误区4：MVCC 可以完全避免锁

```
正确理解：
MVCC 只解决读-写冲突，不解决写-写冲突。
写操作仍然需要加锁（行锁、Gap锁等）。
```

---

## ⚖️ 今日总结

### 掌握的技能

| 技能点 | 状态 | 说明 |
|--------|------|---------|
| LeetCode 46 (全排列) | ✅ Proficient | 理解回溯算法、标记数组、引用传递 |
| 回溯算法框架 | ✅ Proficient | 做选择 → 递归 → 撤销选择 |
| 时间复杂度分析 | ✅ Proficient | O(n × n!) |
| MySQL 四种隔离级别 | ✅ Proficient | RU/RC/RR/SERIALIZABLE |
| 三种并发问题 | ✅ Proficient | 脏读/不可重复读/幻读 |
| MVCC 实现原理 | ✅ Proficient | ReadView 创建时机、可见性判断 |
| 快照读 vs 当前读 | ✅ Proficient | MVCC vs 最新版本 |
| Next-Key Lock | ✅ Proficient | 行锁 + 间隙锁 |

### 技术债务

- [ ] **无** — 当前无待解决的技术债务

---

## 🚀 明日计划 (Day 12)

继续 Phase 2 - Middleware 深挖：

1. **MySQL 索引原理**：
   - B+树 vs B树
   - 聚簇索引 vs 非聚簇索引
   - 索引失效场景

2. **算法**：LeetCode 78 - 子集（回溯算法进阶）

---
