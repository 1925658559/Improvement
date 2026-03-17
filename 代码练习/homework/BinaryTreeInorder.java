import java.util.*;

/**
 * LeetCode 94 - 二叉树的中序遍历
 * 中序遍历顺序：左 → 根 → 右
 */
public class BinaryTreeInorder {

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
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    private void inorder(TreeNode node, List<Integer> result) {
        // 终止条件
        if (node == null) {
            return;
        }

        // 递归体：左 - 根 - 右
        inorder(node.left, result);    // 1. 递归遍历左子树
        result.add(node.val);          // 2. 访问根节点
        inorder(node.right, result);   // 3. 递归遍历右子树
    }

    /**
     * 解法二：迭代实现（使用栈）
     * 时间复杂度：O(n)
     * 空间复杂度：O(n) - 最坏情况栈存储所有节点
     */
    public List<Integer> inorderTraversalIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Stack<TreeNode> stack = new Stack<>();
        TreeNode curr = root;

        while (curr != null || !stack.isEmpty()) {
            // 1. 一直往左走，把所有左节点入栈
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }

            // 2. 弹出栈顶节点（最左节点）
            curr = stack.pop();
            result.add(curr.val);  // 访问根节点

            // 3. 转向右子树
            curr = curr.right;
        }

        return result;
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        BinaryTreeInorder solution = new BinaryTreeInorder();

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
        List<Integer> result1 = solution.inorderTraversal(root);
        System.out.println("递归实现结果: " + result1);  // [1, 2, 3, 4, 6]

        // 测试迭代实现
        List<Integer> result2 = solution.inorderTraversalIterative(root);
        System.out.println("迭代实现结果: " + result2);  // [1, 2, 3, 4, 6]
    }
}
