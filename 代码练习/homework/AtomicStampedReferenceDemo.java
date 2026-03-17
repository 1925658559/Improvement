import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * AtomicStampedReference 演示
 * 演示如何通过版本号解决 ABA 问题
 */
public class AtomicStampedReferenceDemo {

    public static void main(String[] args) {
        // 1. 创建 AtomicStampedReference，初始值为 "A"，版本号为 1
        AtomicStampedReference<String> atomicRef = new AtomicStampedReference<>("A", 1);

        // 获取当前的值和版本号
        String currentValue = atomicRef.getReference();
        int currentStamp = atomicRef.getStamp();
        System.out.println("初始状态: 值=" + currentValue + ", 版本号=" + currentStamp);

        // 2. 演示正常的 CAS（版本号匹配）
        boolean success1 = atomicRef.compareAndSet("A", "B", 1, 2);
        System.out.println("CAS 'A'->'B' (期望版本1, 新版本2): " + success1);
        System.out.println("当前状态: 值=" + atomicRef.getReference() + ", 版本号=" + atomicRef.getStamp());

        // 3. 模拟其他线程修改: B -> A (版本号变为 3)
        boolean success2 = atomicRef.compareAndSet("B", "A", 2, 3);
        System.out.println("CAS 'B'->'A' (期望版本2, 新版本3): " + success2);
        System.out.println("当前状态: 值=" + atomicRef.getReference() + ", 版本号=" + atomicRef.getStamp());

        // 4. 演示 ABA 场景：尝试用旧版本号(1)进行 CAS，应该失败
        boolean success3 = atomicRef.compareAndSet("A", "C", 1, 2);
        System.out.println("CAS 'A'->'C' (期望版本1, 新版本2): " + success3);
        System.out.println("当前状态: 值=" + atomicRef.getReference() + ", 版本号=" + atomicRef.getStamp());

        // 5. 用正确版本号(3)进行 CAS，应该成功
        boolean success4 = atomicRef.compareAndSet("A", "C", 3, 4);
        System.out.println("CAS 'A'->'C' (期望版本3, 新版本4): " + success4);
        System.out.println("当前状态: 值=" + atomicRef.getReference() + ", 版本号=" + atomicRef.getStamp());

        System.out.println("\n--- 结论 ---");
        System.out.println("即使值从 A->B->A 变回了 A，版本号已经从 1->2->3");
        System.out.println("用旧版本号(1)的 CAS 会失败，解决了 ABA 问题！");
    }
}
