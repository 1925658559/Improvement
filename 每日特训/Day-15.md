# Day 15: 全排列（回溯）+ Redis 持久化 + 缓存三灾

## 🔥 算法热身

### 题目：LeetCode 46 - 全排列
**链接**: https://leetcode.cn/problems/permutations/
**状态**: ✅ 通过
**代码位置**: `代码练习/homework/Permutations.java`

**题目描述**:
给定一个不含重复数字的数组 nums，返回其所有可能的全排列。

**示例**:
```
输入：nums = [1,2,3]
输出：[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
```

---

### 解法一：暴力（不可行分析）

直接用 start 参数（组合的做法）：
```java
// ❌ 错误：用 start 会漏掉 [2,1,3] 这类排列
// 当第一位选了 2，start=2，后续只能选索引 2 之后的元素
// 导致 1 再也无法被选到
```

---

### 解法二：回溯 + used[] 数组 O(n × n!)

**核心思想**：
- 全排列 order matters，[1,2] ≠ [2,1]，所以每次都从头遍历整个数组
- 用 `boolean[] used` 标记已选元素，防止重复选取
- 终止条件：`path.size() == nums.length`（所有元素都选完了）

**与组合的核心区别**：

| 特性 | 组合 (LeetCode 77) | 全排列 (LeetCode 46) |
|------|-------------------|---------------------|
| **顺序** | 无序，[1,2]==[2,1] | 有序，[1,2]≠[2,1] |
| **去重机制** | start 参数（只往后选） | used[] 数组（标记已选） |
| **每层遍历范围** | [start, n] | [0, n-1]（全部） |
| **终止条件** | path.size() == k | path.size() == nums.length |

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
    if (path.size() == nums.length) {
        result.add(new ArrayList<>(path));  // 快照副本！
        return;
    }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;          // 已选则跳过
        path.add(nums[i]);
        used[i] = true;                 // 做选择
        backtrack(nums, used, path, result);
        path.remove(path.size() - 1);
        used[i] = false;                // 撤销选择（回溯）
    }
}
```

**复杂度分析**:
- 时间复杂度：**O(n × n!)** — n! 个排列，每个需要 O(n) 生成
- 空间复杂度：**O(n)** — 递归深度 + used 数组

**遍历过程演示（nums=[1,2,3]）**：
```
                        []
           /             |             \
         [1]            [2]            [3]
        /   \          /   \          /   \
     [1,2] [1,3]    [2,1] [2,3]    [3,1] [3,2]
       |     |        |     |        |     |
   [1,2,3][1,3,2] [2,1,3][2,3,1] [3,1,2][3,2,1]
     ✓     ✓        ✓     ✓        ✓     ✓
```

### 常见错误

**1. result.add(path) 不创建副本**
```java
// ❌ 错误：path 是引用，最终所有结果都指向同一个空列表
result.add(path);

// ✅ 正确：创建快照
result.add(new ArrayList<>(path));
```

**2. 用 start 替代 used[]**
```java
// ❌ 错误：会漏掉 [2,1,3] 等排列
backtrack(nums, i + 1, path, result);

// ✅ 正确：每次从 0 开始，用 used[] 去重
for (int i = 0; i < nums.length; i++) {
    if (used[i]) continue;
    ...
}
```

**3. 忘记 used[i] = false 回溯**
```java
// ❌ 错误：used[i] 永远是 true，后续分支无法选该元素
used[i] = true;
backtrack(...);
// 忘记 used[i] = false

// ✅ 正确
used[i] = true;
backtrack(...);
used[i] = false;  // 必须撤销
```

---

## 🧠 核心技术：Redis 持久化 + 缓存三灾

### 一、Redis 持久化

**为什么需要持久化？**
```
Redis 数据在内存 → 断电/重启 → 数据全丢
解决方案：把内存数据写到磁盘，重启时恢复
```

---

#### RDB（Redis Database）— 快照

**原理**：定期将内存中所有数据拍一张"快照"，以二进制文件存储到磁盘。

```
内存状态：
  key1 → value1
  key2 → value2      →  [RDB 文件] dump.rdb（二进制压缩）
  key3 → value3

触发方式：
  1. 手动：BGSAVE 命令（fork 子进程异步执行）
  2. 自动：配置 save 900 1（900秒内有1次写操作则触发）
```

**优点**：
- 文件小（二进制压缩）
- 恢复速度快（直接加载快照）
- 对性能影响小（fork 子进程执行）

**缺点**：
- **数据丢失**：最多丢失上次快照到宕机之间的数据（分钟级）

---

#### AOF（Append Only File）— 命令日志

**原理**：每次执行写命令，将命令追加记录到 AOF 文件，重启时重放所有命令恢复数据。

```
写操作：SET key1 value1
         ↓
AOF 文件追加：*3\r\n$3\r\nSET\r\n$4\r\nkey1\r\n$6\r\nvalue1\r\n
```

**三种刷盘策略**：

| 策略 | 说明 | 数据安全性 | 性能 |
|------|------|-----------|------|
| always | 每条命令都刷盘 | 最高（最多丢1条） | 最低 |
| everysec | 每秒刷盘一次 | 高（最多丢1秒） | 中（推荐） |
| no | 由 OS 决定 | 低 | 最高 |

**AOF Rewrite（重写）**：
```
问题：key 被改 1000 次 → AOF 有 1000 条记录 → 文件越来越大
解决：扫描当前内存，每个 key 只保留最终状态的 1 条命令

SET key 1
SET key 2        →  AOF Rewrite  →  SET key 1000
...
SET key 1000
```

**优点**：数据安全，最多丢失 1 秒数据

**缺点**：
- 文件体积大（文本格式）
- 恢复速度慢（需重放所有命令）
- 频繁 I/O，写性能略低于 RDB

---

#### RDB vs AOF 对比

| | RDB | AOF |
|--|-----|-----|
| **机制** | 定期快照 | 记录每条写命令 |
| **数据安全** | 可能丢失几分钟数据 | 最多丢 1 秒（everysec） |
| **文件大小** | 小（二进制压缩） | 大（可重写压缩） |
| **恢复速度** | 快 | 慢（重放命令） |
| **适用场景** | 缓存、可容忍丢失 | 金融、不可丢失数据 |

**生产环境**：通常 **RDB + AOF 混用**，兼顾恢复速度和数据安全。

---

### 二、缓存三灾

#### 1. 缓存穿透（Cache Penetration）

**定义**：查询一个**数据库中根本不存在**的 key，每次都穿透缓存直接打到 DB。

```
正常流程：请求 → Redis（命中）→ 返回
穿透流程：请求 → Redis（未命中）→ MySQL（也没有）→ 返回空
          恶意攻击：每秒 10000 次不存在的 key → MySQL 崩溃
```

**解决方案**：

方案 A：**缓存空值**
```java
Object value = redis.get(key);
if (value == null) {
    value = mysql.query(key);
    if (value == null) {
        redis.set(key, "NULL", 300);  // 缓存空值，5分钟过期
    }
}
```
缺陷：① 大量空值占用内存；② 数据写入后短暂不一致

方案 B：**布隆过滤器（Bloom Filter）**
```
原理：用 bitmap + 多个 hash 函数，判断 key 是否"可能存在"
      不存在 → 100% 准确，直接拦截
      存在   → 有小概率误判（false positive）

请求 → 布隆过滤器（不存在？直接返回）→ Redis → MySQL
```

---

#### 2. 缓存击穿（Cache Breakdown）

**定义**：**热点 key 过期**，大量并发请求同时穿透缓存打到 DB。

```
热点商品 key 过期瞬间：
  1000 个请求 → Redis（未命中）→ 1000 个请求同时查 MySQL → 崩溃
```

**解决方案**：

方案 A：**互斥锁**
```java
String value = redis.get(key);
if (value == null) {
    if (lock.tryLock()) {           // 只有一个线程抢到锁
        try {
            value = mysql.query(key);
            redis.set(key, value);
        } finally {
            lock.unlock();
        }
    } else {
        Thread.sleep(50);
        return get(key);            // 其余线程等待后重试
    }
}
```
缺陷：等待期间延迟增加，影响用户体验

方案 B：**热点 key 永不过期**（逻辑过期）
```
不设置 TTL，后台异步线程定期刷新缓存
缺陷：数据更新后存在短暂不一致窗口
```

---

#### 3. 缓存雪崩（Cache Avalanche）

**定义**：**大量 key 同时过期** 或 **Redis 宕机**，导致请求全部打到 DB。

**触发场景 1：大量 key 同时过期**
```
凌晨 0 点：批量加载 10 万商品缓存，全部设置 2 小时过期
凌晨 2 点：10 万 key 同时失效 → 海量请求打到 MySQL → 崩溃
```

解决：**过期时间加随机值**
```java
// ❌ 全部同时过期
redis.set(key, value, 7200);

// ✅ 随机打散
redis.set(key, value, 7200 + random.nextInt(600));  // ±10分钟
```

**触发场景 2：Redis 宕机**
```
整个缓存层挂掉 → 所有请求直接打到 MySQL
```

解决：**Redis 集群 / 主从复制**（高可用架构，避免单点故障）

---

#### 缓存三灾对比

| 问题 | 触发条件 | 核心区别 | 解决方案 |
|------|---------|---------|---------|
| **穿透** | 查询不存在的 key | key 从未存在 | 缓存空值 / 布隆过滤器 |
| **击穿** | 热点 key 过期 | 单个热点 key | 互斥锁 / 永不过期 |
| **雪崩** | 大量 key 同时过期 / Redis 宕机 | 大规模失效 | 随机过期时间 / 集群高可用 |

---

### 三、常见面试题

#### Q1: RDB 和 AOF 的区别？生产环境怎么选？

**标准答案**：
RDB 是定期快照，文件小、恢复快，但可能丢失几分钟数据；AOF 记录每条写命令，数据安全（最多丢 1 秒），但文件大、恢复慢。生产环境通常两者混用：AOF 保证数据安全，RDB 作为快速恢复的备份。

#### Q2: 什么是缓存穿透？怎么解决？

**标准答案**：
缓存穿透是指查询一个数据库中根本不存在的 key，每次都绕过缓存直接打到数据库。解决方案：① 缓存空值（查到空结果也缓存，设置短过期时间）；② 布隆过滤器（在缓存前加一层过滤，不存在的 key 直接拦截）。

#### Q3: 缓存击穿和缓存雪崩的区别？

**标准答案**：
击穿是单个热点 key 过期，大量并发请求同时穿透；雪崩是大量 key 同时过期（或 Redis 宕机），导致整体缓存失效。击穿用互斥锁或永不过期解决；雪崩用随机过期时间或集群高可用解决。

#### Q4: AOF Rewrite 的原理是什么？

**标准答案**：
AOF 文件会随写操作不断增大（同一个 key 改 1000 次就有 1000 条记录）。Rewrite 通过扫描当前内存状态，将每个 key 的当前值转成一条命令，生成一个精简的新 AOF 文件，替换旧文件。1000 条历史记录压缩为 1 条。

#### Q5: 布隆过滤器为什么会误判？

**标准答案**：
布隆过滤器用多个 hash 函数将 key 映射到 bitmap 的多个位置。判断存在时，只能确认对应位都是 1，但这些位可能是其他 key 设置的（hash 碰撞），所以存在 false positive（误判为存在）。但判断不存在时是 100% 准确的（只要有一位是 0，就一定不存在）。

---

### 四、常见误区

#### ❌ 误区1：缓存穿透 = 缓存击穿
```
穿透：key 根本不存在（数据库也没有）
击穿：key 存在但过期了（数据库有，只是缓存失效）
完全不同的场景，解决方案也不同
```

#### ❌ 误区2：AOF everysec 策略最多丢 1 条命令
```
正确理解：everysec 是每秒刷盘，最多丢失 1 秒内的所有写命令
          不是 1 条，1 秒内可能有成千上万条写操作
```

#### ❌ 误区3：缓存空值会导致数据永久不一致
```
正确理解：缓存空值要设置短过期时间（如 5 分钟）
          过期后下次请求会重新查 DB，数据最终一致
          也可以在数据写入时主动删除对应的空值缓存
```

---

## ⚖️ 今日总结

### 技能评估

| 技能点 | 状态 | 说明 |
|--------|------|------|
| LeetCode 46（全排列）| ✅ Proficient | used[] 替代 start，理解透彻 |
| 全排列 vs 组合的区别 | ✅ Proficient | 有序 vs 无序，used[] vs start |
| RDB 原理 | ✅ Proficient | 快照、fork 子进程、优缺点 |
| AOF 原理 | ✅ Proficient | 命令日志、三种刷盘策略、Rewrite |
| 缓存穿透 | ✅ Proficient | 空值缓存 + 布隆过滤器 |
| 缓存击穿 | ✅ Proficient | 互斥锁 + 永不过期 |
| 缓存雪崩 | ✅ Proficient | 随机过期时间 + 集群高可用 |

### 技术债务

- 无

### 明日计划 (Day 16)

1. **算法**：LeetCode 39 - 组合总和（回溯 + 剪枝）
2. **技术**：
   - Redis 双写一致性（Cache Aside / Write Through / Write Behind）
   - Spring Bean 生命周期
3. **目标**：双写一致性是分布式系统高频考点，必须掌握到 Proficient
