# Day 13: 子集 II（回溯去重）+ MySQL 索引失效场景 + EXPLAIN 推演

## 🔥 算法热身

**题目**: LeetCode 90 - 子集 II
**链接**: https://leetcode.cn/problems/subsets-ii/
**状态**: ✅ 通过
**代码位置**: `代码练习/homework/SubsetsII.java`

**题目描述**:
给你一个整数数组 `nums`，其中**可能包含重复元素**，请你返回该数组所有可能的子集（幂集）。
解集**不能包含重复的子集**。返回的解集中，子集可以按任意顺序排列。

**示例**:
```
输入：nums = [1,2,2]
输出：[[],[1],[1,2],[1,2,2],[2],[2,2]]
```

---

## 📝 算法解法详解

### 核心思想：回溯 + 去重

**子集 II vs 子集 I 的关键区别**：

| 特性 | 子集 I (LeetCode 78) | 子集 II (LeetCode 90) |
|------|---------------------|----------------------|
| **输入** | 元素互不相同 | 可能包含重复元素 |
| **去重方式** | 只需 `start` 索引 | `start` 索引 + 去重逻辑 |
| **预处理** | 不需要排序 | 必须先排序 |
| **核心难点** | 避免选同一位置 | 避免选相同值（同一层） |

---

### 解法：回溯 + 排序 + 去重

**核心思想**：
1. **先排序**：让相同元素相邻，方便去重
2. **去重条件**：`if (i > start && nums[i] == nums[i-1]) continue;`
   - `i > start`：不是当前层的第一个选择
   - `nums[i] == nums[i-1]`：当前元素和前一个元素相同
3. **回溯框架**：和子集 I 完全一样

**完整代码**：
```java
public List<List<Integer>> subsetsWithDup(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    List<Integer> path = new ArrayList<>();

    // 1. 先排序（关键！）
    Arrays.sort(nums);

    // 2. 从索引 0 开始回溯
    backtrack(nums, 0, path, result);

    return result;
}

private void backtrack(int[] nums, int start,
                      List<Integer> path, List<List<Integer>> result) {
    // 1. 把当前 path 加入结果集
    result.add(new ArrayList<>(path));

    // 2. 遍历：从 start 开始
    for (int i = start; i < nums.length; i++) {
        // 3. 去重逻辑（关键！）
        if (i > start && nums[i] == nums[i - 1]) {
            continue; // 跳过重复元素
        }

        // 4. 做选择
        path.add(nums[i]);

        // 5. 递归
        backtrack(nums, i + 1, path, result);

        // 6. 撤销选择（回溯）
        path.remove(path.size() - 1);
    }
}
```

**复杂度分析**:
- 时间复杂度: **O(n × 2ⁿ)** - 2ⁿ 个子集，每个需要 O(n) 时间生成
- 空间复杂度: **O(n × 2ⁿ)** - 结果集存储 2ⁿ 个子集
- 递归深度: O(n)

---

### 🔍 执行过程演示

**输入**: `nums = [1,2,2]`（排序后仍是 `[1,2,2]`）

```
决策树：
                    []  ← 加入结果
          /         |         \
        [1]        [2]    (跳过第2个2)  ← 去重！
       /   \        |
    [1,2] [1,2]   [2,2]  ← 这里的[1,2]不跳过（不在同一层）
      |
   [1,2,2]
```

**遍历过程表格**：

| 步骤 | 操作 | i | start | nums[i] | 是否跳过 | path | result |
|------|------|---|-------|---------|---------|------|--------|
| 1 | 初始调用 | - | 0 | - | - | [] | [[]] |
| 2 | i=0, 选择 1 | 0 | 0 | 1 | ❌ (i==start) | [1] | [[], [1]] |
| 3 | i=1, 选择 2 | 1 | 1 | 2 | ❌ (i==start) | [1,2] | [..., [1,2]] |
| 4 | i=2, 选择 2 | 2 | 2 | 2 | ❌ (i==start) | [1,2,2] | [..., [1,2,2]] |
| 5 | 回溯 | - | - | - | - | [1,2] | - |
| 6 | 回溯 | - | - | - | - | [1] | - |
| 7 | i=2, 选择 2 | 2 | 1 | 2 | ✅ (i>start && nums[2]==nums[1]) | - | - |
| 8 | 回溯 | - | - | - | - | [] | - |
| 9 | i=1, 选择 2 | 1 | 0 | 2 | ❌ (nums[1]!=nums[0]) | [2] | [..., [2]] |
| 10 | i=2, 选择 2 | 2 | 2 | 2 | ❌ (i==start) | [2,2] | [..., [2,2]] |

**最终结果**: `[[], [1], [1,2], [1,2,2], [2], [2,2]]`

---

### 🔑 关键知识点

#### 1. 为什么必须先排序？

**原因**：
- 去重逻辑依赖于比较 `nums[i]` 和 `nums[i-1]`
- 只有排序后，相同的元素才会相邻
- 如果不排序，相同元素可能分散在数组中，无法通过比较相邻元素去重

**示例**：
```
未排序：[2, 1, 2]
- 无法通过 nums[i] == nums[i-1] 判断重复

排序后：[1, 2, 2]
- nums[2] == nums[1]，可以判断重复 ✅
```

---

#### 2. 为什么去重条件是 `i > start` 而不是 `i > 0`？

**错误代码**：
```java
// ❌ 错误：使用 i > 0
if (i > 0 && nums[i] == nums[i - 1]) {
    continue;
}
```

**问题**：会跳过合法的子集，比如 `[2,2]`

**正确代码**：
```java
// ✅ 正确：使用 i > start
if (i > start && nums[i] == nums[i - 1]) {
    continue;
}
```

**原因**：
- `i > start` 表示"不是当前层的第一个选择"
- 在第 1 层，第二个 2 应该跳过（避免重复的 [2] 分支）
- 在第 2 层，第二个 2 不应该跳过（需要生成 [2,2]）

**ASCII 图示**：
```
第 1 层（start=0）：
  i=1: nums[1]=2, i > start (1 > 0), 不跳过 ✅
  i=2: nums[2]=2, i > start (2 > 0), nums[2]==nums[1], 跳过 ✅

选择了 nums[1]=2 后，进入第 2 层（start=2）：
  i=2: nums[2]=2, i == start (2 == 2), 不跳过 ✅
  → 生成 [2,2]
```

---

#### 3. 子集 I vs 子集 II 的代码对比

| 代码部分 | 子集 I | 子集 II |
|---------|--------|---------|
| **排序** | 不需要 | `Arrays.sort(nums)` |
| **去重** | 不需要 | `if (i > start && nums[i] == nums[i-1]) continue;` |
| **其他** | 完全相同 | 完全相同 |

---

### ⚠️ 常见错误

**1. 忘记排序**
```java
// ❌ 错误：没有排序
public List<List<Integer>> subsetsWithDup(int[] nums) {
    // 直接开始回溯，去重逻辑会失效
    backtrack(nums, 0, path, result);
}

// ✅ 正确：先排序
public List<List<Integer>> subsetsWithDup(int[] nums) {
    Arrays.sort(nums);  // 必须先排序
    backtrack(nums, 0, path, result);
}
```

---

**2. 去重条件错误**
```java
// ❌ 错误：使用 i > 0
if (i > 0 && nums[i] == nums[i - 1]) {
    continue;  // 会跳过 [2,2] 这样的合法子集
}

// ✅ 正确：使用 i > start
if (i > start && nums[i] == nums[i - 1]) {
    continue;  // 只跳过同一层的重复元素
}
```

---

**3. 忘记创建 ArrayList 副本**
```java
// ❌ 错误
result.add(path);  // path 是引用，会不断变化

// ✅ 正确
result.add(new ArrayList<>(path));  // 创建副本
```

---

**4. 数组越界**
```java
// ❌ 错误：没有检查 i-1 是否越界
if (nums[i] == nums[i - 1]) {  // 当 i=0 时会越界
    continue;
}

// ✅ 正确：先检查 i > start
if (i > start && nums[i] == nums[i - 1]) {
    continue;
}
```

---

## 🧠 核心技术：MySQL 索引失效场景 + EXPLAIN 推演

### 一、索引失效的 7 大场景

#### 场景 1：对索引列进行函数操作或计算

**示例**：
```sql
-- ❌ 索引失效
SELECT * FROM user WHERE age + 1 = 26;

-- ✅ 使用索引
SELECT * FROM user WHERE age = 25;
```

**原因**：
- B+树索引是按照 `age` 的原始值排序的
- 对 `age` 进行计算后，MySQL 无法直接在 B+树中定位
- 必须全表扫描，对每一行计算 `age + 1`

**ASCII 图示**：
```
B+树索引（idx_age）：
    [10 | 20 | 30]  ← 按 age 原始值排序
     /    |    \
  [1..9] [11..19] [21..29]

查询 WHERE age = 25：
  → 可以直接在 B+树中定位到 25 ✅

查询 WHERE age + 1 = 26：
  → B+树中没有 "age+1" 的值，无法定位 ❌
  → 必须全表扫描，逐行计算
```

---

#### 场景 2：隐式类型转换

**示例**：
```sql
CREATE TABLE user (
  phone VARCHAR(20),  -- 字符串类型
  KEY idx_phone(phone)
);

-- ❌ 索引失效（隐式类型转换）
SELECT * FROM user WHERE phone = 13800138000;  -- 数字

-- ✅ 使用索引
SELECT * FROM user WHERE phone = '13800138000';  -- 字符串
```

**原因**：
- MySQL 会把字符串 `phone` 转换成数字进行比较
- 相当于：`WHERE CAST(phone AS UNSIGNED) = 13800138000`
- 对索引列进行了函数操作，索引失效

**MySQL 类型转换规则**：
- 数字类型的优先级 > 字符串类型
- 当两种类型比较时，低优先级的类型会被转换成高优先级的类型
- 所以字符串会被转换成数字

---

#### 场景 3：LIKE 查询以 % 开头

**示例**：
```sql
-- ✅ 使用索引（前缀匹配）
SELECT * FROM user WHERE name LIKE '张%';

-- ❌ 索引失效（以 % 开头）
SELECT * FROM user WHERE name LIKE '%张';

-- ❌ 索引失效（包含）
SELECT * FROM user WHERE name LIKE '%张%';
```

**原因**：
- B+树索引是按照字符串的字典序排列的
- `LIKE '张%'`：以"张"开头的记录在 B+树中是连续的，可以范围查询
- `LIKE '%张'`：以"张"结尾的记录在 B+树中是分散的，无法定位

**ASCII 图示**：
```
B+树索引（idx_name）按字典序排列：

['李张'] → ['王小张'] → ['张三'] → ['张四'] → ['张五'] → ['赵张明']

查询 LIKE '张%'：
  → 从'张三'开始，顺序扫描到'张五' ✅

查询 LIKE '%张'：
  → '李张'和'王小张'在 B+树中不连续，无法定位 ❌
```

---

#### 场景 4：联合索引的最左前缀原则

**示例**：
```sql
CREATE TABLE user (
  name VARCHAR(50),
  age  INT,
  city VARCHAR(50),
  KEY idx_name_age_city(name, age, city)  -- 联合索引
);

-- ✅ 使用索引
SELECT * FROM user WHERE name = '张三';
SELECT * FROM user WHERE name = '张三' AND age = 25;
SELECT * FROM user WHERE name = '张三' AND age = 25 AND city = '北京';

-- ❌ 索引失效（跳过了最左边的 name）
SELECT * FROM user WHERE age = 25;
SELECT * FROM user WHERE age = 25 AND city = '北京';

-- ⚠️ 部分使用索引（只使用 name 部分）
SELECT * FROM user WHERE name = '张三' AND city = '北京';
```

**最左前缀原则**：
- 联合索引必须**从最左边的字段开始，连续使用**
- 如果跳过了最左边的字段，整个索引失效
- 如果跳过了中间的字段，只能使用到跳过字段之前的部分

**ASCII 图示**：
```
联合索引：idx_name_age_city(name, age, city)

✅ 可以使用的情况：
name                    → 使用 name
name → age              → 使用 name + age
name → age → city       → 使用 name + age + city

❌ 无法使用的情况：
     age                → 跳过了 name，整个索引失效
     age → city         → 跳过了 name，整个索引失效

⚠️ 部分使用的情况：
name → (跳过 age) → city → 只使用 name，city 无法使用
```

---

#### 场景 5：OR 条件

**示例**：
```sql
-- ❌ 索引失效（city 没有索引）
SELECT * FROM user WHERE name = '张三' OR city = '北京';

-- ✅ 使用索引（name 和 age 都有索引）
SELECT * FROM user WHERE name = '张三' OR age = 25;
```

**原因**：
- 如果 OR 的任一边没有索引，整个查询都无法使用索引
- 必须全表扫描

---

#### 场景 6：不等于（!=、<>）

**示例**：
```sql
-- ❌ 通常无法使用索引
SELECT * FROM user WHERE age != 25;
SELECT * FROM user WHERE age <> 25;

-- ✅ 使用索引
SELECT * FROM user WHERE age = 25;
SELECT * FROM user WHERE age > 25;
```

**原因**：
- 不等于的范围太大，优化器认为全表扫描更快
- 具体是否使用索引取决于数据分布

---

#### 场景 7：IS NULL / IS NOT NULL

**示例**：
```sql
-- ⚠️ 取决于数据分布
SELECT * FROM user WHERE age IS NULL;
SELECT * FROM user WHERE age IS NOT NULL;
```

**原因**：
- 如果 NULL 值很少，`IS NULL` 可能使用索引
- 如果 NULL 值很多，`IS NOT NULL` 可能不使用索引
- 具体取决于优化器的判断

---

### 二、EXPLAIN 推演实战

#### EXPLAIN 关键字段详解

**1. type（访问类型）- 最重要的性能指标**

| type 值 | 含义 | 性能 |
|---------|------|------|
| **system** | 表只有一行记录 | ⭐⭐⭐⭐⭐ 最好 |
| **const** | 通过主键或唯一索引查询，最多返回一行 | ⭐⭐⭐⭐⭐ 最好 |
| **eq_ref** | 唯一索引扫描 | ⭐⭐⭐⭐ 很好 |
| **ref** | 非唯一索引扫描 | ⭐⭐⭐⭐ 很好 |
| **range** | 索引范围扫描 | ⭐⭐⭐ 好 |
| **index** | 全索引扫描 | ⭐⭐ 一般 |
| **ALL** | 全表扫描 | ⭐ 差 |

**2. key（实际使用的索引）- 最直观的指标**

| key 值 | 含义 |
|--------|------|
| **索引名** | ✅ 使用了该索引 |
| **NULL** | ❌ 没有使用索引 |

**3. rows（扫描的行数）**

- 显示 MySQL 估计需要扫描的行数
- **数值越小越好**

**4. Extra（额外信息）**

| Extra 值 | 含义 | 性能 |
|----------|------|------|
| **Using index** | 使用了覆盖索引，不需要回表 | ⭐⭐⭐⭐⭐ 最好 |
| **Using where** | 需要在服务器层过滤数据 | ⭐⭐⭐ 一般 |
| **Using index condition** | 使用了索引下推优化 | ⭐⭐⭐⭐ 好 |
| **Using filesort** | 需要额外的排序操作 | ⭐⭐ 差 |
| **Using temporary** | 需要创建临时表 | ⭐ 很差 |

---

#### 如何判断索引是否生效？

**三个关键指标**：

1. **看 `key` 字段**：
   - ✅ 有索引名 → 使用了索引
   - ❌ `NULL` → 没有使用索引

2. **看 `type` 字段**：
   - ✅ `const`、`eq_ref`、`ref`、`range` → 使用了索引
   - ❌ `ALL` → 全表扫描，索引失效

3. **看 `rows` 字段**：
   - ✅ 数值小 → 扫描行数少，性能好
   - ❌ 数值大 → 扫描行数多，性能差

---

#### 实战案例对比

**案例 1：使用索引**
```sql
EXPLAIN SELECT * FROM user WHERE age = 25;
```

**EXPLAIN 输出**：
```
| type | key     | rows | Extra |
|------|---------|------|-------|
| ref  | idx_age | 100  | NULL  |
```

**分析**：
- ✅ `type = ref`：使用了索引
- ✅ `key = idx_age`：使用了 idx_age 索引
- ✅ `rows = 100`：只扫描 100 行

---

**案例 2：索引失效**
```sql
EXPLAIN SELECT * FROM user WHERE age + 1 = 26;
```

**EXPLAIN 输出**：
```
| type | key  | rows   | Extra       |
|------|------|--------|-------------|
| ALL  | NULL | 100000 | Using where |
```

**分析**：
- ❌ `type = ALL`：全表扫描
- ❌ `key = NULL`：没有使用索引
- ❌ `rows = 100000`：扫描 10 万行
- ⚠️ `Extra = Using where`：需要在服务器层过滤

**性能差距**：100 行 vs 100000 行 = **1000 倍**！

---

### 三、常见面试题

#### Q1: 为什么对索引列进行函数操作会导致索引失效？

**标准答案**：
B+树索引是按照列的原始值排序的。对列进行函数操作后，MySQL 无法直接在 B+树中定位，必须全表扫描，对每一行进行计算。

---

#### Q2: 什么是隐式类型转换？如何避免？

**标准答案**：
当字符串列和数字比较时，MySQL 会把字符串转换成数字，相当于对索引列进行了函数操作，导致索引失效。

**避免方法**：
- 确保查询条件的类型和列的类型一致
- 字符串列用字符串值查询：`WHERE phone = '13800138000'`

---

#### Q3: 什么是最左前缀原则？

**标准答案**：
联合索引必须从最左边的字段开始，连续使用。如果跳过了最左边的字段，整个索引失效；如果跳过了中间的字段，只能使用到跳过字段之前的部分。

**示例**：
```sql
-- 联合索引：idx_name_age_city(name, age, city)

-- ✅ 使用索引
WHERE name = '张三'
WHERE name = '张三' AND age = 25

-- ❌ 索引失效
WHERE age = 25  -- 跳过了 name

-- ⚠️ 部分使用
WHERE name = '张三' AND city = '北京'  -- 只使用 name
```

---

#### Q4: LIKE 查询什么时候能使用索引？

**标准答案**：
只有前缀匹配（`LIKE '张%'`）能使用索引。以 % 开头（`LIKE '%张'` 或 `LIKE '%张%'`）无法使用索引。

**原因**：
B+树索引是按字典序排列的，前缀匹配的记录在 B+树中是连续的，可以范围查询；以 % 开头的记录是分散的，无法定位。

---

#### Q5: 如何用 EXPLAIN 判断索引是否生效？

**标准答案**：
看三个关键字段：
1. **key**：有索引名表示使用了索引，NULL 表示没有使用
2. **type**：`ref`/`range` 表示使用了索引，`ALL` 表示全表扫描
3. **rows**：扫描行数，越小越好

---

### 四、常见误区

#### ❌ 误区1：索引越多越好

```
正确理解：
索引会占用存储空间，并且会降低写操作（INSERT/UPDATE/DELETE）的性能。
应该根据查询需求合理创建索引。
```

---

#### ❌ 误区2：联合索引可以跳过中间字段

```
正确理解：
联合索引必须从最左边开始，连续使用。
跳过中间字段后，后面的字段无法使用索引。
```

---

#### ❌ 误区3：LIKE 查询都不能使用索引

```
正确理解：
前缀匹配（LIKE '张%'）可以使用索引。
只有以 % 开头的 LIKE 查询才无法使用索引。
```

---

## ⚖️ 今日总结

### 掌握的技能

| 技能点 | 状态 | 说明 |
|--------|------|---------|
| LeetCode 90 (子集 II) | ✅ Proficient | 理解回溯 + 去重逻辑，代码通过测试 |
| 回溯去重条件 | ✅ Proficient | `i > start && nums[i] == nums[i-1]` |
| 索引失效场景 1：函数操作 | ✅ Proficient | `WHERE age + 1 = 26` |
| 索引失效场景 2：类型转换 | ✅ Proficient | `WHERE phone = 123`（phone 是 VARCHAR） |
| 索引失效场景 3：LIKE % 开头 | ✅ Proficient | `WHERE name LIKE '%张'` |
| 索引失效场景 4：最左前缀 | ✅ Proficient | 联合索引必须从最左边开始 |
| 索引失效场景 5：OR 条件 | ✅ Proficient | OR 的一边没有索引 |
| 索引失效场景 6：不等于 | ✅ Proficient | `WHERE age != 25` |
| 索引失效场景 7：IS NULL | ✅ Proficient | 取决于数据分布 |
| EXPLAIN 分析 | ✅ Proficient | 看 type、key、rows 三个字段 |
| EXPLAIN type 字段 | ✅ Proficient | ref/range 好，ALL 差 |
| EXPLAIN key 字段 | ✅ Proficient | 有索引名表示使用了索引 |

### 技术债务

- [ ] **算法代码实践**：LeetCode 78 自己默写 + 测试验证（Day-12 遗留）

---

## 🚀 明日计划 (Day 14)

继续 Phase 2 - Middleware 深挖：

1. **补齐技术债务**：
   - LeetCode 78 代码默写 + 测试

2. **Redis 基础**：
   - 5 种基本数据结构（String/List/Hash/Set/ZSet）
   - 跳表原理（为什么 ZSet 用跳表而不是红黑树）

3. **算法**：LeetCode 组合问题（回溯专题继续）

---
