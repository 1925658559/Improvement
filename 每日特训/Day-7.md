# 📅 每日特训日志

**日期**：Day-7
**第几天**：Day 7
**当前阶段**：Phase 1 - Java Core (内功重塑)

---

## ⏰ CRU - Step 1: Check-in (状态同步)

### 📌 今日攻击目标 (Target)
1. 类加载双亲委派机制
2. 打破双亲委派的场景
3. 算法：二叉树前序遍历

### 🔍 昨日回顾
- ✅ JVM 内存模型 → Proficient
- ✅ GC 算法 → Proficient
- ✅ LeetCode 142 → Pass

### ⚠️ 技术债务检查
- 无

---

## ⚔️ CRU - Step 2: Drill (高压训练)

### 🧮 算法热身 (15min)

**题目**: LeetCode 144 - 二叉树的前序遍历
**链接**: https://leetcode.cn/problems/binary-tree-preorder-traversal/
**状态**: ✅ Pass

**题目描述**:
给你二叉树的根节点 `root`，返回它节点值的**前序遍历**。

**示例**:
```
输入：root = [1,null,2,3]

    1
     \
      2
     /
    3

输出：[1,2,3]
```

---

### 📝 算法解法详解

#### 解法一：递归实现（推荐）

**核心思想**：按照"根-左-右"的顺序递归遍历

```java
public List<Integer> preorderTraversal(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    preorder(root, result);
    return result;
}

private void preorder(TreeNode node, List<Integer> result) {
    // 终止条件
    if (node == null) {
        return;
    }

    // 递归体：根 - 左 - 右
    result.add(node.val);           // 1. 访问根节点
    preorder(node.left, result);    // 2. 递归遍历左子树
    preorder(node.right, result);   // 3. 递归遍历右子树
}
```

**复杂度分析**:
- 时间复杂度: O(n) - 每个节点访问一次
- 空间复杂度: O(h) - h 为树的高度，递归栈深度

---

#### 解法二：迭代实现（使用栈）

**核心思想**：用栈模拟递归调用

```java
public List<Integer> preorderTraversalIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) {
        return result;
    }

    Stack<TreeNode> stack = new Stack<>();
    stack.push(root);

    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        result.add(node.val);  // 访问根节点

        // 右子树先入栈（后处理）
        if (node.right != null) {
            stack.push(node.right);
        }
        // 左子树后入栈（先处理）
        if (node.left != null) {
            stack.push(node.left);
        }
    }

    return result;
}
```

**复杂度分析**:
- 时间复杂度: O(n)
- 空间复杂度: O(n) - 最坏情况栈存储所有节点

---

### 🔍 两种解法对比

| 解法 | 时间复杂度 | 空间复杂度 | 优点 | 缺点 |
|------|------------|------------|------|------|
| **递归** | O(n) | O(h) | 代码简洁，易理解 | 递归深度过大可能栈溢出 |
| **迭代** | O(n) | O(n) | 无递归开销 | 代码稍复杂 |

---

### ⚠️ 常见错误

**1. 递归终止条件遗漏**
```java
// ❌ 错误代码：没有检查 null
private void preorder(TreeNode node, List<Integer> result) {
    result.add(node.val);  // NPE！
    preorder(node.left, result);
    preorder(node.right, result);
}

// ✅ 正确代码
if (node == null) return;
```

**2. 迭代法入栈顺序错误**
```java
// ❌ 错误代码：左子树先入栈
if (node.left != null) stack.push(node.left);
if (node.right != null) stack.push(node.right);
// 结果：右子树先被访问（变成根-右-左）

// ✅ 正确代码：右子树先入栈
if (node.right != null) stack.push(node.right);
if (node.left != null) stack.push(node.left);
```

**3. 理解错误：混淆三种遍历**
```
前序遍历：根 → 左 → 右
中序遍历：左 → 根 → 右
后序遍历：左 → 右 → 根
```

---

### 📝 学习笔记

---

## 一、类加载双亲委派机制

### 1.1 三层类加载器

```
┌─────────────────────────────────────────────────────────────────┐
│                    类加载器层级结构                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   启动类加载器                          │
│   - 加载: JDK 核心库                        │
│   - 路径: JAVA_HOME/lib/rt.jar                              │
│   - C++ 实现，Java 无法获取                                  │
│     ┌───────────────────────────────────────────────┐          │
│     │     扩展类加载器              │          │
│     │     - 加载: 扩展库                             │          │
│     │     - 路径: JAVA_HOME/lib/ext/*.jar           │          │
│     │       ┌─────────────────────────────────┐     │          │
│     │       │  应用类加载器        │     │          │
│     │       │  - 加载: 你写的类                │     │          │
│     │       │  - 路径: classpath / 项目目录    │     │          │
│     │       └─────────────────────────────────┘     │          │
│     └───────────────────────────────────────────────┘          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

### 1.2 双亲委派工作流程

```
1. 应用类加载器收到加载请求
       ↓
2. 自己不加载，委托给扩展类加载器
       ↓
3. 扩展类加载器也不加载，委托给启动类加载器
       ↓
4. 启动类加载器尝试加载
   - 成功 → 返回 Class 对象
   - 失败 → 通知扩展类加载器
       ↓
5. 扩展类加载器尝试加载
   - 成功 → 返回
   - 失败 → 通知应用类加载器
       ↓
6. 应用类加载器尝试加载
   - 成功 → 返回
   - 失败 → 抛出 ClassNotFoundException
```

---

### 1.3 源码层面实现

**ClassLoader.loadClass() 源码**：

```java
protected Class<?> loadClass(String name, boolean resolve) {
    synchronized (getClassLoadingLock(name)) {
        // 1. 检查是否已加载
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            try {
                if (parent != null) {
                    // 2. 委托给父加载器（双亲委派的关键）
                    c = parent.loadClass(name, false);
                } else {
                    // 3. 委托给启动类加载器
                    c = findBootstrapClassOrNull(name);
                }
            } catch (ClassNotFoundException e) {
                // 父加载器无法加载
            }

            if (c == null) {
                // 4. 父加载器都找不到，自己加载
                c = findClass(name);
            }
        }
        return c;
    }
}
```

---

### 1.4 双亲委派的好处

**1. 安全性**：防止核心类被篡改
```
用户尝试使用 java.lang.String
    ↓
应用类加载器委托给扩展类加载器
    ↓
扩展类加载器委托给启动类加载器
    ↓
启动类加载器从 rt.jar 加载真正的 String
    ↓
加载成功，直接返回 ✓
    ↓
❌ 用户自定义的 String 根本不会被加载
```

**2. 一致性**：避免同一个类被多次加载

---

## 二、打破双亲委派

### 2.1 为什么需要打破？

**场景：Tomcat 多应用隔离**

问题：两个 Web 应用用不同版本的 Spring
- App A 用 Spring 5.x
- App B 用 Spring 6.x

如果用双亲委派：
```
1. App A 启动，父加载器加载了 Spring 5.x
2. App B 启动，父加载器说"已加载"（其实是 5.x）
3. ❌ App B 被迫使用 Spring 5.x，冲突！
```

解决方案：打破双亲委派
- 每个 Web 应用有自己的类加载器
- **优先加载自己的类**，找不到才委托给父加载器

---

### 2.2 如何打破双亲委派？

**重写 loadClass() 方法，改变加载顺序**：

```java
@Override
protected Class<?> loadClass(String name, boolean resolve) {
    // 1. 检查是否已加载
    Class<?> c = findLoadedClass(name);
    if (c == null) {
        try {
            // 2. 先尝试自己加载（打破双亲委派）
            c = findClass(name);
        } catch (ClassNotFoundException e) {
            // 3. 自己找不到，才委托给父加载器
            if (parent != null) {
                c = parent.loadClass(name, false);
            }
        }
    }
    return c;
}
```

---

### 2.3 Tomcat 的安全设计

**Tomcat 打破双亲委派，但保留了核心类的安全**：

```
┌─────────────────────────────────────────────────────────────────┐
│  Tomcat WebAppClassLoader 的加载策略                           │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  对于 JDK 核心类（java.*、javax.* 等）：                         │
│    → 仍然遵循双亲委派                                           │
│    → 最终交给启动类加载器加载                                    │
│    → ✅ 无法被恶意类替换                                        │
│                                                                 │
│  对于应用类（如 Spring、用户代码）：                              │
│    → 打破双亲委派                                               │
│    → 优先自己加载                                               │
│    → ✅ 实现应用隔离                                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

### 2.4 打破双亲委派的场景

| 场景 | 原因 |
|------|------|
| **Tomcat** | 多应用隔离，不同版本共存 |
| **SPI (JDBC)** | 接口在 JDK，实现在第三方 jar |
| **OSGi** | 模块化热部署 |

---

## 三、常见面试题

### Q1: 什么是双亲委派机制？

**标准答案**：当一个类加载器收到类加载请求时，它首先不会自己去尝试加载这个类，而是把这个请求委派给父类加载器去完成，只有当父加载器反馈自己无法完成这个加载请求（找不到类）时，子加载器才会尝试自己去加载。

### Q2: 为什么要使用双亲委派？

**标准答案**：
1. **安全性**：防止核心类被篡改（如 `java.lang.String`）
2. **一致性**：避免同一个类被多次加载

### Q3: 如何打破双亲委派？

**标准答案**：重写 `loadClass()` 方法，改变加载顺序为：先自己加载，找不到再委托给父加载器。

### Q4: 哪些场景需要打破双亲委派？

**标准答案**：
1. **Tomcat**：多应用隔离，每个应用用自己的类版本
2. **SPI**：如 JDBC，接口在 JDK（`java.sql.Driver`），实现在第三方 jar（`com.mysql.jdbc.Driver`），需要加载第三方类
3. **OSGi**：模块化热部署

### Q5: Tomcat 打破双亲委派会有安全问题吗？

**标准答案**：不会。Tomcat 只对应用类打破双亲委派，对于 JDK 核心类（`java.*`、`javax.*`），仍然遵循双亲委派，最终交给启动类加载器加载，无法被恶意类替换。

---

## 四、常见误区

### ❌ 误区1：双亲委派是指类之间有父子关系

```
正确理解：
双亲委派是指"类加载器"之间有父子关系，不是类之间有父子关系。
- 启动类加载器是扩展类加载器的父亲
- 扩展类加载器是应用类加载器的父亲
```

---

### ❌ 误区2：打破双亲委派就不安全了

```
正确理解：
打破双亲委派不等于不安全。
- Tomcat 只对应用类打破双亲委派
- 对于核心类仍然遵循双亲委派
- 因此既能实现应用隔离，又能保证核心类安全
```

---

### ❌ 误区3：类加载器的父加载器是继承关系

```
正确理解：
类加载器的父子关系不是继承关系，而是组合关系。
- 每个类加载器有一个 parent 字段指向父加载器
- 不是 extends 父加载器
```

---

## 🎯 今日技能评估

| 技能点 | 状态 | 说明 |
|--------|------|------|
| LeetCode 144 (二叉树前序遍历) | ✅ Proficient | 递归 + 迭代两种实现 |
| 类加载双亲委派机制 | ✅ Proficient | 三层加载器 + 委派流程 + 源码 |
| 打破双亲委派的场景 | ✅ Proficient | Tomcat、SPI、OSGi |

---

## 🏆 Phase 1 总结

**Phase 1: Java Core [Day 1-7]** ✅ **完成！**

| 技能点 | 状态 | 完成日期 | 备注 |
|--------|------|----------|------|
| HashMap 1.7/1.8 源码 | ✅ Proficient | Day-1 | 数组+链表 vs 红黑树 |
| ConcurrentHashMap 锁机制 | ✅ Proficient | Day-2 | Segment vs CAS+Synchronized |
| Synchronized 锁升级 | ✅ Proficient | Day-3 | 偏向→轻量→重量 |
| Volatile 内存语义 | ✅ Proficient | Day-3 | JMM 内存屏障 |
| ThreadPoolExecutor 7参数 | ✅ Proficient | Day-4 | 核心线程数、拒绝策略 |
| AQS 源码 (ReentrantLock) | ✅ Proficient | Day-4 | CLH 队列、Condition |
| CAS & ABA 问题 | ✅ Proficient | Day-5 | Unsafe 类、AtomicStampedReference |
| AQS Condition | ✅ Proficient | Day-5 | await/signal、双队列 |
| JVM 内存模型 | ✅ Proficient | Day-6 | 堆、栈、方法区、GC Root |
| GC 算法 (CMS/G1) | ✅ Proficient | Day-6 | 标记-清除/复制/标记-整理、CMS/G1 |
| 类加载双亲委派 | ✅ Proficient | Day-7 | 三层加载器、打破双亲委派 |
| **阶段进度** | **11/11** | ✅ **Phase 1 完成！** | |

---

## 🚀 明日计划 (Day 8)

1. **Phase 2 启动**：MySQL 基础
   - 数据库索引原理
   - B+树 vs B树

2. **算法**：二叉树专题（中序遍历、后序遍历）
