import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 77 - 组合
 *
 * 题目：给定两个整数 n 和 k，返回范围 [1, n] 中所有可能的 k 个数的组合。
 *
 * 示例：
 * 输入：n = 4, k = 2
 * 输出：[[1,2],[1,3],[1,4],[2,3],[2,4],[3,4]]
 */
public class Combinations {

    /**
     * 主函数：返回所有组合
     */
    public List<List<Integer>> combine(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> path = new ArrayList<>();

        // TODO: 调用回溯函数，从 1 开始
        backtrack(n, k, 1, path, result);

        return result;
    }

    /**
     * 回溯函数
     *
     * @param n      范围 [1, n]
     * @param k      需要选择 k 个数
     * @param start  从哪个数字开始选择
     * @param path   当前正在构建的组合
     * @param result 结果集
     */
    private void backtrack(int n, int k, int start,
                          List<Integer> path, List<List<Integer>> result) {
        // TODO: 1. 递归终止条件
        //       提示：什么时候把 path 加入结果集？
        //       提示：加入后要 return 终止递归
        if (path.size() == k) {
            result.add(new ArrayList<>(path));
            return;
        }


        // TODO: 2. 遍历：从 start 开始，到 n
        //       提示：for (int i = start; i <= n; i++)
        for (int i = start; i <= n; i++){

            // TODO: 3. 做选择：选择数字 i
            path.add(i);

            // TODO: 4. 递归：从 i+1 开始
            backtrack(n, k, i + 1, path, result);

            // TODO: 5. 撤销选择（回溯）
            path.remove(path.size() - 1);
        }

    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        Combinations solution = new Combinations();

        // 测试用例 1
        int n1 = 4, k1 = 2;
        List<List<Integer>> result1 = solution.combine(n1, k1);
        System.out.println("输入: n = " + n1 + ", k = " + k1);
        System.out.println("输出: " + result1);
        System.out.println("组合数量: " + result1.size() + " (期望: 6)");

        System.out.println();

        // 测试用例 2
        int n2 = 1, k2 = 1;
        List<List<Integer>> result2 = solution.combine(n2, k2);
        System.out.println("输入: n = " + n2 + ", k = " + k2);
        System.out.println("输出: " + result2);
        System.out.println("组合数量: " + result2.size() + " (期望: 1)");
    }
}
