import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashMap 1.8 核心原理演示
 *
 * 对比三种实现方式：
 * 1. Hashtable           - 全局锁（synchronized 整个方法）
 * 2. ConcurrentHashMap 1.7 - 分段锁（Segment 继承 ReentrantLock）
 * 3. ConcurrentHashMap 1.8 - CAS + synchronized（锁数组节点）
 */
public class ConcurrentHashMapDemo {

    public static void main(String[] args) {
        // ========== 1. 基本使用 ==========
        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // put 操作
        map.put("key1", 100);
        map.put("key2", 200);

        // get 操作（无锁读取，volatile 保证可见性）
        Integer value = map.get("key1");
        System.out.println("get(key1) = " + value);

        // putIfAbsent（原子操作，ifAbsent 才插入）
        map.putIfAbsent("key1", 999);  // key1 已存在，不覆盖
        System.out.println("putIfAbsent 后 key1 = " + map.get("key1"));  // 仍然是 100

        // ========== 2. 原子操作演示 ==========
        // computeIfAbsent（CAS 思想：如果不存在则计算）
        map.computeIfAbsent("key3", k -> {
            System.out.println("key3 不存在，执行计算逻辑");
            return 300;
        });

        // compute（更新值，线程安全）
        map.compute("key1", (key, oldValue) -> {
            System.out.println("key1 旧值 = " + oldValue);
            return oldValue + 50;  // 100 + 50 = 150
        });
        System.out.println("compute 后 key1 = " + map.get("key1"));  // 150

        // ========== 3. 并发测试 ==========
        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                map.put("thread-" + Thread.currentThread().getId() + "-" + i, i);
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        Thread t3 = new Thread(task);

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\n并发写入后，map 大小 = " + map.size());
    }
}
