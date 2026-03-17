import java.util.*;

/**
 * LeetCode 102 - 二叉树的层序遍历
 * 层序遍历：一层一层地遍历（BFS - 广度优先遍历）
 */
public class BinaryTreeLevelOrder {

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
     * 解法一：BFS + 队列（推荐）
     * 核心思想：
     * - 使用队列实现"先进先出"
     * - 每次处理一层前，先记录当前队列大小（当前层的节点数）
     * - 处理完当前层后，队列中就是下一层的所有节点
     *
     * 时间复杂度：O(n) - 每个节点访问一次
     * 空间复杂度：O(n) - 队列最多存储一层的节点
     */
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);  // 将根节点加入队列

        while (!queue.isEmpty()) {
            // 关键：记录当前层的节点数量
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>();

            // 处理当前层的所有节点
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();  // 取出队首节点
                currentLevel.add(node.val);    // 访问节点

                // 将子节点加入队列（为下一层做准备）
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            // 将当前层的结果加入最终结果
            result.add(currentLevel);
        }

        return result;
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        BinaryTreeLevelOrder solution = new BinaryTreeLevelOrder();

        // 构建测试树：
        //       3
        //      / \
        //     9  20
        //       /  \
        //      15   7
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);

        // 测试层序遍历
        List<List<Integer>> result = solution.levelOrder(root);
        System.out.println("层序遍历结果: " + result);  // [[3], [9, 20], [15, 7]]
    }
}
