# 📅 每日特训日志

**日期**：Day-5
**第几天**：Day 5
**当前阶段**：Phase 1 - Java Core (内功重塑)

---

## ⏰ CRU - Step 1: Check-in (状态同步)

### 📌 今日攻击目标 (Target)
1. LeetCode 141 - Linked List Cycle (快慢指针)
2. CAS & ABA 问题 (Unsafe 类, AtomicStampedReference)
3. AQS Condition 深入 (await/signal 原理)
4. 代码实战：手写简易版 AQS

### 🔍 昨日回顾
- ✅ ThreadPoolExecutor 核心原理 → Proficient
- ✅ AQS 基础 (state/CLH/acquire/release) → Proficient
- ✅ LeetCode 21 (Merge Two Sorted Lists) → Pass

### ⚠️ 技术债务检查
- 无

---

## ⚔️ CRU - Step 2: Drill (高压训练)

### 🧮 算法热身 (15min)

**题目**: LeetCode 141 - Linked List Cycle
**链接**: https://leetcode.cn/problems/linked-list-cycle/
**状态**: ✅ Pass

**题目描述**:
给你一个链表的头节点 `head`，判断链表中是否有环。

**解法**: 快慢指针 (Floyd 判圈算法)

**核心思想**:
- 慢指针每次走 1 步
- 快指针每次走 2 步
- 如果有环，快指针一定会追上慢指针
- 如果无环，快指针会先到达 null

**为什么快慢指针一定会相遇？**
假设环的长度为 C，快慢指针的距离每轮缩小 1 步，最多 C 轮后一定相遇。

**代码实现**:
```java
public class Solution {
    public boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }

        ListNode slow = head;
        ListNode fast = head.next;

        while (slow != fast) {
            if (fast == null || fast.next == null) {
                return false;
            }
            slow = slow.next;
            fast = fast.next.next;
        }
        return true;
    }
}
```

**复杂度分析**:
- 时间复杂度: O(n)
- 空间复杂度: O(1)

**耗时**: ~15min (包括苏格拉底式引导讨论)

---

## 📝 学习笔记

### 一、CAS & ABA 问题

#### 1.1 CAS 原理

**CAS (Compare And Swap)**：比较并交换，是一种无锁算法。

**三个操作数**:
- V: 内存值 (Value)
- E: 预期值 (Expect)
- N: 新值 (New)

**操作流程**:
```
if (V == E) {
    V = N;  // 修改成功
    return true;
} else {
    // 修改失败
    return false;
}
```

**底层实现**：
```java
unsafe.compareAndSwapInt(obj, offset, expect, update);
```

#### 1.2 CAS 的优缺点

**优点**:
- 不需要切换 CPU 状态（用户态操作）
- 竞争不激烈时性能优于 synchronized
- 不会造成死锁

**缺点**:
1. **ABA 问题**：值从 A→B→A，CAS 无法察觉
2. **自旋消耗 CPU**：竞争激烈时会空转
3. **只能保证单个变量**的原子性
4. **只适合简单业务逻辑**

#### 1.3 ABA 问题

**问题场景**（链表节点删除）:
```
初始: head → A → B → C

线程1（删除A）:
1. 读取 head.next = A
2. [被挂起]

线程3:
3. 删除 A: head → B → C
4. 删除 B: head → C
5. 插入 A: head → A → C

线程1（恢复）:
6. 检查 head.next == A ✅
7. CAS 成功！head.next = B
8. 结果: head → B → C (但 B 已经不在链表中了！)
```

**问题本质**:
- CAS 检查的是**内存地址**（A 的地址没变）
- 无法察觉 A 的**内容**已经变化（A.next 从 B 变成了 C）
- 基于"值没变"的假设做出的操作可能是错误的

#### 1.4 AtomicStampedReference - 解决 ABA

**核心思想**: 引入**版本号** (Stamp)

**Pair 结构**:
```java
private static class Pair<T> {
    final T reference;  // 实际的值
    final int stamp;    // 版本号
}
```

**CAS 操作**:
```java
// 同时比较值和版本号
if (reference == expectedRef && stamp == expectedStamp) {
    reference = newRef;
    stamp = newStamp;
    return true;
}
```

**为什么能解决 ABA**:
```
初始: A(版本1) → B(版本2) → A(版本3)

CAS 检查:
- 值是 A ✅
- 版本是 3 ❌ (期望版本1)
- CAS 失败！
```

**代码示例**: 见 `AtomicStampedReferenceDemo.java`

#### 1.5 面试题

**Q1: CAS 是什么？**
CAS (Compare And Swap) 是一种无锁算法，通过比较内存值与预期值来决定是否更新。

**Q2: ABA 问题是什么？如何解决？**
ABA 问题：值从 A→B→A 变回原样，CAS 无法察觉变化。
解决：使用 AtomicStampedReference，增加版本号，每次修改版本号+1。

**Q3: AtomicStampedReference 内部怎么存储值和版本号？**
使用 Pair 内部类封装，Pair 对象用 volatile 修饰，CAS 时比较整个 Pair 对象的引用。

---

### 二、AQS Condition 深入

#### 2.1 Condition vs Object.wait/notify

| 维度 | synchronized (Object) | ReentrantLock (Condition) |
|------|----------------------|---------------------------|
| 等待队列 | 1 个 | 多个（可精确唤醒） |
| 唤醒精度 | 随机唤醒 | 精确唤醒指定队列 |
| 使用场景 | 简单同步 | 复杂场景（生产者-消费者） |

**示例**: 生产者-消费者问题
```java
// synchronized: notify() 唤醒随机线程（可能是生产者）
synchronized (lock) {
    lock.wait();    // 等待
    lock.notify();  // 随机唤醒
}

// Condition: 精确唤醒消费者
Lock lock = new ReentrantLock();
Condition notEmpty = lock.newCondition();  // 消费者队列
Condition notFull = lock.newCondition();   // 生产者队列

lock.lock();
try {
    notFull.await();     // 生产者等待
    notEmpty.signal();   // 唤醒消费者
} finally {
    lock.unlock();
}
```

#### 2.2 AQS 双队列结构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    AQS 双队列结构                                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  【同步队列 (Sync Queue) - CLH 队列】                                         │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐                              │
│  │   HEAD   │ ←→ │  Node-1  │ ←→ │  Node-2  │ ...                         │
│  │ (哑节点)  │    │ 等待锁   │    │ 等待锁   │                              │
│  └──────────┘    └──────────┘    └──────────┘                              │
│                                                                             │
│  【等待队列 (Condition Queue) - 单向链表】                                   │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐                              │
│  │ firstWaiter│ → │  Node-1  │ → │  Node-2  │ ...                         │
│  │ (队头)    │    │ await()  │    │ await()  │                              │
│  └──────────┘    └──────────┘    └──────────┘                              │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 2.3 await() 流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Condition.await() 流程                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  线程调用 condition.await()                                                 │
│         ↓                                                                   │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  1. 当前线程加入 Condition 等待队列（单向链表）                         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│         ↓                                                                   │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  2. 完全释放锁 (state = 0)                                            │   │
│  │     - 注意：要释放所有重入次数                                         │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│         ↓                                                                   │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  3. 阻塞当前线程 (LockSupport.park)                                   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**同步队列 → 等待队列**: 线程从 CLH 队列转移到 Condition 队列

#### 2.4 signal() 流程

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Condition.signal() 流程                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  线程调用 condition.signal()                                                │
│         ↓                                                                   │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  1. 将 Condition 等待队列的头节点转移到同步队列尾部                    │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│         ↓                                                                   │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  2. 被唤醒的线程在同步队列末尾等待，需要重新竞争锁                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  【等待队列 → 同步队列】: 线程从 Condition 队列回到 CLH 队列                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**关键点**: signal() 后，被唤醒的线程不是立即获取锁，而是需要重新排队！

#### 2.5 Node.waitStatus 状态

```java
// Node 类中的 waitStatus 常量
static final int CANCELLED =  1;  // 节点已取消
static final int SIGNAL     = -1;  // 后继节点需要被唤醒
static final int CONDITION  = -2;  // 节点在 Condition 队列中
static final int PROPAGATE  = -3;  // 共享锁传播
static final int 0          =  0;  // 初始状态
```

**CONDITION (-2)**: 节点当前在 Condition 等待队列中

#### 2.6 面试题

**Q1: Condition.await() 和 Object.wait() 有什么区别？**
1. Condition 可以创建多个，Object 只有一个等待队列
2. Condition 支持精确唤醒，Object 是随机唤醒
3. Condition 需要手动 lock/unlock，Object 是 synchronized 自动管理

**Q2: signal() 后线程立即获取锁吗？**
不是。signal() 将线程从 Condition 队列转移到同步队列末尾，需要重新竞争锁。

**Q3: AQS 内部有几个队列？两个队列的作用是什么？**
- 同步队列 (CLH)：等待获取锁的线程
- 等待队列 (Condition)：调用 await() 的线程

---

### 三、代码实战：手写简易版 AQS

**代码文件**: `SimpleAQS.java`

#### 3.1 核心组件

```java
// 1. state: 同步状态
private volatile int state;

// 2. CLH 同步队列
private volatile Node head;
private volatile Node tail;

// 3. CAS: Unsafe.compareAndSwapInt
```

#### 3.2 acquire 流程

```
acquire() → tryAcquire() → addWaiter() → acquireQueued()
    │            │              │              │
    │            │              │              └→ 在队列中自旋等待
    │            │              └→ 加入同步队列尾部
    │            └→ CAS state 0→1
    └→ 入口方法
```

#### 3.3 关键设计

**head 哑节点**:
- **逻辑上**: 代表当前持有锁的线程
- **物理上**: thread = null，帮助 GC 回收
- **结构上**: 作为队列哨兵，简化边界处理

**为什么要 head.thread = null**:
1. 表示当前节点不在"等待"状态
2. 帮助 GC 回收已结束的线程对象
3. 避免内存泄漏

#### 3.4 为什么用 CAS

**问题**: 如果 tryAcquire() 直接 `state = 1` 会怎样？

**场景**: 两个线程同时调用 tryAcquire()
```
线程1: 读取 state = 0
线程2: 读取 state = 0  ← 都认为可以获取锁
线程1: state = 1
线程2: state = 1      ← 两个线程都认为自己获取了锁！
```

**CAS 解决**:
```
线程1: CAS(0→1) → 成功
线程2: CAS(0→1) → 失败 (因为 state 已经是 1 了)
```

---

## 🎯 今日技能评估

| 技能点 | 状态 | 说明 |
|--------|------|------|
| LeetCode 141 快慢指针 | ✅ Proficient | 理解 Floyd 判圈算法原理 |
| CAS & ABA 问题 | ✅ Proficient | 理解 CAS 原理、ABA 问题场景、AtomicStampedReference 解决方案 |
| AQS Condition | ✅ Proficient | 理解双队列结构、await/signal 流程 |
| 简易版 AQS 实现 | ✅ Proficient | 理解 state、CLH 队列、CAS、acquire/release 流程 |

---

## 🚀 明日计划 (Day 6)

1. **JVM 内存模型**
   - 堆、栈、方法区
   - 对象内存布局

2. **GC 算法**
   - 三色标记
   - 可达性分析
   - CMS/G1

3. **算法**: 继续链表专题

---

## ⚖️ 今日总结

**掌握**:
- ✅ 快慢指针判断链表环
- ✅ CAS 原理及 ABA 问题
- ✅ AtomicStampedReference 版本号机制
- ✅ AQS Condition 双队列结构
- ✅ await/signal 流程

**代码产出**:
- ✅ Solution.java (快慢指针)
- ✅ AtomicStampedReferenceDemo.java (ABA 演示)
- ✅ SimpleAQS.java (简易版 AQS)

**明日重点**:
- JVM 内存模型与 GC 算法
- 算法继续链表专题
