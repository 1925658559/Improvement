# 🚀 50天 Java 后端大厂特训计划 (Big-Tech Hunter 版)

**学员**：徐迎庆  
**当前背景**：网络工程专业 / 熟悉网络协议底层 / 项目偏旧 (JSP/Servlet)  
**目标**：合肥 -> 江浙沪 (字节/阿里/腾讯等级别后端岗位)  
**核心战略**：以 RPC 框架发挥网络专业优势，以 JUC/JVM 补齐 CS 基础短板。

---

## 📅 每日作息建议 (Hell Mode)
> **警告**：这50天没有周末，没有游戏。
* **08:00 - 09:30**：🧠 **算法时间** (清醒时刻刷 LeetCode)
* **09:30 - 12:30**：📚 **硬核输入** (看书、读源码、理解底层原理)
* **14:00 - 18:00**：💻 **项目实战** (手写 RPC，Debug，踩坑)
* **20:00 - 22:00**：📝 **复盘与输出** (整理笔记，模拟面试自问自答)

---

## 阶段一：内功重塑 (Java Core) [Day 1 - 10]
**目标**：从“会用 API”到“懂底层源码”。

### Day 1-2: 集合框架 (Collection)
- [ ] **HashMap 源码**：手画 1.7 (数组+链表) 与 1.8 (红黑树) 结构图。
- [ ] **核心流程**：精通 `put()` 扩容机制、Hash 算法、寻址过程。
- [ ] **ConcurrentHashMap**：理解分段锁 (Segment) 与 CAS + Synchronized 的区别。
- [ ] **面试题**：为什么 HashMap 线程不安全？扩容因子为什么是 0.75？

### Day 3-6: 并发编程 (JUC) —— *重灾区*
- [ ] **Day 3 (锁)**：`Synchronized` 锁升级 (偏向->轻量->重量)；`Volatile` 内存屏障与可见性。
- [ ] **Day 4 (线程池)**：**ThreadPoolExecutor** 7大参数详解；4种拒绝策略；手写一个 OOM 场景。
- [ ] **Day 5 (AQS)**：`ReentrantLock` 源码；CLH 队列原理；`Condition` 等待/通知机制。
- [ ] **Day 6 (原子类)**：CAS 原理 (Unsafe 类)；ABA 问题及解决方案 (`AtomicStampedReference`)。

### Day 7-9: JVM 虚拟机
- [ ] **Day 7 (内存)**：堆、栈、方法区、程序计数器；对象创建与内存分配流程。
- [ ] **Day 8 (GC)**：GC Roots 可达性分析；CMS vs G1 区别；三色标记法。
- [ ] **Day 9 (类加载)**：双亲委派模型源码；Tomcat 如何打破双亲委派。

### Day 10: 阶段复盘 & 算法启动
- [ ] 复习前 9 天所有笔记（尝试口述）。
- [ ] **启动算法**：LeetCode Hot 100 开始，每日 2-3 题，雷打不动。

---

## 阶段二：存储与中间件 (Middleware) [Day 11 - 20]
**目标**：证明你懂“海量数据”处理。

### Day 11-14: MySQL 高阶
- [ ] **Day 11 (索引)**：B+树结构详解（必须能画）；聚簇 vs 非聚簇索引；覆盖索引。
- [ ] **Day 12 (调优)**：`Explain` 命令详解；最左前缀法则；索引失效场景。
- [ ] **Day 13 (事务)**：ACID 特性；隔离级别；**MVCC** 版本链与 ReadView 原理。
- [ ] **Day 14 (锁)**：行锁、间隙锁 (Gap Lock)、临键锁；死锁排查。

### Day 15-18: Redis 缓存
- [ ] **Day 15 (基础)**：SDS, SkipList (跳表) 原理；Redis 为什么快 (IO多路复用)。
- [ ] **Day 16 (持久化)**：RDB vs AOF 优缺点及重写机制。
- [ ] **Day 17 (高可用)**：主从复制原理；Sentinel 哨兵；Cluster 集群槽位 (16384)。
- [ ] **Day 18 (实战坑)**：缓存穿透 (布隆过滤器)、击穿、雪崩；双写一致性策略 (延时双删)。

### Day 19-20: Spring 核心
- [ ] **IOC/AOP**：循环依赖的三级缓存解决方案；动态代理 (JDK vs CGLIB)。
- [ ] **Spring Boot**：`@SpringBootApplication` 自动装配原理；Starter 编写流程。

---

## 阶段三：核武器打造 (Light-RPC) [Day 21 - 35]
[cite_start]**目标**：结合网络工程背景 [cite: 6][cite_start]，手写高性能 RPC 框架，替换简历中的 JSP 电商项目 [cite: 74]。

### Day 21-23: Netty 网络编程基础
- [ ] **BIO vs NIO**：理解阻塞与非阻塞区别；Selector, Channel, Buffer 三大件。
- [ ] **Reactor 模型**：单线程/多线程/主从多线程 Reactor 模型原理。
- [ ] **Hello Netty**：实现一个简单的 Server/Client 字符串收发。

### Day 24-26: 通信协议设计 (Protocol)
- [ ] **自定义协议**：设计魔数、版本号、请求类型、序列化方式、数据长度。
- [ ] **解决 TCP 问题**：利用 `LengthFieldBasedFrameDecoder` 解决粘包/拆包。

### Day 27-28: 序列化 (Serialization)
- [ ] **集成算法**：引入 Protobuf 或 Hessian。
- [ ] **性能对比**：编写测试用例，对比 Java 原生序列化与 Protobuf 的字节大小。

### Day 29-31: 注册中心 (Registry)
- [ ] **集成 Zookeeper/Nacos**：实现服务注册与发现。
- [ ] **客户端缓存**：实现服务列表的本地缓存 (Local Cache) + Watch 机制更新。

### Day 32-33: 代理与负载均衡
- [ ] **动态代理**：使用 JDK 动态代理屏蔽网络细节，实现 `Interface` 直接调用。
- [ ] **负载均衡**：手写轮询 (RoundRobin)、随机 (Random)、一致性 Hash 算法。

### Day 34-35: 整合与优化
- [ ] **Spring集成**：编写 `@RpcService` 注解与 Spring Bean 后置处理器。
- [ ] **从头跑通**：确保 Consumer 能成功调用 Provider 并拿到结果。

---

## 阶段四：简历重构与冲刺 [Day 36 - 50]
**目标**：学会“包装”与“表达”。

### Day 36-38: 简历大修
- [ ] [cite_start]**删除**：彻底移除 JSP/Servlet 电商项目 [cite: 74]。
- [ ] **置顶 RPC**：使用 STAR 法则描述 Light-RPC 项目（强调 Netty、零拷贝、并发模型）。
- [ ] [cite_start]**改造安全项目**：将深度学习检测系统 [cite: 97] 包装为“高并发流量清洗中心”，强调 QPS 和 实时性。

### Day 39-45: 场景题与面经
- [ ] **搜集面经**：牛客网/Github 搜索“字节 Java 面经”。
- [ ] **场景模拟**：
    - Redis 挂了数据库被打死怎么办？
    - 消息队列积压怎么处理？
    - 秒杀系统如何设计 (限流/削峰)？

### Day 46-50: 终极冲刺
- [ ] **算法二刷**：LeetCode Hot 100 错题重做。
- [ ] **全真模拟**：找朋友或对着镜子进行 Mock Interview，录音复盘。

---

## 📚 必备资源清单
1.  **书籍**：
    * 《Java并发编程的艺术》 (死磕 JUC)
    * 《深入理解Java虚拟机（第3版）》 (死磕 JVM)
    * 《Redis设计与实现》 (理解数据结构)
2.  **实战参考 (Github)**：
    * 搜索 `netty-rpc`, `guide-rpc-framework` (参考架构，**严禁直接 Copy**)
3.  **刷题**：
    * LeetCode Hot 100 / 剑指 Offer

> **Big-Tech Hunter 寄语**：
> 徐同学，你的网络工程底子是很好的护城河。这 50 天会很痛苦，但当你能亲手写出 RPC 框架并讲清楚 Netty 线程模型时，你将超越 90% 的校招竞争者。**执行力决定命运。**