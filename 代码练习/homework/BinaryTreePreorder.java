import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 144 - 二叉树的前序遍历
 * 遍历顺序：根 -> 左 -> 右
 */
public class BinaryTreePreorder {

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
     * 前序遍历 - 递归实现
     * 时间复杂度: O(n)
     * 空间复杂度: O(h) h为树的高度
     */
    public List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        preorder(root, result);
        return result;
    }

    private void preorder(TreeNode node, List<Integer> result) {
        // 终止条件：节点为空
        if (node == null) {
            return;
        }

        // 递归体：根 - 左 - 右
        result.add(node.val);           // 1. 访问根节点
        preorder(node.left, result);    // 2. 递归遍历左子树
        preorder(node.right, result);   // 3. 递归遍历右子树
    }

    /**
     * 前序遍历 - 迭代实现（使用栈）
     * 时间复杂度: O(n)
     * 空间复杂度: O(n)
     */
    public List<Integer> preorderTraversalIterative(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        // 使用栈模拟递归调用
        java.util.Stack<TreeNode> stack = new java.util.Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            result.add(node.val);  // 访问根节点

            // 右子树先入栈（后处理）
            if (node.right != null) {
                stack.push(node.right);
            }
            // 左子树后入栈（先处理）
            if (node.left != null) {
                stack.push(node.left);
            }
        }

        return result;
    }

    // 测试
    public static void main(String[] args) {
        // 构建测试用例：[1,null,2,3]
        //       1
        //        \
        //         2
        //        /
        //       3
        TreeNode root = new TreeNode(1);
        root.right = new TreeNode(2);
        root.right.left = new TreeNode(3);

        BinaryTreePreorder solution = new BinaryTreePreorder();

        // 测试递归实现
        System.out.println("递归实现: " + solution.preorderTraversal(root));     // [1, 2, 3]

        // 测试迭代实现
        System.out.println("迭代实现: " + solution.preorderTraversalIterative(root)); // [1, 2, 3]

        // 额外测试用例：完整的二叉树
        //       1
        //      / \
        //     2   3
        //    / \
        //   4   5
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(3);
        root2.left.left = new TreeNode(4);
        root2.left.right = new TreeNode(5);

        System.out.println("完整树递归: " + solution.preorderTraversal(root2));  // [1, 2, 4, 5, 3]
        System.out.println("完整树迭代: " + solution.preorderTraversalIterative(root2)); // [1, 2, 4, 5, 3]
    }
}
