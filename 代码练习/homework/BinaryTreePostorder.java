import java.util.*;

/**
 * LeetCode 145 - 二叉树的后序遍历
 * 后序遍历顺序：左 → 右 → 根
 */
public class BinaryTreePostorder {

    // 树节点定义
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }
    }

    /**
     * 解法一：递归实现（推荐）
     * 时间复杂度：O(n) - 每个节点访问一次
     * 空间复杂度：O(h) - h 为树的高度，递归栈深度
     */
    public List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postorder(root, result);
        return result;
    }

    private void postorder(TreeNode node, List<Integer> result) {
        // 终止条件
        if (node == null) {
            return;
        }

        // 递归体：左 - 右 - 根
        postorder(node.left, result);    // 1. 递归遍历左子树
        postorder(node.right, result);   // 2. 递归遍历右子树
        result.add(node.val);            // 3. 访问根节点
    }

    /**
     * 解法二：迭代实现（使用栈 + 记录上一个访问的节点）
     * 核心思想：
     * - 后序遍历是"左 → 右 → 根"
     * - 访问根节点前，必须确保左右子树都已访问
     * - 用 prev 记录上一个访问的节点，判断右子树是否已访问
     *
     * 时间复杂度：O(n)
     * 空间复杂度：O(n)
     */
    public List<Integer> postorderTraversalIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Stack<TreeNode> stack = new Stack<>();
        TreeNode curr = root;
        TreeNode prev = null;  // 记录上一个访问的节点

        while (curr != null || !stack.isEmpty()) {
            // 1. 一直往左走，把所有左节点入栈
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }

            // 2. 查看栈顶节点（不弹出）
            curr = stack.peek();

            // 3. 判断是否可以访问当前节点
            // 条件：右子树为空 或 右子树已经访问过
            if (curr.right == null || curr.right == prev) {
                // 可以访问当前节点
                result.add(curr.val);
                stack.pop();
                prev = curr;  // 记录已访问的节点
                curr = null;  // 重置 curr，避免重复访问左子树
            } else {
                // 右子树还未访问，转向右子树
                curr = curr.right;
            }
        }

        return result;
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        BinaryTreePostorder solution = new BinaryTreePostorder();

        // 构建测试树：
        //       4
        //      / \
        //     2   6
        //    / \
        //   1   3
        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(6);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);

        // 测试递归实现
        List<Integer> result1 = solution.postorderTraversal(root);
        System.out.println("递归实现结果: " + result1);  // [1, 3, 2, 6, 4]

        // 测试迭代实现
        List<Integer> result2 = solution.postorderTraversalIterative(root);
        System.out.println("迭代实现结果: " + result2);  // [1, 3, 2, 6, 4]
    }
}
