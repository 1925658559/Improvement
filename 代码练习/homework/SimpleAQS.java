import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

/**
 * 简易版 AQS 实现
 *
 * 核心思想：
 * 1. state: 同步状态（0=未锁定，>0=已锁定）
 * 2. CLH 队列: 存储等待获取锁的线程
 * 3. CAS: 原子修改 state
 *
 * @author Xu Yingqing
 */
public class SimpleAQS {

    /**
     * 同步状态
     * volatile 保证可见性
     * 0 = 未锁定
     * 1 = 已锁定（重入次数）
     */
    private volatile int state;

    /**
     * CLH 同步队列的头节点（哑节点，不代表任何线程）
     * 初值为 null，第一次 acquire 时会初始化
     */
    private volatile Node head;

    /**
     * CLH 同步队列的尾节点
     * 新等待的线程会加入到 tail 后面
     */
    private volatile Node tail;

    /**
     * 获取当前同步状态值
     */
    protected final int getState() {
        return state;
    }

    /**
     * 设置同步状态值（用于释放锁时）
     * 注意：这个方法没有 CAS 保护，只在 release 流程中使用
     */
    protected final void setState(int newState) {
        state = newState;
    }

    /**
     * ========================================
     * acquire 流程：获取锁
     * ========================================
     *
     * 流程：
     * 1. tryAcquire: 尝试 CAS 修改 state 0→1
     * 2. addWaiter: 失败则加入 CLH 队列尾部
     * 3. acquireQueued: 在队列中自旋等待
     */
    public final void acquire() {
        // 步骤1: 尝试直接获取锁
        if (!tryAcquire()) {
            // 步骤2: 获取失败，将当前线程加入等待队列
            Node node = addWaiter();

            // 步骤3: 在队列中自旋等待获取锁
            acquireQueued(node);
        }
        // 获取锁成功，直接返回
    }

    /**
     * 尝试获取锁（非公平）
     *
     * 使用 CAS 将 state 从 0 改为 1
     *
     * @return true=获取成功, false=获取失败
     */
    protected final boolean tryAcquire() {
        // CAS 操作：期望 state=0，如果成功则设置为 1
        // Unsafe.compareAndSwapInt 保证了原子性
        return unsafe.compareAndSwapInt(this, stateOffset, 0, 1);
    }

    /**
     * 将当前线程加入到 CLH 等待队列的尾部
     *
     * @return 包装当前线程的 Node 节点
     */
    private Node addWaiter() {
        // 创建新节点，包装当前线程
        Node node = new Node(Thread.currentThread());

        // 快速尝试：如果队列已初始化，直接 CAS 设置 tail
        Node pred = tail;
        if (pred != null) {
            // 设置新节点的前驱为当前的 tail
            node.prev = pred;
            // CAS 设置 tail 为新节点（原子操作）
            if (compareAndSetTail(pred, node)) {
                // CAS 成功，将旧 tail 的 next 指向新节点
                pred.next = node;
                return node;
            }
            // CAS 失败，说明有竞争，进入 enq 自旋
        }

        // 队列未初始化或 CAS 失败，使用 enq 自旋处理
        enq(node);
        return node;
    }

    /**
     * 自旋初始化队列或插入节点
     *
     * @param node 要插入的节点
     * @return 节点的前驱节点
     */
    private Node enq(Node node) {
        // 无限自旋，直到成功
        for (;;) {
            Node t = tail;
            if (t == null) {
                // 队列未初始化，先创建哑节点作为 head
                // 必须先初始化 head，因为 head 是哨兵节点
                if (compareAndSetHead(null)) {
                    tail = head;
                }
            } else {
                // 队列已初始化，CAS 插入节点到 tail
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
                // CAS 失败，继续自旋重试
            }
        }
    }

    /**
     * 在队列中自旋等待获取锁
     *
     * @param node 当前线程的节点
     */
    private final void acquireQueued(final Node node) {
        // 无限自旋，直到获取锁或被中断
        for (;;) {
            // 获取前驱节点
            final Node p = node.predecessor();

            // 如果前驱是 head，说明当前节点是第一个等待的线程
            if (p == head && tryAcquire()) {
                // 获取锁成功，将自己设置为新的 head
                setHead(node);
                p.next = null; // help GC
                return;
            }

            // 前驱不是 head，或获取锁失败
            // 检查是否应该阻塞
            if (shouldParkAfterFailedAcquire(p)) {
                // 阻塞当前线程，等待被唤醒
                LockSupport.park(this);
            }
            // 被唤醒后，继续自旋重试
        }
    }

    /**
     * 检查是否应该阻塞当前线程
     *
     * @param pred 前驱节点
     * @return true=应该阻塞, false=继续自旋
     */
    private static boolean shouldParkAfterFailedAcquire(Node pred) {
        // 简化版：直接返回 true，让线程阻塞
        // 实际 AQS 会检查前驱节点的 waitStatus 状态
        return true;
    }

    /**
     * 设置当前节点为 head（成为哑节点）
     */
    private void setHead(Node node) {
        head = node;
        node.thread = null; // head 不关联具体线程
        node.prev = null;
    }

    /**
     * ========================================
     * release 流程：释放锁
     * ========================================
     *
     * 流程：
     * 1. tryRelease: 将 state 设为 0
     * 2. unparkSuccessor: 唤醒同步队列的第一个等待节点
     */
    public final void release() {
        // 步骤1: 尝试释放锁
        if (tryRelease()) {
            // 步骤2: 释放成功，唤醒后继节点
            Node h = head;
            if (h != null) {
                unparkSuccessor(h);
            }
        }
    }

    /**
     * 尝试释放锁
     *
     * @return true=完全释放, false=还有重入
     */
    protected boolean tryRelease() {
        // 简化版：直接将 state 设为 0
        // 实际 AQS 需要处理重入情况（state--）
        int free = getState() - 1;
        if (free == 0) {
            setState(0);
            return true;
        }
        setState(free);
        return false;
    }

    /**
     * 唤醒后继节点
     *
     * @param node 当前节点（通常是 head）
     */
    private void unparkSuccessor(Node node) {
        // 获取后继节点
        Node s = node.next;
        if (s != null && s.thread != null) {
            // 唤醒后继线程
            LockSupport.unpark(s.thread);
        }
    }

    /**
     * ========================================
     * CAS 工具方法
     * ========================================
     */

    /**
     * CAS 设置 head
     */
    private final boolean compareAndSetHead(Node update) {
        return unsafe.compareAndSwapObject(this, headOffset, null, update);
    }

    /**
     * CAS 设置 tail
     */
    private final boolean compareAndSetTail(Node expect, Node update) {
        return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
    }

    /**
     * ========================================
     * Node 节点类（CLH 队列节点）
     * ========================================
     */
    static final class Node {
        /** 节点中的线程 */
        Thread thread;

        /** 前驱节点 */
        Node prev;

        /** 后继节点 */
        Node next;

        Node(Thread thread) {
            this.thread = thread;
        }

        /**
         * 获取前驱节点（处理边界情况）
         */
        Node predecessor() {
            Node p = prev;
            if (p == null) {
                throw new NullPointerException();
            } else {
                return p;
            }
        }
    }

    /**
     * ========================================
     * Unsafe 相关（用于 CAS 操作）
     * ========================================
     */
    private static final sun.misc.Unsafe unsafe;
    private static final long stateOffset;
    private static final long headOffset;
    private static final long tailOffset;

    static {
        try {
            // 获取 Unsafe 实例
            unsafe = getUnsafe();

            // 获取字段在内存中的偏移量
            stateOffset = unsafe.objectFieldOffset(SimpleAQS.class.getDeclaredField("state"));
            headOffset = unsafe.objectFieldOffset(SimpleAQS.class.getDeclaredField("head"));
            tailOffset = unsafe.objectFieldOffset(SimpleAQS.class.getDeclaredField("tail"));
        } catch (Exception e) {
            throw new Error("初始化失败", e);
        }
    }

    /**
     * 获取 Unsafe 实例
     *
     * 注意：在 Java 9+ 中，直接使用 Unsafe 被限制，需要使用 VarHandle
     * 这里使用反射获取 Unsafe（仅用于学习）
     */
    private static sun.misc.Unsafe getUnsafe() throws Exception {
        java.lang.reflect.Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return (sun.misc.Unsafe) field.get(null);
    }
}
