# 🚀 Big-Tech Hunter - 50天 Java 后端特训计划

> 借助 Claude Code 的 AI 辅助学习方法，系统化提升至字节/阿里 P6 水平

## 📖 项目简介

这是一个为期 50 天的 Java 后端工程师特训项目，目标是通过 **AI 辅助 + 苏格拉底式教学** 的方式，系统掌握：
- ✅ Java 核心技术（JUC、JVM、集合框架）
- ✅ 中间件原理（MySQL、Redis、Spring）
- ✅ 分布式 RPC 框架开发（Netty、服务治理）
- ✅ 算法与数据结构（LeetCode 50+ 题）
- ✅ 面试能力（手撕代码、系统设计、原理深挖）

**核心特色**：
- 🎓 **苏格拉底式教学**：AI 不直接给答案，而是通过提问引导你自己思考
- 📝 **状态持久化**：每日日志 + 进度跟踪，形成可复盘的学习档案
- 🔥 **高强度实战**：每日算法 + 技术深挖 + 代码实战，拒绝纸上谈兵
- 🛡️ **工程化规范**：对标大厂标准（Checkstyle、单测覆盖率、Git 规范）

---

## 🏗️ 项目结构

```
Improvement/
├── CLAUDE.md              # AI 助手的核心指令（教学协议、评估标准、红线规范）
├── 50Days.md              # 50 天完整路线图
├── 每日特训/
│   ├── Day-1.md           # 每日学习日志（算法 + 技术 + 总结）
│   ├── Day-2.md
│   └── ...
├── 总学习进度/
│   ├── 总学习进度.md       # 技能点雷达图（Gap/Mastered 状态）
│   └── 技术债务.md         # 遗留问题追踪
├── 知识总结/
│   ├── 算法模板总结.md
│   ├── Java核心知识总结.md
│   └── 面试高频题汇总.md
├── 代码练习/              # 算法题代码实现
├── 工程规范/
│   └── checkstyle.xml     # 阿里巴巴 Java 规约
└── 输出成果/              # Light-RPC 项目代码
```

---

## 🎯 快速开始

### 1. 环境准备

**必需工具**：
- [Claude Code CLI](https://github.com/anthropics/claude-code) - AI 编程助手
- Git - 版本控制
- JDK 17+ - Java 开发环境
- Maven/Gradle - 构建工具

**前置技能检查**（Day 0）：
```bash
# 检查 Git 基础
git rebase --help
git cherry-pick --help

# 检查 Maven 配置
mvn dependency:tree

# 检查 Linux 命令
netstat -tunlp | grep 8080
```

### 2. 克隆项目

```bash
git clone <your-repo-url>
cd Improvement
```

### 3. 配置 Claude Code

将 `CLAUDE.md` 放置在项目根目录（已包含），Claude Code 会自动读取这份指令。

**关键配置说明**：
- `CLAUDE.md` 定义了 AI 的教学风格（苏格拉底式提问）
- 定义了代码规范红线（禁止 `System.out.println`、魔法值等）
- 定义了每日日志的标准格式

### 4. 开始第一天

在 Claude Code 中输入：
```
/start
```

AI 会：
1. 读取你的学习进度
2. 锁定今日目标
3. 开始算法热身 + 技术深挖

---

## 📅 学习流程

### 每日标准流程（2-3 小时）

```
┌─────────────────────────────────────────┐
│ 1. Check-in (5min)                      │
│    - AI 读取昨日进度和技术债务           │
│    - 确定今日目标                        │
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│ 2. 算法热身 (15min)                      │
│    - 每日 1 题 LeetCode                  │
│    - 要求：2 种解法 + 复杂度分析          │
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│ 3. 技术深挖 (15min)                      │
│    - AI 苏格拉底式追问                   │
│    - 必须下探到源码/OS 层面               │
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│ 4. 代码实战 (40min+)                     │
│    - 生产级代码编写                      │
│    - 通过 Checkstyle + 单测               │
└─────────────────────────────────────────┘
           ↓
┌─────────────────────────────────────────┐
│ 5. Review & Update (10min)              │
│    - 更新每日日志                        │
│    - 标记 Mastered/Gap 技能点            │
└─────────────────────────────────────────┘
```

### 每日日志模板

参考 `每日特训/Day-1.md` 的格式，必须包含：

**算法部分**：
- 题目描述 + 示例
- 解法一（暴力）+ 解法二（优化）
- 遍历过程演示表格
- 复杂度对比
- 常见错误（3 个以上）

**技术部分**：
- ASCII 图示说明结构/流程
- 表格对比不同方案
- 完整代码示例（带注释）
- 面试高频题（5 题以上）
- 常见误区（3 个以上）

**总结部分**：
- 技能评估表格
- 明日计划

---

## 🎓 核心理念：苏格拉底式教学

### ❌ 传统学习方式（填鸭式）
```
你：HashMap 的底层原理是什么？
AI：HashMap 底层是数组 + 链表 + 红黑树...（直接讲解）
```

### ✅ 本项目方式（引导式）
```
你：HashMap 的底层原理是什么？
AI：你觉得为什么需要用数组而不是链表作为底层结构？
你：因为数组可以 O(1) 访问...
AI：那为什么还需要链表？直接用数组不行吗？
你：因为会有哈希冲突...
AI：很好！那为什么 JDK 1.8 又引入了红黑树？
```

**核心原则**：
- AI 永远不直接给答案，而是通过提问引导你思考
- 拒绝浅层回答，必须追问到底层原理（OS、CPU、源码）
- 你必须自己写代码，AI 只做 Code Review

---

## 🛑 零容忍红线

以下代码会被 AI 立即打断并要求重写：

### 1. Dirty Code
```java
// ❌ 禁止
System.out.println("debug");
e.printStackTrace();
if (type == 1) { ... }  // 魔法值
List list = new ArrayList();  // 无语义命名

// ✅ 正确
log.info("User login: {}", userId);
log.error("Failed to connect", e);
if (type == UserType.VIP) { ... }
List<User> activeUsers = new ArrayList<>();
```

### 2. Concurrency Suicide
```java
// ❌ 禁止
new Thread(() -> { ... }).start();  // 显式创建线程
SimpleDateFormat sdf = new SimpleDateFormat();  // 非线程安全
ThreadLocal<User> context = new ThreadLocal<>();  // 未 remove()

// ✅ 正确
ExecutorService executor = new ThreadPoolExecutor(...);
DateTimeFormatter formatter = DateTimeFormatter.ofPattern(...);
try {
    context.set(user);
    // ...
} finally {
    context.remove();
}
```

### 3. Architecture
```java
// ❌ 禁止
@Controller
public class UserController {
    public void register() {
        // 直接在 Controller 写业务逻辑
        userDao.insert(...);
    }
}

// ✅ 正确
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    public Result register() {
        return userService.register(...);
    }
}
```

---

## 📊 技能评估标准

| 等级 | 定义 | 面试表现 | 判定标准 |
|------|------|----------|----------|
| **Mastered** | 能讲清原理、手写实现、指导他人 | 可应对 P7 级别追问 | 能说出 3 种以上实现方案 |
| **Proficient** | 理解原理、熟练应用、能排错 | 可应对 P6 级别深挖 | 能讲清源码核心流程 |
| **Familiar** | 了解概念、能使用、需查文档 | 可应对 P5 基础问答 | 知道是什么，怎么用 |
| **Gap** | 仅听说过、未实践 | 面试风险点 | 需要立即补齐 |

在 `总学习进度/总学习进度.md` 中持续更新你的技能点状态。

---

## 🛠️ 常用指令

在 Claude Code 中使用以下指令：

| 指令 | 功能 | 示例 |
|------|------|------|
| `/start` | 开始今日特训 | `/start` |
| `/algo [tag]` | 随机抽取算法题 | `/algo 双指针` |
| `/drill [topic]` | 深度原理拷问 | `/drill ConcurrentHashMap` |
| `/review` | 代码审查（P6 级） | `/review` |
| `/log` | 生成今日日志模板 | `/log` |
| `/block` | 记录技术债务 | `/block 不理解 AQS 的 CLH 队列` |
| `/mock` | 模拟面试 | `/mock` |

---

## 📈 50 天路线图

### Phase 1: Java Core & Algo Base (Day 1-10)
- 算法：数组、链表、双指针
- 技术：HashMap、ConcurrentHashMap、AQS、ThreadPool、JVM

### Phase 2: Middleware & Algo Adv (Day 11-20)
- 算法：二叉树、回溯、DFS/BFS
- 技术：MySQL（索引、MVCC、锁）、Redis（数据结构、持久化、缓存）、Spring（Bean 生命周期、AOP）

### Phase 3: Light-RPC Dev (Day 21-38)
- 算法：DP、贪心、图论
- 技术：Netty（Reactor、零拷贝）、RPC 协议、服务治理（注册中心、负载均衡、熔断）

### Phase 4: Release & Verify (Day 39-42)
- 发布 Light-RPC v0.1.0
- JMH 压测（目标：QPS > 10k, P99 < 10ms）

### Phase 5: Resume & Mock (Day 43-50)
- 简历优化（STAR 法则）
- 全真模拟面试（3 轮）

详见 `50Days.md`。

---

## 💡 使用建议

### 给同学的建议

1. **不要跳过 Day 0 的前置检查**
   Git、Maven、Linux 基础必须过关，否则后面会卡壳。

2. **严格遵循每日日志格式**
   这不是形式主义，而是你复习的唯一依据。面试前翻看日志，能快速回忆所有知识点。

3. **拥抱苏格拉底式教学**
   一开始会不适应（AI 不直接给答案），但这是最有效的深度学习方式。坚持 3 天就会习惯。

4. **代码必须自己写**
   AI 只做 Code Review，不要复制粘贴 AI 的代码。手写一遍才是你的。

5. **技术债务要及时清理**
   遇到不懂的立即记录到 `技术债务.md`，周末集中攻克。

6. **Phase 结束后做归档**
   每 10 天将日志归档到 `archive/`，防止 Context 溢出。

---

## 🤝 贡献与反馈

如果你在使用过程中有改进建议，欢迎：
- 提交 Issue
- 提交 PR（优化日志模板、补充知识点）
- 分享你的学习心得

---

## 📄 License

本项目仅供个人学习使用，请勿用于商业用途。

---

## 🙏 致谢

- [Claude Code](https://github.com/anthropics/claude-code) - AI 编程助手
- [LeetCode](https://leetcode.cn/) - 算法题库
- [阿里巴巴 Java 开发手册](https://github.com/alibaba/p3c) - 代码规范

---

**开始你的 50 天特训之旅吧！** 🚀

```bash
# 在 Claude Code 中输入
/start
```
