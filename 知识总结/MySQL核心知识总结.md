# MySQL 核心知识总结 (Day 8-10)

> Phase 2 MySQL 核心知识点速查手册

---

## 1. 索引原理

### B+树 vs B树
| 特性 | B树 | B+树 |
|------|-----|------|
| 数据存储 | 所有节点 | 仅叶子节点 |
| 叶子节点链表 | 无 | 有 |
| 范围查询 | 需中序遍历 | 直接遍历链表 ✅ |
| 查询稳定度 | 不稳定 | 稳定 ✅ |

### 为什么选择B+树？
- 树高更低（减少磁盘I/O）
- 支持范围查询
- 查询性能稳定

---

## 2. 索引类型

### 聚簇索引 vs 非聚簇索引

**聚簇索引（主键索引）**：
- 叶子节点存储：完整的行数据
- 一张表只能有一个

**非聚簇索引（二级索引）**：
- 叶子节点存储：索引列值 + 主键值
- 一张表可以有多个
- 查询时可能需要"回表"

### 回表
通过非聚簇索引查询时，先找到主键值，再通过主键索引查找完整数据

### 覆盖索引
查询的所有字段都包含在索引中，无需回表
- 性能提升约50%

---

## 3. 索引失效场景

### 6大失效场景

**1. 对索引列进行计算/函数操作**
```sql
❌ WHERE age + 1 = 26
✅ WHERE age = 25
```

**2. LIKE以通配符开头**
```sql
❌ WHERE name LIKE '%张%'
✅ WHERE name LIKE '张%'
```

**3. 类型转换**
```sql
-- phone是VARCHAR类型
❌ WHERE phone = 13800138000
✅ WHERE phone = '13800138000'
```

**4. 使用OR（部分字段无索引）**
```sql
❌ WHERE age = 25 OR name = '张三'  -- name无索引
✅ WHERE age = 25 OR age = 30
```

**5. 使用 != 或 NOT IN**
```sql
❌ WHERE age != 25
✅ WHERE age > 25 OR age < 25
```

**6. 联合索引不满足最左前缀**
```sql
-- 索引：INDEX(a, b, c)
❌ WHERE b = 2
✅ WHERE a = 1 AND b = 2
```

---

## 4. 事务隔离级别

### 四个标准级别（从低到高）

| 隔离级别 | 中文名 | 脏读 | 不可重复读 | 幻读 |
|---------|--------|------|-----------|------|
| **READ UNCOMMITTED** | 读未提交 | ✅ 可能 | ✅ 可能 | ✅ 可能 |
| **READ COMMITTED** | 读已提交 | ❌ 不可能 | ✅ 可能 | ✅ 可能 |
| **REPEATABLE READ** | 可重复读 | ❌ 不可能 | ❌ 不可能 | ✅ 可能 |
| **SERIALIZABLE** | 串行化 | ❌ 不可能 | ❌ 不可能 | ❌ 不可能 |

### 各级别详解

**读未提交（Read Uncommitted）**
- 最低级别，事务可以看到其他事务**未提交**的修改
- 会出现脏读（读取到未提交的数据）

**读已提交（Read Committed）**
- 只能看到其他事务**已提交**的修改
- Oracle 默认级别
- 会出现不可重复读（同一事务内多次读取结果不同）

**可重复读（Repeatable Read）**
- 事务期间多次读取数据**结果一致**
- MySQL InnoDB 默认级别
- 会出现幻读（新增/删除记录时）

**串行化（Serializable）**
- 最高级别，事务顺序执行
- 完全避免所有并发问题，但性能最差

### 常见问题

**Q: MySQL 默认隔离级别是？**
A: REPEATABLE READ（可重复读）

**Q: 脏读、不可重复读、幻读的区别？**
- 脏读：读到其他事务未提交的数据
- 不可重复读：同一事务内两次读取结果不同（数据被修改）
- 幻读：同一事务内两次查询结果不同（记录数变多/少）

### 实战示例

**示例1：脏读（READ UNCOMMITTED）**
```sql
-- 初始数据：account表，id=1, balance=100

-- 事务A（读未提交）
SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;
BEGIN;
SELECT balance FROM account WHERE id=1;  -- 读到 100

-- 事务B（在另一个会话）
BEGIN;
UPDATE account SET balance=200 WHERE id=1;  -- 修改但未提交

-- 事务A 再次查询
SELECT balance FROM account WHERE id=1;  -- 读到 200 ❌ 脏读！

-- 事务B 回滚
ROLLBACK;

-- 事务A 再次查询
SELECT balance FROM account WHERE id=1;  -- 读到 100（事务B回滚了）
COMMIT;
```
**问题**：事务A读到了事务B未提交的数据（200），但事务B最终回滚了。

---

**示例2：不可重复读（READ COMMITTED）**
```sql
-- 初始数据：account表，id=1, balance=100

-- 事务A（读已提交）
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;
BEGIN;
SELECT balance FROM account WHERE id=1;  -- 第1次读：100

-- 事务B（在另一个会话）
BEGIN;
UPDATE account SET balance=200 WHERE id=1;
COMMIT;  -- 提交

-- 事务A 再次查询
SELECT balance FROM account WHERE id=1;  -- 第2次读：200 ❌ 不可重复读！
COMMIT;
```
**问题**：事务A在同一个事务内，两次读取的结果不同（100 → 200）。

---

**示例3：可重复读（REPEATABLE READ）**
```sql
-- 初始数据：account表，id=1, balance=100

-- 事务A（可重复读 - MySQL默认）
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
BEGIN;
SELECT balance FROM account WHERE id=1;  -- 第1次读：100

-- 事务B（在另一个会话）
BEGIN;
UPDATE account SET balance=200 WHERE id=1;
COMMIT;  -- 提交

-- 事务A 再次查询
SELECT balance FROM account WHERE id=1;  -- 第2次读：100 ✅ 可重复读！
COMMIT;
```
**结果**：事务A在整个事务期间，多次读取的结果一致（都是100）。

---

**示例4：幻读（REPEATABLE READ）**
```sql
-- 初始数据：account表，id=1和id=2两条记录

-- 事务A（可重复读）
SET SESSION TRANSACTION ISOLATION LEVEL REPEATABLE READ;
BEGIN;
SELECT COUNT(*) FROM account;  -- 第1次读：2条记录

-- 事务B（在另一个会话）
BEGIN;
INSERT INTO account (id, balance) VALUES (3, 300);
COMMIT;  -- 提交

-- 事务A 再次查询
SELECT COUNT(*) FROM account;  -- 第2次读：2条记录 ✅ 没有幻读

-- 但是如果事务A尝试插入
INSERT INTO account (id, balance) VALUES (3, 300);
-- ❌ 报错：Duplicate entry '3'（幻读现象）
COMMIT;
```
**说明**：MySQL的RR级别通过MVCC + Next-Key Lock基本解决了幻读，但在某些场景下仍可能出现。

---

**示例5：串行化（SERIALIZABLE）**
```sql
-- 事务A（串行化）
SET SESSION TRANSACTION ISOLATION LEVEL SERIALIZABLE;
BEGIN;
SELECT * FROM account WHERE id=1;  -- 加共享锁

-- 事务B（在另一个会话）
BEGIN;
UPDATE account SET balance=200 WHERE id=1;  -- 阻塞，等待事务A释放锁

-- 事务A 提交
COMMIT;  -- 事务B的UPDATE才能执行
```
**结果**：事务顺序执行，完全避免并发问题，但性能最差。

---

### 如何设置隔离级别

```sql
-- 查看当前隔离级别
SELECT @@transaction_isolation;

-- 设置会话级别
SET SESSION TRANSACTION ISOLATION LEVEL READ COMMITTED;

-- 设置全局级别（需要重启生效）
SET GLOBAL TRANSACTION ISOLATION LEVEL REPEATABLE READ;
```

---

## 5. MVCC 原理

### 版本链（Undo Log）
- 每个版本有 trx_id（事务ID）和 roll_ptr（回滚指针）
- 通过 roll_ptr 连接形成版本链

### ReadView（读视图）
**包含信息**：
- m_ids：当前活跃的事务ID列表
- min_trx_id：最小的活跃事务ID
- max_trx_id：下一个要分配的事务ID

### 可见性判断规则
```
1. trx_id < min_trx_id → ✅ 可见（已提交）
2. trx_id >= max_trx_id → ❌ 不可见（还未开始）
3. trx_id 在 m_ids 中 → ❌ 不可见（未提交）
4. 其他情况 → ✅ 可见（已提交）
```

### RC vs RR
| 隔离级别 | ReadView创建时机 | 特点 |
|---------|-----------------|------|
| READ COMMITTED | 每次查询都创建 | 可以读到已提交的修改 |
| REPEATABLE READ | 第一次查询时创建 | 事务内多次查询结果一致 |

---

## 面试高频题

### 索引相关

**Q1: 聚簇索引和非聚簇索引的区别？**
A: 聚簇索引叶子节点存完整行数据，非聚簇索引存索引列值+主键值

**Q2: 什么是回表？如何避免？**
A: 通过非聚簇索引查询后，再通过主键索引查找完整数据。使用覆盖索引可以避免回表

**Q3: 为什么 WHERE age + 1 = 26 不能使用索引？**
A: B+树索引存储的是age的原始值，无法直接查找age+1的值，必须全表扫描

**Q4: 为什么 LIKE '%张%' 不能使用索引？**
A: B+树是有序的，只有前缀匹配才能利用索引的有序性快速定位

**Q5: 什么是覆盖索引？**
A: 查询的所有字段都包含在索引中，无需回表，性能提升约50%

### MVCC相关

**Q1: 什么是MVCC？**
A: 多版本并发控制，通过保存数据的多个版本，让不同事务看到不同的数据快照

**Q2: MVCC如何实现可重复读？**
A: 通过版本链保存历史版本，事务第一次查询时创建ReadView，后续查询复用ReadView

**Q3: ReadView包含哪些信息？**
A: m_ids（活跃事务列表）、min_trx_id、max_trx_id、creator_trx_id

**Q4: RC和RR的区别？**
A: RC每次查询都创建ReadView，RR第一次查询时创建并复用

**Q5: MVCC能完全避免锁吗？**
A: 不能。MVCC只解决读-写冲突，写-写冲突仍需要加锁

---

**最后更新**: 2026-03-10
