# Java 核心知识总结 (Day 1-7)

> Phase 1 核心知识点速查手册

---

## 1. HashMap

### 核心原理
- **JDK 1.7**: 数组 + 链表
- **JDK 1.8**: 数组 + 链表 + 红黑树

### 红黑树转换
- 链表长度 ≥ 8 且数组长度 ≥ 64
- 查找效率：O(n) → O(log n)

### 面试题
**Q: JDK 1.7 和 1.8 的区别？**
A: 数据结构（增加红黑树）、插入方式（头插→尾插）、扩容优化

---

## 2. ConcurrentHashMap

### JDK 1.7: Segment 分段锁
- 16个Segment，每个Segment是一个小HashMap
- 并发度 = Segment数量

### JDK 1.8: CAS + Synchronized
- 锁粒度更细（锁定数组元素）
- 并发度 = 数组长度

### 面试题
**Q: 如何保证线程安全？**
A: 1.7用Segment分段锁，1.8用CAS+Synchronized锁定数组元素

---

## 3. JUC 并发

### Synchronized 锁升级
无锁 → 偏向锁 → 轻量级锁 → 重量级锁

### Volatile
- 可见性
- 禁止指令重排序

### ThreadPoolExecutor 7参数
1. corePoolSize
2. maximumPoolSize
3. keepAliveTime
4. unit
5. workQueue
6. threadFactory
7. handler（拒绝策略）

### AQS
- state变量 + CLH队列
- CAS获取锁，失败则入队阻塞

### AQS Condition
- await()：释放锁，进入等待队列
- signal()：唤醒等待队列中的节点
- signalAll()：唤醒所有节点

### CAS & ABA 问题
- CAS：Compare And Swap，乐观锁机制
- ABA问题：线程A修改值为B，线程B又改回A，线程C误以为未被修改
- 解决方案：使用 AtomicStampedReference（带版本号）或 AtomicMarkableReference

### Synchronized 锁升级
```
无锁 → 偏向锁 → 轻量级锁 → 重量级锁
```
- 偏向锁：第一次获取锁时，记录线程ID
- 轻量级锁：自旋 CAS 抢锁
- 重量级锁：阻塞抢锁，涉及用户态/内核态切换

---

## 4. JVM

### 内存模型
- 线程共享：堆、方法区
- 线程私有：虚拟机栈、本地方法栈、程序计数器

### 对象内存布局
```
┌─────────────────────────────────────────┐
│  对象头 (Object Header)                  │
│  ├── Mark Word (8字节)                  │
│  │   ├── 哈希码、GC年龄、锁状态           │
│  └── 类型指针 (4/8字节)                  │
├─────────────────────────────────────────┤
│  实例数据                               │
│  ├── 基本类型：int(4)、long(8)、boolean(1) │
│  └── 引用类型：4/8字节                   │
├─────────────────────────────────────────┤
│  对齐填充 (8字节对齐)                    │
└─────────────────────────────────────────┘
```

### GC算法
- 标记-清除：有碎片
- 复制：无碎片，浪费空间
- 标记-整理：无碎片，移动开销大

### CMS vs G1
- CMS：低停顿，有碎片
- G1：可预测停顿，无碎片

### 类加载流程
```
加载 → 验证 → 准备 → 解析 → 初始化
```
1. 加载：将.class文件加载到方法区
2. 验证：检查字节码格式
3. 准备：为静态变量分配内存并赋默认值
4. 解析：将符号引用转为直接引用
5. 初始化：执行静态代码块，为静态变量赋值

### 类加载双亲委派
```
Bootstrap → Extension → Application → 自定义
```
先委托父加载器，父加载器无法加载才自己加载

### 打破双亲委派场景
1. JDBC DriverManager（SPI机制）
2. Tomcat（多应用隔离）
3. OSGi（模块化加载）
4. Spring（动态加载）

---

## 面试高频题精选

### HashMap
1. 底层实现原理？
2. 为什么链表长度8转红黑树？
3. 扩容机制？
4. 为什么线程不安全？

### ConcurrentHashMap
1. 如何保证线程安全？
2. 1.7和1.8的区别？
3. get操作需要加锁吗？

### JUC
1. Synchronized锁升级过程？
2. Volatile的作用？
3. 线程池执行流程？
4. AQS原理？
5. CAS的ABA问题？

### JVM
1. 内存模型？
2. GC Roots有哪些？
3. Minor GC和Full GC的区别？
4. 类加载双亲委派？

---

**最后更新**: 2026-03-10
