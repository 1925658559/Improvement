import java.util.Objects;

/**
 * 手写 HashMap 实现（简化版）
 * 底层结构：数组 + 链表
 * 泛型 <K, V>：K=键的类型，V=值的类型
 */
public class HashMapDemo<K, V> {

    // ==================== 成员变量 ====================

    /**
     * 默认数组初始容量：16
     * 必须是 2 的幂次方，方便用位运算 & (n-1) 计算下标
     * private=私有，static=静态（属于类），final=不可改变
     */
    private static final int DEFAULT_CAPACITY = 16;

    /**
     * 底层数组：存储 Node 节点
     * 每个 table[index] 可能是 null，也可能是一个链表的头节点
     * Node<K, V>[]：Node 类型的数组
     */
    private Node<K, V>[] table;

    /**
     * 当前存储的键值对数量
     * 用于判断是否需要扩容（size/capacity > 0.75 时触发）
     */
    private int size;


    // ==================== 内部类：节点 ====================

    /**
     * Node：存储键值对的节点
     * static=静态内部类，不依赖外部类对象，节省内存
     */
    static class Node<K, V> {
        K key;                // 键
        V value;              // 值
        Node<K, V> next;      // 指向下一个节点（形成链表，处理 Hash 冲突）
        int hash;             // 缓存 key 的 hash 值，避免重复计算

        /**
         * Node 构造函数
         * @param hash   key 的 hash 值
         * @param key    键
         * @param value  值
         * @param next   指向下一个节点的指针
         */
        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;      // this.hash：成员变量，hash：参数
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }


    // ==================== 核心方法 ====================

    /**
     * 计算 key 的 hash 值（扰动函数）
     * @param key 键（可能为 null）
     * @return hash 值
     */
    static final int hash(Object key) {
        int h;
        // 三元运算符：(条件) ? 值1 : 值2
        // 如果 key 是 null，返回 0（HashMap 允许一个 null 键）
        // 如果 key 不是 null，计算 hash：
        //   1. key.hashCode()：调用对象的 hashCode() 方法得到 hash 值
        //   2. h >>> 16：无符号右移 16 位，把高 16 位移到低 16 位
        //   3. ^ ：异或运算（相同为0，不同为1），让高低位混合
        // 目的：让高位参与运算，减少 Hash 冲突
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        // 注意括号的位置！先赋值给 h，再运算。
    }

    /**
     * 向 HashMap 中插入一个键值对
     * @param key   键
     * @param value 值
     */
    public void put(K key, V value) {
        // ============ Step 1: 初始化数组（如果是第一次） ============
        // 如果 table 是 null（第一次调用 put）或长度为 0，先初始化数组
        if (table == null || table.length == 0) {
            // 创建一个长度为 DEFAULT_CAPACITY（16）的 Node 数组
            // 此时数组中所有位置都是 null，还没有存储任何节点
            table = new Node[DEFAULT_CAPACITY];
        }

        // ============ Step 2: 计算 Hash 值和数组下标 ============
        // 调用 hash(key) 方法计算出 key 的 hash 值
        int hashValue = hash(key);

        // 缓存数组长度到局部变量 n（避免重复读取 table.length）
        int n = table.length;

        // 计算数组下标：hashValue & (n-1)
        // & 是位运算，比取模 % 快得多
        // n-1 的二进制全是 1（如 15=1111），& 操作相当于取低 n 位
        // 等价于 hashValue % n，但效率更高
        int index = (n - 1) & hashValue;

        // ============ Step 3: 取出该位置的链表头节点 ============
        // table[index] 可能是 null（该位置没有元素），也可能是一个 Node（链表头）
        Node<K, V> head = table[index];

        // ============ Step 4: 如果该位置为空，直接插入 ============
        if (head == null) {
            // 创建一个新的 Node 节点，放到数组的这个位置
            // 参数：hashValue, key, value, null（next 是 null，表示没有后续节点）
            table[index] = new Node<>(hashValue, key, value, null);
            // 增加键值对数量
            size++;
        }
        // ============ Step 5: 如果该位置不为空，遍历链表 ============
        else {
            // 用 current 变量指向链表头，准备遍历
            Node<K, V> current = head;

            // while 循环遍历链表，直到 current 为 null
            while (current != null) {
                // ============ 情况A：找到相同的 key，覆盖 value ============
                // 判断条件：
                //   1. current.hash == hashValue：先比 hash（int 比较，很快）
                //   2. Objects.equals(current.key, key)：hash 相同再用 equals() 比内容
                //      Objects.equals() 会自动处理 null，避免 NPE
                if (current.hash == hashValue && Objects.equals(current.key, key)) {
                    // key 相同，覆盖旧 value 为新 value
                    current.value = value;
                    // 直接返回方法，不再继续执行（不增加 size，因为是覆盖）
                    return;
                }

                // ============ 情况B：遍历到链表尾部，插入新节点 ============
                // current.next == null 表示当前节点是链表的最后一个节点
                if (current.next == null) {
                    // 创建新节点，挂到当前节点的后面（尾插法）
                    current.next = new Node<>(hashValue, key, value, null);
                    // 增加键值对数量（插入了新节点）
                    size++;
                    // 返回方法
                    return;
                }

                // ============ 继续遍历下一个节点 ============
                // current = current.next：把 current 指向下一个节点
                // 继续下一轮 while 循环
                current = current.next;
            }
        }
    }
}
