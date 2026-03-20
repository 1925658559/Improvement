# Day 14: 组合（回溯）+ Redis 基础（5种数据结构 + 跳表原理）

## 🔥 算法热身

### 债务清除：LeetCode 78 - 子集 I

**题目**: LeetCode 78 - 子集
**链接**: https://leetcode.cn/problems/subsets/
**状态**: ✅ 通过（债务清除）
**代码位置**: `代码练习/homework/Subsets.java`

**核心思想**：回溯，每次进入函数时把当前 path 加入结果集（决策树的每个节点都是答案）

**完整代码**：
```java
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    List<Integer> path = new ArrayList<>();
    backtrack(nums, 0, path, result);
    return result;
}

private void backtrack(int[] nums, int start,
                      List<Integer> path, List<List<Integer>> result) {
    result.add(new ArrayList<>(path));  // 每个节点都是答案
    for (int i = start; i < nums.length; i++) {
        path.add(nums[i]);
        backtrack(nums, i + 1, path, result);
        path.remove(path.size() - 1);
    }
}
```

---

### 新题：LeetCode 77 - 组合

**题目**: LeetCode 77 - 组合
**链接**: https://leetcode.cn/problems/combinations/
**状态**: ✅ 通过
**代码位置**: `代码练习/homework/Combinations.java`
**耗时**: Bug free

**题目描述**:
给定两个整数 `n` 和 `k`，返回范围 `[1, n]` 中所有可能的 `k` 个数的组合。

**示例**:
```
输入：n = 4, k = 2
输出：[[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]
```

### 解法：回溯 + 递归终止条件

**核心思想**：
- 只有叶子节点（`path.size() == k`）才加入结果集
- 遍历范围是 `[1, n]`，注意是 `<=`

**完整代码**：
```java
public List<List<Integer>> combine(int n, int k) {
    List<List<Integer>> result = new ArrayList<>();
    List<Integer> path = new ArrayList<>();
    backtrack(n, k, 1, path, result);
    return result;
}

private void backtrack(int n, int k, int start,
                      List<Integer> path, List<List<Integer>> result) {
    if (path.size() == k) {          // 终止条件：选够 k 个数
        result.add(new ArrayList<>(path));
        return;
    }
    for (int i = start; i <= n; i++) {  // 注意是 <=
        path.add(i);
        backtrack(n, k, i + 1, path, result);
        path.remove(path.size() - 1);
    }
}
```

**复杂度分析**:
- 时间复杂度: **O(C(n,k) × k)** - C(n,k) 个组合，每个需要 O(k) 时间生成
- 空间复杂度: **O(k)** - 递归深度最大为 k

### 子集 vs 组合对比

| 特性 | 子集 (LeetCode 78) | 组合 (LeetCode 77) |
|------|-------------------|-------------------|
| **何时加入结果集** | 每次进入函数时 | 只在 path.size() == k 时 |
| **决策树收集** | 所有节点 | 只有叶子节点 |
| **是否需要 return** | 不需要 | 需要 |
| **遍历范围** | `i < nums.length` | `i <= n` |
| **回溯框架** | 完全相同 | 完全相同 |

### 遍历过程演示（n=4, k=2）

```
决策树：
                    []
          /      /    \    \
        [1]    [2]   [3]  [4]
       / | \    |  \   |
    [1,2][1,3][1,4] [2,3][2,4] [3,4]
      ✓   ✓    ✓     ✓    ✓     ✓
```

### 常见错误

**1. for 循环用 < 而不是 <=**
```java
// ❌ 错误：漏掉了 n 本身
for (int i = start; i < n; i++)

// ✅ 正确
for (int i = start; i <= n; i++)
```

**2. 忘记 return 终止递归**
```java
// ❌ 错误：没有 return，会继续递归
if (path.size() == k) {
    result.add(new ArrayList<>(path));
}

// ✅ 正确
if (path.size() == k) {
    result.add(new ArrayList<>(path));
    return;
}
```

**3. 忘记创建 ArrayList 副本**
```java
// ❌ 错误：path 是引用，会不断变化
result.add(path);

// ✅ 正确
result.add(new ArrayList<>(path));
```

---

## 🧠 核心技术：Redis 基础

### 一、Redis 是什么？

**定位**：基于内存的 Key-Value 数据库

```
Redis = Remote Dictionary Server
      = 内存数据库 + 丰富数据结构 + 持久化支持
```

**为什么 Redis 快？**

```
存储位置对比：
  内存（RAM）：读取速度 ~100 纳秒
  硬盘（SSD）：读取速度 ~100 微秒
  差距：1000 倍！

MySQL：数据主要在磁盘（有 Buffer Pool 缓存热数据）
Redis：数据主要在内存 → 速度极快
```

**Redis vs MySQL 对比**：

| 特性 | MySQL | Redis |
|------|-------|-------|
| **存储位置** | 主要在磁盘 | 主要在内存 |
| **读取速度** | 毫秒级（ms） | 微秒级（μs） |
| **数据持久化** | 天然持久化 | 需要配置（RDB/AOF） |
| **数据结构** | 表 + 行 | Key-Value + 丰富数据结构 |
| **适用场景** | 持久化存储、复杂查询 | 缓存、计数器、排行榜 |

---

### 二、Redis 5 种基本数据结构

```
Redis 数据结构全景：

String  → 简单 Key-Value        → Token、计数器、缓存
List    → 有序、可重复           → 消息队列、最新消息列表
Hash    → Key-Field-Value       → 存储对象属性（用户信息）
Set     → 无序、不重复           → 好友列表、标签、去重
ZSet    → 有序、不重复、带分数   → 排行榜、优先级队列
```

#### 1. String（字符串）

**常用命令**：
```bash
SET key value          # 设置值
GET key                # 获取值
INCR key               # 原子自增（计数器）
EXPIRE key seconds     # 设置过期时间
SETNX key value        # 不存在才设置（分布式锁）
```

**典型场景**：
```
场景 1：缓存用户信息
  SET user:1001 '{"name":"张三","age":25}'
  GET user:1001

场景 2：计数器（文章阅读量）
  INCR article:views:1001   → 原子操作，线程安全

场景 3：存储登录 Token
  SET token:abc123 "user:1001" EX 3600  → 1小时过期
```

---

#### 2. List（列表）

**底层实现**：双向链表 + 压缩列表（ziplist）

**常用命令**：
```bash
RPUSH key value    # 从右边插入
LPUSH key value    # 从左边插入
LPOP  key          # 从左边取出
RPOP  key          # 从右边取出
LRANGE key 0 -1   # 获取所有元素
```

**典型场景**：
```
消息队列（先进先出）：
  生产者：RPUSH queue "msg1"   → 从右边放入
  消费者：LPOP  queue          → 从左边取出

最新消息列表（最新 10 条）：
  LPUSH news "新消息"
  LTRIM news 0 9              → 只保留最新 10 条
```

---

#### 3. Hash（哈希）

**底层实现**：哈希表 + 压缩列表（ziplist）

**常用命令**：
```bash
HSET  key field value    # 设置字段
HGET  key field          # 获取字段
HMSET key f1 v1 f2 v2   # 批量设置
HGETALL key              # 获取所有字段
HDEL  key field          # 删除字段
```

**典型场景**：
```
存储用户对象：
  HSET user:1001 name "张三"
  HSET user:1001 age  25
  HSET user:1001 city "北京"
  HGETALL user:1001
  → {name: 张三, age: 25, city: 北京}

优势：可以单独更新某个字段，不需要序列化整个对象
```

---

#### 4. Set（集合）

**底层实现**：哈希表 + 整数集合（intset）

**常用命令**：
```bash
SADD     key member      # 添加元素（自动去重）
SISMEMBER key member     # 判断是否存在 O(1)
SMEMBERS key             # 获取所有元素
SINTER   key1 key2       # 交集（共同好友）
SUNION   key1 key2       # 并集
SDIFF    key1 key2       # 差集
```

**典型场景**：
```
好友列表：
  SADD friends:张三 "李四" "王五" "赵六"
  SISMEMBER friends:张三 "李四"  → 1（是好友）
  SISMEMBER friends:张三 "陌生人" → 0（不是好友）

共同好友：
  SINTER friends:张三 friends:李四  → 共同好友列表
```

---

#### 5. ZSet（有序集合）

**底层实现**：哈希表 + 跳表（Skip List）

**常用命令**：
```bash
ZADD    key score member    # 添加元素（带分数）
ZSCORE  key member          # 获取分数
ZRANK   key member          # 获取排名（从小到大）
ZREVRANK key member         # 获取排名（从大到小）
ZRANGE  key 0 9 WITHSCORES  # 获取 Top 10
ZRANGEBYSCORE key 80 90     # 获取分数在 80-90 的元素
```

**典型场景**：
```
游戏排行榜：
  ZADD leaderboard 9500 "张三"
  ZADD leaderboard 8800 "李四"
  ZADD leaderboard 9200 "王五"

  ZREVRANGE leaderboard 0 9 WITHSCORES  → Top 10
  ZREVRANK  leaderboard "张三"          → 排名第 1
```

---

### 三、跳表原理（Skip List）

**为什么需要跳表？**

```
普通有序链表查找 17：
1 → 3 → 5 → 7 → 9 → 11 → 13 → 15 → 17
需要遍历 9 个节点，时间复杂度 O(n)
```

**跳表的核心思想：多层索引**

```
第 3 层：  1 ─────────────→ 9 ─────────────→ 17
           ↓                  ↓                  ↓
第 2 层：  1 ────→ 5 ────→ 9 ────→ 13 ────→ 17
           ↓        ↓        ↓        ↓         ↓
第 1 层：  1 → 3 → 5 → 7 → 9 → 11 → 13 → 15 → 17 → 19

查找 17 的路径：
  第3层：1 → 9 → 17 ✓（跳过了很多节点）
  只需访问 5 个节点，而不是 9 个
```

**跳表的性能**：

| 操作 | 时间复杂度 | 原理 |
|------|-----------|------|
| **查找** | O(log n) | 多层索引，每层跳过一半节点 |
| **插入** | O(log n) | 找到位置后插入，随机决定层数 |
| **删除** | O(log n) | 找到节点后删除所有层的索引 |
| **范围查询** | O(log n + k) | 找到起点后顺序遍历 |

**为什么层数是 log n？**
```
每层跳过一半节点（类似二分查找）：
  n 个节点 → log₂n 层索引
  每层最多访问 2 个节点
  总访问次数 ≈ 2 × log₂n = O(log n)
```

---

### 四、ZSet 为什么用跳表而不是红黑树？

| 对比维度 | 跳表 | 红黑树 |
|---------|------|--------|
| **实现复杂度** | 简单，代码 < 200 行 | 复杂，需要旋转操作，代码 > 500 行 |
| **范围查询** | 找到起点后顺序遍历 ✅ | 需要中序遍历，实现复杂 |
| **并发性能** | 只需锁局部节点 ✅ | 旋转影响多个节点，锁范围大 |
| **查找性能** | O(log n) | O(log n) |
| **内存占用** | 略高（多层索引） | 略低 |

**Redis 作者 antirez 的原话**：
> "跳表实现更简单，调试更容易，而且在范围查询上比红黑树更高效。"

---

### 五、常见面试题

#### Q1: Redis 为什么这么快？

**标准答案**：
1. **基于内存**：数据存储在内存中，读写速度比磁盘快 1000 倍
2. **单线程模型**：避免了多线程的 Context Switch 和锁竞争
3. **I/O 多路复用**：使用 epoll 实现非阻塞 I/O，单线程处理大量并发连接
4. **高效数据结构**：跳表、哈希表等，时间复杂度低

---

#### Q2: Redis 的 5 种基本数据结构及适用场景？

**标准答案**：

| 数据结构 | 适用场景 |
|---------|---------|
| String | Token、计数器、缓存对象 |
| List | 消息队列、最新消息列表 |
| Hash | 存储对象属性（用户信息） |
| Set | 好友列表、标签、去重、共同好友 |
| ZSet | 排行榜、优先级队列、延迟队列 |

---

#### Q3: 跳表的时间复杂度是多少？为什么？

**标准答案**：
查找、插入、删除均为 O(log n)。原因是跳表通过多层索引，每层跳过一半的节点，类似二分查找的思想，需要 log n 层索引，每层最多访问常数个节点。

---

#### Q4: 为什么 Redis ZSet 用跳表而不是红黑树？

**标准答案**：
1. **实现简单**：跳表代码量远少于红黑树，维护成本低
2. **范围查询更高效**：跳表找到起点后顺序遍历即可，红黑树需要中序遍历
3. **并发性能更好**：跳表只需锁局部节点，红黑树旋转时影响多个节点

---

#### Q5: Redis 的 Set 和 ZSet 有什么区别？

**标准答案**：

| 特性 | Set | ZSet |
|------|-----|------|
| **是否有序** | 无序 | 有序（按 score 排序） |
| **是否有分数** | 无 | 有（每个元素带 score） |
| **底层实现** | 哈希表 | 哈希表 + 跳表 |
| **适用场景** | 去重、共同好友 | 排行榜、优先级队列 |

---

### 六、常见误区

#### ❌ 误区1：Redis 是单线程的，所以性能差

```
正确理解：
Redis 的核心命令处理是单线程的，但这反而是优势：
- 避免了多线程的 Context Switch 开销
- 避免了锁竞争
- 配合 I/O 多路复用（epoll），单线程也能处理大量并发
Redis 6.0 之后，I/O 读写引入了多线程，但命令执行仍是单线程
```

#### ❌ 误区2：Redis 数据存在内存中，断电就丢失

```
正确理解：
Redis 支持两种持久化方式：
- RDB：定期快照，将内存数据写入磁盘
- AOF：记录每条写命令，重启时重放
可以配置持久化策略，保证数据安全
```

#### ❌ 误区3：Hash 适合存储好友列表

```
正确理解：
Hash 适合存储对象的多个属性（Key-Field-Value）
好友列表只需要存储 ID，不需要 Field-Value 结构
应该用 Set：自动去重 + O(1) 判断是否是好友
```

---

## ⚖️ 今日总结

### 技能评估

| 技能点 | 状态 | 说明 |
|--------|------|------|
| LeetCode 78（子集 I）| ✅ Proficient | 债务清除，代码通过测试 |
| LeetCode 77（组合）| ✅ Proficient | 理解终止条件，代码通过测试 |
| 子集 vs 组合的区别 | ✅ Proficient | 所有节点 vs 叶子节点 |
| Redis 定位 | ✅ Familiar | 内存数据库，比 MySQL 快 1000 倍 |
| Redis 5 种数据结构 | ✅ Familiar | String/List/Hash/Set/ZSet 及适用场景 |
| 跳表原理 | ✅ Familiar | 多层索引，O(log n) 查找 |
| ZSet vs 红黑树 | ✅ Familiar | 实现简单、范围查询更高效 |

### 技术债务

- 无新增

### 明日计划 (Day 15)

1. **算法**：LeetCode 46 - 全排列（回溯专题）
2. **Redis 深入**：
   - RDB vs AOF 持久化原理
   - 缓存穿透 / 击穿 / 雪崩 / 双写一致性
3. **目标**：Redis 缓存问题是面试高频考点，必须掌握到 Proficient 级别
