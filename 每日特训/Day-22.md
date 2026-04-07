# Day 22 学习框架

## 今日定位

- 阶段：Phase 3 持续推进
- 学习原则：理论优先，继续沿着 `Netty -> 自定义协议 -> RPC 请求处理链` 主线推进
- 今日目标：先把昨天遗留的 `writeAndFlush` 传播路径、`Handler` 摆放顺序和 RPC 响应回写链路压实，再补上 `RpcRequest/RpcResponse` 最小字段设计、服务定位、本地服务注册表、注册中心与负载均衡的职责边界

## 今日主题

### 算法热身
- 题目：LeetCode 70 - 爬楼梯
- 目标：继续巩固 `DP` 最小状态定义、转移方程和空间压缩
- 当前状态：进行中

### 技术主线
- 主题：`writeAndFlush` 在 Netty Pipeline 中的传播机制，以及它和最小 RPC 调用链的映射
- 目标：
  - 搞清 `writeAndFlush` 是从哪里开始传播的
  - 搞清为什么出站事件不会再经过前面的解码器
  - 搞清服务端执行业务后，响应对象如何进入编码器并写回网络
  - 搞清 `RpcRequest / RpcResponse` 为什么需要这些最小字段
  - 搞清 `本地服务注册表 / 注册中心 / 负载均衡` 分别解决什么问题
- 当前状态：进行中

## 预计学习点

### 第一层：先理解现象
- 为什么请求进来时走的是解码链
- 为什么响应写回时走的是编码链
- 为什么同一条 Pipeline 里，不是每次所有 Handler 都执行

### 第二层：再理解机制
- `ctx.writeAndFlush()` 和 `channel.writeAndFlush()` 的传播起点区别
- 出站事件为什么只会寻找前面的 `Outbound Handler`
- `Decoder / Encoder / 业务 Handler` 的典型摆放顺序
- `RpcRequest / RpcResponse` 的最小字段设计
- `服务发现 / 服务定位 / 负载均衡` 的最小因果链

### 第三层：最后压成面试表达
- 如何解释 Netty 中响应回写的传播路径
- 如何解释 `writeAndFlush` 和 Handler 顺序的关系
- 如何把它映射到 RPC 返回值编码与回包过程
- 如何解释一次 RPC 调用从“找地址”到“执行业务”再到“回写响应”的完整顺序

## 算法学习记录（待补充）

### 1. 题目描述
- 题目：`LeetCode 70 - 爬楼梯`
- 要求：每次可以爬 1 阶或 2 阶，问到第 `n` 阶一共有多少种走法

### 2. 解法一
- 解法：`动态规划`
- 待补：状态定义、转移方程、边界条件

### 3. 错误思路对比
- 待补：与“只看最后一步”或“暴力递归”对比

### 4. 复杂度分析
- 待补

### 5. 常见错误
- 待补

## 技术学习记录（已完成）

### 1. 定义
- `writeAndFlush` 本质上不是“直接把数据发出去”
- 它在 Netty 中会触发一条 `出站事件传播链`
- 这条链会让响应对象沿着符合条件的 `Outbound Handler` 继续传播，直到最终写入 Socket

### 2. 实际场景
- 场景：RPC 服务端已经完成请求解码和业务执行，拿到了一个 `RpcResponse`
- 这时业务 Handler 会调用 `writeAndFlush(response)`
- 接下来 Netty 要做的，不再是“把字节流解码成对象”
- 而是把 `RpcResponse` 交给编码器，转成字节流后写回客户端

### 3. Why
- 为什么响应回写不能再走 Decoder：
  - 因为 `Decoder` 的职责是处理入站字节流，把“网络数据”变成“程序对象”
  - 但响应回写时，方向已经反过来了，当前手里拿的是“程序对象”，目标是“写回网络”
  - 所以这一步天然应该走 `Encoder` 对应的出站链，而不是 `Decoder` 对应的入站链
- 为什么必须理解传播起点：
  - 因为同样是 `writeAndFlush`
  - 从不同节点出发，能命中的 `Outbound Handler` 范围不一样
  - 这会直接决定某个 `Encoder` 是否真的会生效

### 4. 核心机制
- 第一层结论：
  - `writeAndFlush` 触发的是 `出站事件`
  - `Decoder` 属于 `Inbound Handler`
  - 所以响应回写时不会再经过 `Decoder`
- 第二层结论：
  - `ctx.writeAndFlush()`：从“当前 Handler 节点”开始，向前寻找可处理当前出站事件的 Handler
  - `channel.writeAndFlush()`：从“整条 Pipeline 尾部”开始，按完整出站链路传播
- 典型影响：
  - 如果业务 Handler 里使用的是 `ctx.writeAndFlush()`
  - 而 `Encoder` 又被放在当前业务 Handler 的后面
  - 那这次出站传播向前查找时，就可能根本命中不到这个 `Encoder`
  - 最终现象就是：看起来调用了 `writeAndFlush`，但编码器没有生效
- 最小 RPC Pipeline 顺序：
  - `LengthFieldBasedFrameDecoder`
  - `ProtocolDecoder`
  - `RpcRequestHandler`
  - `RpcResponseEncoder`
- 顺序原因：
  - 先切帧，解决半包/粘包
  - 再解析协议，把完整字节帧变成 `RpcRequest`
  - 再执行业务，得到 `RpcResponse`
  - 最后编码回写网络
- `ProtocolDecoder` 与 `RpcRequestHandler` 的职责边界：
  - `ProtocolDecoder`：解决 `字节流 -> RpcRequest`
  - `RpcRequestHandler`：解决 `RpcRequest -> 方法执行结果`
  - 前者关心“报文能不能被正确看懂”
  - 后者关心“看懂之后该怎么执行业务”
- `RpcRequestHandler` 的最小执行步骤：
  - 先根据 `serviceName` 找本地服务实例
  - 再根据 `methodName + parameterTypes` 找目标方法
  - 最后用 `parameters` 执行调用并得到结果
- 一条 RPC 请求进入服务端后的最小顺序：
  - `切帧`
  - `协议解析`
  - `反序列化`
  - `服务定位`
  - `方法调用`
  - `响应编码`
  - `回写网络`

### 4.1 `RpcRequest` 的最小字段设计
- `requestId`
  - 作用：唯一标识一次请求
  - Why：客户端并发发多个请求时，响应顺序不一定一致，必须靠它做请求-响应匹配
- `serviceName`
  - 作用：定位目标服务
  - Why：`methodName` 不具备全局唯一性，不同服务都可能有 `getById`、`query` 这类同名方法
- `methodName`
  - 作用：在目标服务内部定位方法名
- `parameterTypes`
  - 作用：唯一确定方法签名
  - Why：Java 支持方法重载，仅靠 `methodName` 无法确定到底是哪个方法
- `parameters`
  - 作用：真正的方法参数值

### 4.2 `RpcResponse` 的最小字段设计
- `requestId`
  - 作用：把这条响应匹配回原请求
- `status/code`
  - 作用：表达本次调用结果状态
  - Why：`data` 只能承载成功结果，不能表达“服务不存在、方法不存在、反序列化失败、服务端异常”等失败状态
- `message`
  - 作用：失败时补充错误原因
- `data`
  - 作用：成功时的返回值

### 4.3 本地服务注册表
- 最小模型可以理解成：`Map<String, Object>`
- 启动时完成：
  - `serviceName -> serviceBean` 的本地映射注册
- 请求到来时完成：
  - 按 `serviceName` 直接查本地映射表拿到服务实例
- Why：
  - 请求路径不能每次临时去扫描项目里的实现类
  - 这样会增加运行时开销，也会把“服务暴露”和“请求执行”耦合在一起

### 4.4 注册中心与本地服务注册表的区别
- `注册中心`
  - 解决：`请求应该发到哪台机器`
  - 本质：`serviceName -> 一组网络地址`
- `本地服务注册表`
  - 解决：`请求到了这台机器后，应该调用哪个本地服务实例`
  - 本质：`serviceName -> 本地服务对象`
- 这是 RPC 中的“两次定位”：
  - 第一次：客户端通过注册中心做服务发现
  - 第二次：服务端通过本地注册表做服务分发

### 4.5 为什么还需要负载均衡
- 注册中心通常返回的是一组可用服务地址，而不是一个最终目标节点
- 客户端还需要从这些候选地址里选出“这次发给谁”
- 这一步就是负载均衡
- Why：
  - 分摊流量，避免单机过热
  - 提高可用性，一台故障还能切到其他节点
  - 让集群扩容真正生效
- 入门方案：
  - `轮询`：最容易理解，适合节点能力和请求成本比较接近的场景

### 5. 常见误区
- 误区 1：调用了 `writeAndFlush` 就一定会经过所有 Handler
- 不对。Netty 会按“事件类型 + 传播起点 + 传播方向”选择 Handler，不是无差别跑完整条链
- 误区 2：`Encoder` 放在哪都一样
- 不对。它能不能被当前这次出站传播命中，和它在 Pipeline 中的位置直接相关
- 误区 3：响应回写也会重新走一遍解码逻辑
- 不对。请求解码和响应编码是两条方向相反的处理链
- 误区 4：`methodName` 足够定位目标方法
- 不对。Java 支持方法重载，还必须结合 `parameterTypes`
- 误区 5：`RpcResponse` 只要有 `data` 就够了
- 不对。失败场景也要有结构化返回，至少需要 `status/code + message`
- 误区 6：注册中心和本地服务注册表是同一个东西
- 不对。前者管理网络地址，后者管理本地服务实例
- 误区 7：注册中心返回多个地址后就不需要负载均衡
- 不对。地址列表只是候选集合，真正选哪个节点由负载均衡决定

### 6. 面试表达
- 在 Netty 里，`writeAndFlush` 触发的是出站事件，不会重新走入站解码链。RPC 服务端收到请求后，通常先沿入站链完成拆包、解码和业务处理；业务执行结束得到 `RpcResponse` 后，再通过出站链找到 `Encoder` 做编码并回写网络。这里还要注意传播起点差异：`ctx.writeAndFlush()` 是从当前 Handler 往前找出站处理器，`channel.writeAndFlush()` 是从整条 Pipeline 尾部发起，所以如果 `Encoder` 被放在当前业务 Handler 后面，前者可能命中不到它。
- 一次 RPC 调用通常分两次定位。第一次是在客户端侧通过注册中心根据 `serviceName` 获取可用服务地址列表，再由负载均衡从中选择一个节点发起请求；第二次是在服务端节点内部，经过 `LengthFieldBasedFrameDecoder` 切帧、`ProtocolDecoder` 协议解析与反序列化后，`RpcRequestHandler` 再根据 `serviceName` 查本地服务注册表找到服务实例，再结合 `methodName`、`parameterTypes` 和 `parameters` 完成方法调用。最后将结果封装成 `RpcResponse`，通过编码器和 `writeAndFlush` 回写到网络。

### 7. 今日疑问 / 技术债务
- [x] `ctx.writeAndFlush()` 与 `channel.writeAndFlush()` 的传播起点区别，已建立第一轮理解
- [x] `Encoder` 放错位置时为什么看起来“没有生效”，已建立第一轮链路理解
- [x] `Decoder / Encoder / 业务 Handler` 的典型摆放顺序，已建立最小理解
- [x] `RpcRequest / RpcResponse` 的最小字段设计，已建立第一轮理解
- [x] `本地服务注册表 / 注册中心 / 负载均衡` 的职责边界，已建立第一轮理解
- [ ] `一致性 Hash` 相比轮询更适合哪些 RPC 场景，留到下一轮继续

## 总结区（预留）

### 今日掌握
- 已理解 `writeAndFlush` 触发的是出站事件，而不是重新走入站解码链
- 已理解为什么响应回写不会经过 `Decoder`
- 已理解 `ctx.writeAndFlush()` 与 `channel.writeAndFlush()` 的传播起点不同
- 已理解 `Encoder` 的摆放顺序会直接影响它能否被当前出站传播命中
- 已建立最小 RPC Pipeline 顺序：`切帧 -> 协议解析 -> 业务执行 -> 响应编码`
- 已区分 `ProtocolDecoder` 与 `RpcRequestHandler` 的职责边界
- 已理解 `RpcRequest` 至少需要：`requestId / serviceName / methodName / parameterTypes / parameters`
- 已理解 `RpcResponse` 至少需要：`requestId / status(code) / message / data`
- 已理解为什么 `serviceName` 负责服务级定位，`methodName + parameterTypes` 负责方法级定位
- 已理解本地服务注册表的作用是：`serviceName -> 本地服务实例`
- 已理解注册中心的作用是：`serviceName -> 一组可用服务地址`
- 已理解负载均衡的作用是：从可用地址列表中为当前请求选出目标节点
- 已能按顺序复述一条最小 RPC 调用链：`注册中心 -> 负载均衡 -> 发请求 -> 切帧 -> 协议解析 -> 服务定位 -> 方法调用 -> 响应编码 -> 回写网络`

### 仍有疑问
- `一致性 Hash` 与普通轮询在 RPC 场景中的 trade-off 还没展开
- 负载均衡策略和注册中心推拉模型还没深入到源码层

### 技术债务
- 暂无新增正式技术债务
- 保留学习延伸点：`一致性 Hash`、注册中心 watch / 推送机制

### 明日计划
- 继续推进 `一致性 Hash`、注册中心 watch 机制与服务列表更新链路
