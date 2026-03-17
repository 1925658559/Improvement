// LeetCode 142 - Linked List Cycle II
// 作者: 徐迎庆
// 日期: Day-6

/**
 * Definition for singly-linked list.
 */
class ListNode {
    int val;
    ListNode next;
    ListNode(int x) {
        val = x;
        next = null;
    }
}

/**
 * 环形链表 II - 找到环的入口节点
 *
 * 核心思想（数学推导）：
 * 设:
 *   a = 头节点到环入口的距离
 *   b = 环入口到快慢指针相遇点的距离
 *   c = 环的长度
 *   n = 快指针在环里转的圈数
 *
 * 当快慢指针相遇时:
 *   慢指针距离 = a + b
 *   快指针距离 = a + b + n*c
 *   快指针距离 = 2 × 慢指针距离
 *
 *   ∴ 2(a + b) = a + b + n*c
 *   ∴ a + b = n*c
 *   ∴ a = n*c - b = (n-1)*c + (c-b)
 *
 * 结论: 从起点走 a 步 = 从相遇点走 (c-b) 步（都能到达环入口）
 *
 * 算法步骤:
 * 1. 快慢指针判断是否有环，找到相遇点
 * 2. 一个指针从头开始，一个从相遇点开始，都走一步
 * 3. 两指针相遇的位置就是环入口
 */
class Solution {
    public ListNode detectCycle(ListNode head) {
        // 边界条件：空链表或只有一个节点，不可能有环
        if (head == null || head.next == null) {
            return null;
        }

        // 初始化快慢指针，都从头节点开始
        ListNode slow = head;
        ListNode fast = head;

        // ==================== 第一阶段：找相遇点 ====================
        // 快指针每次走2步，慢指针每次走1步
        // 如果有环，它们一定会在环内某处相遇
        while (fast != null && fast.next != null) {
            slow = slow.next;          // 慢指针走1步
            fast = fast.next.next;     // 快指针走2步

            // 快慢指针相遇，说明链表有环
            if (slow == fast) {
                // ==================== 第二阶段：找环入口 ====================
                // 数学推导结论：
                // 设 a=头到环入口距离，b=环入口到相遇点距离，c=环长度
                // 相遇时：2(a+b) = a+b+n*c → a = n*c - b = (n-1)*c + (c-b)
                // 含义：从起点走a步 = 从相遇点走(c-b)步，都能到达环入口
                
                ListNode ptr1 = head;   // ptr1 从头节点出发
                ListNode ptr2 = slow;   // ptr2 从相遇点出发（slow == fast）

                // 两个指针都以每次1步的速度前进
                // 它们一定会在环入口相遇
                while (ptr1 != ptr2) {
                    ptr1 = ptr1.next;
                    ptr2 = ptr2.next;
                }
                
                // 返回环入口节点
                return ptr1;
            }
        }

        // fast 或 fast.next 为 null，说明链表无环
        return null;
    }
}

// 测试代码
class LinkedListCycleII {
    public static void main(String[] args) {
        Solution solution = new Solution();

        // 构建带环的链表: 3 -> 2 -> 0 -> -4
        //                               ^         |
        //                               |_________|
        ListNode head = new ListNode(3);
        ListNode node2 = new ListNode(2);
        ListNode node0 = new ListNode(0);
        ListNode nodeMinus4 = new ListNode(-4);

        head.next = node2;
        node2.next = node0;
        node0.next = nodeMinus4;
        nodeMinus4.next = node2;  // 形成环，入口是 node2

        ListNode cycleStart = solution.detectCycle(head);

        if (cycleStart != null) {
            System.out.println("环入口节点的值: " + cycleStart.val);
            // 期望输出: 2
        } else {
            System.out.println("链表无环");
        }
    }
}
