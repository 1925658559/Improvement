/**
 * ================================================================
 * 极简版 ConcurrentHashMap - 模拟 JDK 1.8 的 synchronized 锁节点机制
 * ================================================================
 *
 * 核心设计思想：
 * 1. 使用 数组 + 链表 结构存储数据（红黑树简化为链表）
 * 2. 使用 synchronized 锁住数组节点（而非整个 Map），实现高并发
 * 3. volatile 关键字保证线程间的可见性（get() 无需加锁）
 *
 * 对比 JDK 1.7：
 *   - 1.7 使用 Segment 分段锁，默认 16 个 Segment，并发度有限
 *   - 1.8 直接锁数组节点，理论上并发度等于数组长度
 *
 * ================================================================
 */
public class MyConcurrentHashMap<K, V> {

    // =========================== 常量定义 ===========================

    /**
     * 默认数组容量 = 16
     *
     * 为什么是 16？
     * 1. 必须是 2 的幂次方（为了位运算优化）
     * 2. 16 是平衡点：太小冲突多，太大浪费内存
     * 3. 扩容时翻倍：16 → 32 → 64 → 128...
     */
    private static final int DEFAULT_CAPACITY = 16;


    // =========================== 内部类：Node 节点 ===========================

    /**
     * 链表节点类 - 存储键值对
     *
     * 为什么字段设计成这样？
     * - hash: final，避免重复计算
     * - key: final，key 永远不变
     * - value: volatile，保证多线程可见性（关键！）
     * - next: volatile，保证链表结构的可见性（关键！）
     */
    private static class Node<K, V> {
        /**
         * hash 值缓存
         *
         * 为什么缓存？
         * - 查找时无需重新计算 key.hashCode()
         * - 比较时先用 hash 快速过滤（hash 不同，key 肯定不同）
         */
        final int hash;

        /**
         * 键 - 不可变
         *
         * 为什么 final？
         * - key 永远不会改变，用于 equals 判断
         */
        final K key;

        /**
         * 值 - 可变 + 可见
         *
         * 为什么 volatile？（关键！）
         * 1. 保证可见性：一个线程修改 value，其他线程立即可见
         * 2. 禁止重排序：防止构造函数逸出导致的内存语义问题
         *
         * 没有 volatile 会怎样？
         * - 线程A 修改了 value，线程B 可能读不到最新值（读到旧值）
         */
        volatile V value;

        /**
         * 下一个节点 - 可变 + 可见
         *
         * 为什么 volatile？（关键！）
         * 1. 链表结构变化对其他线程可见
         * 2. get() 方法遍历链表时，能看到最新结构
         *
         * 这就是为什么 get() 方法不需要加锁的原因！
         */
        volatile Node<K, V> next;

        /**
         * 构造函数
         *
         * @param hash  缓存的 hash 值
         * @param key   键（不可变）
         * @param value 值（可变）
         * @param next  下一个节点（可变）
         */
        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }


    // =========================== 核心字段 ===========================

    /**
     * 底层数组 - 桶数组
     *
     * 为什么 volatile？
     * - 保证数组引用的可见性（扩容后其他线程能立即看到新数组）
     *
     * 为什么是 Node<K,V>[] 而非 List？
     * - 数组访问是 O(1)，直接通过下标定位
     * - 每个位置是一个桶（Bucket），存储链表头节点
     */
    private volatile Node<K, V>[] table;


    // =========================== 构造函数 ===========================

    /**
     * 构造函数 - 初始化空数组
     *
     * 为什么用@SuppressWarnings("unchecked")？
     * - Java 不允许直接创建泛型数组：new Node<K,V>[size]
     * - 只能先创建原始类型数组，再强制转换
     */
    @SuppressWarnings("unchecked")
    public MyConcurrentHashMap() {
        // 创建长度为 16 的空数组（懒加载，此时每个位置都是 null）
        table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];
    }


    // =========================== 核心方法：hash() ===========================

    /**
     * 计算key的hash值 - 扰动函数
     *
     * 为什么需要扰动函数？
     * 1. hashCode() 的质量可能不好（很多 key 的 hash 值集中在低位）
     * 2. 直接用 hashCode() 会导致大量冲突（都挂在同一个桶里）
     * 3. 扰动函数让高位参与运算，使 hash 值更均匀
     *
     * 示例：
     * - 假设数组长度 16（二进制 10000）
     * - 如果只用低 4 位，很多 key 的低 4 位相同 → 冲突！
     * - 高低位异或后，高位的影响也加入 → 冲突减少
     *
     * @param key 键（可能为 null）
     * @return 扰动后的 hash 值（保证是正数）
     */
    private int hash(K key) {
        // 1. 处理 null：HashMap 允许 key 为 null，hash 值固定为 0
        int h = key == null ? 0 : key.hashCode();

        // 2. 扰动函数：高位异或低位
        //    h >>> 16：将高 16 位移到低 16 位
        //    h ^ (h >>> 16)：高 16 位与低 16 位异或
        //    & 0x7fffffff：保证结果是正数（0x7fffffff = 2147483647，二进制 0111...1）
        //
        // 为什么保证正数？
        // - 因为 hash 可能是负数（hashCode() 的实现）
        // - 如果 hash 是负数，计算下标时会越界
        // - & 0x7fffffff 将符号位清零，确保结果 >= 0
        return (h ^ (h >>> 16)) & 0x7fffffff;
    }


    // =========================== 核心方法：put() ===========================

    /**
     * 插入键值对 - 线程安全
     *
     * 核心流程：
     * 1. 计算 hash 和数组下标
     * 2. 如果桶为空 → 创建头节点（需处理并发）
     * 3. 如果桶不为空 → 锁住头节点，遍历链表
     *    - key 已存在 → 覆盖 value
     *    - key 不存在 → 尾插法插入新节点
     *
     * 为什么这个设计是线程安全的？
     * - 不同桶的操作互不干扰（锁的是 table[index]，不是整个 table）
     * - 同一个桶的操作串行执行（synchronized 保证）
     *
     * @param key   键
     * @param value 值
     */
    public void put(K key, V value) {
        // ==================== Step 1: 计算 hash 和下标 ====================

        // 1.1 计算扰动后的 hash 值
        int hash = hash(key);

        // 1.2 计算数组下标
        //
        // 为什么用 & 而非 %？
        // - % 是取模运算，效率低（需要除法）
        // - & 是位运算，效率高（CPU 单周期完成）
        //
        // 为什么是 (table.length - 1)？
        // - table.length 必须是 2 的幂次方（如 16, 32, 64）
        // - 2 的幂次方 - 1 的二进制全是 1（如 15 = 1111）
        // - hash & 1111 等价于取 hash 的低 4 位
        // - 效果等同于 hash % 16，但效率更高
        //
        // 示例：
        //   hash = 37 (二进制 100101)
        //   n-1  = 15 (二进制 001111)
        //   37 & 15 = 5 (二进制 000101)
        //   等价于 37 % 16 = 5
        int index = hash & (table.length - 1);

        // ==================== Step 2: 检查桶是否为空 ====================

        // 获取桶的头节点
        Node<K, V> node = table[index];

        if (node == null) {
            // ==================== 桶为空：需要创建头节点 ====================

            // 🔥 注意：这里是并发安全的难点！
            //
            // 问题：如果直接 `table[index] = new Node<>()`，会怎样？
            // - 线程A 判断 node == null，准备创建
            // - 线程B 也判断 node == null，也准备创建
            // - 两个线程都创建节点，后面的覆盖前面的 → 数据丢失！
            //
            // JDK 1.8 的解决方案：CAS (Compare-And-Swap)
            //   - casTabAt(tab, i, null, newNode)
            //   - 只有一个线程能成功，其他线程失败后重试
            //
            // 我们的简化方案：synchronized + Double-Check
            //   - 加锁后再次检查是否为空
            //   - 如果为空才创建（防止重复创建）

            synchronized (this) {  // 简化版：锁住整个 map（JDK 1.8 是 CAS 无锁）

                // Double-Check：再次检查是否为空
                // 为什么需要两次检查？
                // - 第一次检查（无锁）：快速判断，不为空直接跳过
                // - 第二次检查（加锁）：防止多个线程同时通过第一次检查
                if (table[index] == null) {
                    // 创建头节点，next 为 null（链表只有一个节点）
                    table[index] = new Node<>(hash, key, value, null);
                    return;  // 插入成功，直接返回
                }
            }
        }

        // ==================== Step 3: 桶不为空，遍历链表 ====================

        // 🔥 核心思想：锁住头节点，而不是整个 table
        //
        // 为什么这样设计？
        // - synchronized(table[index]) 只锁住这一个桶
        // - 其他桶的操作可以并发执行（table[0], table[1], table[2]...）
        // - 并发度大大提升！
        //
        // 对比 Hashtable：
        // - Hashtable 的 put() 方法是 synchronized put(...) {}
        // - 相当于锁住整个 table，任何时刻只能一个线程操作
        // - 吞吐量极低

        synchronized (table[index]) {  // 只锁住这个桶的头节点
            Node<K, V> current = table[index];  // 当前遍历的节点
            Node<K, V> prev = null;             // 前一个节点（用于尾插法）

            // 遍历链表
            while (current != null) {

                // ==================== 检查 key 是否已存在 ====================

                // 为什么先比较 hash？
                // - hash 不同，key 肯定不同（快速过滤）
                // - hash 相同，再用 equals 确认（避免频繁调用 equals）
                if (current.hash == hash &&
                    (current.key == key || (current.key != null && current.key.equals(key)))) {
                    // key 已存在，覆盖 value
                    current.value = value;
                    return;  // 更新成功，直接返回
                }

                // 保存前一个节点（用于尾插法）
                prev = current;

                // 🔥 关键：移动到下一个节点
                // 如果忘记这行，会死循环！
                current = current.next;
            }

            // ==================== 遍历完成，key 不存在，插入新节点 ====================

            // 尾插法：插入到链表尾部
            // 为什么用尾插法而非头插法？
            // - JDK 1.7 用头插法，扩容时会倒序，可能导致死循环
            // - JDK 1.8 改用尾插法，保持顺序，不会死循环
            if (prev != null) {
                prev.next = new Node<>(hash, key, value, null);
            }
        }
    }


    // =========================== 核心方法：get() ===========================

    /**
     * 根据 key 获取 value - 线程安全（无需加锁）
     *
     * 🔥 为什么不需要加锁？（面试高频问题！）
     *
     * 答案：volatile 保证可见性
     *
     * 详细解释：
     * 1. Node 的 value 和 next 字段都是 volatile 修饰
     * 2. volatile 保证：
     *    - 可见性：一个线程修改，其他线程立即可见
     *    - 禁止重排序：防止读到未初始化的对象
     * 3. get() 只是读取操作，不修改数据结构
     * 4. 即使其他线程正在执行 put()，get() 也能读到最新值（或旧值，但不会出错）
     *
     * 可能的疑问：
     * - Q: get() 时 put() 正在修改链表，会怎样？
     * - A: 可能读到旧值或新值，但不会崩溃或读到脏数据（volatile 保证了）
     *
     * - Q: 如果读到一半，链表被修改了，会怎样？
     * - A: volatile 的 happens-before 原则保证要么读到旧结构，要么读到新结构
     *
     * @param key 键
     * @return 对应的值，不存在返回 null
     */
    public V get(K key) {
        // ==================== Step 1: 定位桶 ====================

        int hash = hash(key);
        int index = hash & (table.length - 1);

        // ==================== Step 2: 遍历链表 ====================

        Node<K, V> node = table[index];  // 获取头节点（volatile 读取，保证可见性）

        while (node != null) {
            // 检查是否找到目标 key
            if (node.hash == hash &&
                (node.key == key || (node.key != null && node.key.equals(key)))) {
                // 找到了，返回 value（volatile 读取，保证可见性）
                return node.value;
            }

            // 🔥 关键：移动到下一个节点
            // 如果忘记这行，会死循环！
            node = node.next;
        }

        // 遍历完整个链表都没找到，返回 null
        return null;
    }


    // =========================== 测试代码 ===========================

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 创建 Map 实例
        MyConcurrentHashMap<String, Integer> map = new MyConcurrentHashMap<>();

        // ==================== 基本功能测试 ====================

        map.put("key1", 100);
        map.put("key2", 200);
        map.put("key1", 150);  // 覆盖 key1 的值

        System.out.println("get(key1) = " + map.get("key1"));  // 期望: 150
        System.out.println("get(key2) = " + map.get("key2"));  // 期望: 200
        System.out.println("get(key3) = " + map.get("key3"));  // 期望: null

        // ==================== 并发测试 ====================

        // 创建任务：每个线程插入 100 个键值对
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                // key = 线程名-序号，保证不同线程的 key 不冲突
                map.put(Thread.currentThread().getName() + "-" + i, i);
            }
        };

        // 创建两个线程并发写入
        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");

        t1.start();
        t2.start();

        // 等待两个线程完成
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n并发写入后 map 大小约为: 200");
        // 注意：我们这个简化版没有实现 size() 方法，所以无法打印实际大小
        // 实际 JDK 的 ConcurrentHashMap 有维护 size 的复杂机制（LongAdder）
    }
}


// ================================================================
// 总结：核心设计要点
// ================================================================
//
// 1. 锁粒度优化：
//    - Hashtable: synchronized 锁住整个方法 → 吞吐量低
//    - 我们的设计: synchronized(table[index]) 锁住单个桶 → 吞吐量高
//
// 2. volatile 保证可见性：
//    - Node.value 和 Node.next 都是 volatile
//    - get() 无需加锁，性能最佳
//
// 3. 位运算优化：
//    - 用 hash & (n-1) 替代 hash % n
//    - 效率提升约 2 倍
//
// 4. 扰动函数：
//    - (h ^ (h >>> 16)) 让高位参与运算
//    - 减少 hash 冲突，提升性能
//
// 5. Double-Check：
//    - 第一次检查（无锁）：快速判断
//    - 第二次检查（加锁）：防止并发问题
//
// ================================================================
// 面试高频问题
// ================================================================
//
// Q1: ConcurrentHashMap 1.7 和 1.8 的区别？
// A1: 1.7 用 Segment 分段锁，1.8 用 CAS + synchronized 锁节点
//
// Q2: get() 方法为什么不需要加锁？
// A2: volatile 保证可见性，读取操作不会破坏数据结构
//
// Q3: 为什么计算下标用 & 而不是 %？
// A3: & 是位运算，效率高；& (n-1) 等价于 % n（前提：n 是 2 的幂）
//
// Q4: 什么是扰动函数？为什么需要？
// A4: (h ^ (h >>> 16)) 让高位参与运算，减少 hash 冲突
//
// Q5: 如果哈希冲突很严重，会怎样？
// A5: 链表变长，查找退化到 O(n)；JDK 1.8 会在链表长度 ≥8 且数组≥64 时转红黑树
//
// ================================================================
