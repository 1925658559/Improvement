// LeetCode 206 - Reverse Linked List
// 作者: 徐迎庆
// 日期: 2026-02-03

/**
 * Definition for singly-linked list.
 */
public class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Solution {
    /**
     * 反转链表 - 头插法（Head Insertion Method）
     *
     * 核心思想：
     * 创建一个新的链表（以 dummy 为哨兵节点），从原链表依次摘取节点，
     * 每次都插入到新链表的头部（dummy 之后），最终实现反转。
     *
     * 时间复杂度: O(n) - 遍历一次链表
     * 空间复杂度: O(1) - 只用了常数级别的额外空间
     *
     * @param head 原链表头节点
     * @return 反转后的链表头节点
     */
    public ListNode reverseList(ListNode head) {
        // Step 1: 创建哨兵节点（dummy node）
        // dummy.next 指向新链表的头节点，初始为 null
        ListNode dummy = new ListNode(0);  // 哨兵节点的值无实际意义
        dummy.next = null;

        // Step 2: 遍历原链表，依次摘取节点
        ListNode cur = head;  // cur 指向当前要处理的节点

        while (cur != null) {
            // 2.1 保存当前节点的下一个节点（防止断开后找不到）
            ListNode next = cur.next;

            // 2.2 头插核心：将 cur 插入到 dummy 之后
            // 原本: dummy -> [新链表第一个节点]
            // 插入后: dummy -> cur -> [新链表第一个节点]
            cur.next = dummy.next;  // cur 的 next 指向新链表的头
            dummy.next = cur;        // dummy 的 next 指向 cur

            // 2.3 移动到原链表的下一个节点
            cur = next;

            // 图示过程（以 1->2->3 为例）：
            // 初始: dummy->null,  cur=1
            // 第1轮: dummy->1->null,     cur=2
            // 第2轮: dummy->2->1->null,   cur=3
            // 第3轮: dummy->3->2->1->null, cur=null
        }

        // Step 3: 返回新链表的头节点
        // dummy 是哨兵节点，dummy.next 才是真正的头节点
        return dummy.next;
    }
}

// 测试代码
class ReverseLinkedList {
    public static void main(String[] args) {
        Solution solution = new Solution();

        // 构建测试链表: 1 -> 2 -> 3 -> 4 -> 5
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);

        System.out.println("原链表:");
        printList(head);

        ListNode result = solution.reverseList(head);

        System.out.println("反转后:");
        printList(result);
    }

    private static void printList(ListNode head) {
        // TODO: 实现链表打印
        ListNode cur = head;
        while (cur != null) {
            System.out.print(cur.val + " -> ");
            cur = cur.next;
        }
        System.out.println("null");
    }
}
