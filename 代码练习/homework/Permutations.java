import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 46 - 全排列
 *
 * 题目描述：
 * 给定一个不含重复数字的数组 nums，返回其所有可能的全排列。
 *
 * 示例：
 * 输入：nums = [1,2,3]
 * 输出：[[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
 *
 * 核心思路：
 * 1. 使用回溯算法（DFS）
 * 2. 用 boolean[] used 标记哪些数字已经被使用
 * 3. 用 List<Integer> path 存储当前正在构建的排列
 * 4. 递归终止条件：path.size() == nums.length
 */
public class Permutations {

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        List<Integer> path = new ArrayList<>();

        backtrack(nums, used, path, result);
        return result;
    }

    /**
     * 回溯函数
     *
     * @param nums   原始数组
     * @param used   标记数组，used[i]=true 表示 nums[i] 已被使用
     * @param path   当前正在构建的排列
     * @param result 结果集，存储所有排列
     */
    private void backtrack(int[] nums, boolean[] used,
                           List<Integer> path, List<List<Integer>> result) {
        // TODO 1: 写出递归终止条件
        // 提示：什么时候 path 就是一个完整的排列了？
        // 提示：path 的长度等于 nums 的长度时，说明所有数字都选完了
                            if (path.size() == nums.length) {
                                result.add(new ArrayList<>(path));
                                return;
                            } 


        // TODO 2: 遍历所有数字，尝试每一个选择
        for (int i = 0; i < nums.length; i++) {
            // TODO 3: 如果这个数字已经用过了，跳过
            // 提示：检查 used[i] 是否为 true
            if (used[i]) {
                continue;
            }



            // TODO 4: 做选择（选择这个数字）
            // 提示：需要做两件事：
            //   1. 把 nums[i] 加入 path
            //   2. 标记 used[i] = true
            path.add(nums[i]);
            used[i] = true;



            // TODO 5: 递归（继续处理下一个位置）
            // 提示：调用 backtrack 函数
            backtrack(nums, used, path, result);



            // TODO 6: 撤销选择（回溯）
            // 提示：需要做两件事（与 TODO 4 相反）：
            //   1. 把 path 的最后一个元素移除
            //   2. 标记 used[i] = false
            path.remove(path.size() - 1);
            used[i] = false;


        }
    }

    // 测试代码
    public static void main(String[] args) {
        Permutations solution = new Permutations();

        // 测试用例 1
        int[] nums1 = {1, 2, 3};
        List<List<Integer>> result1 = solution.permute(nums1);
        System.out.println("输入: [1,2,3]");
        System.out.println("输出: " + result1);
        System.out.println("期望: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]");
        System.out.println();

        // 测试用例 2
        int[] nums2 = {0, 1};
        List<List<Integer>> result2 = solution.permute(nums2);
        System.out.println("输入: [0,1]");
        System.out.println("输出: " + result2);
        System.out.println("期望: [[0,1],[1,0]]");
        System.out.println();

        // 测试用例 3
        int[] nums3 = {1};
        List<List<Integer>> result3 = solution.permute(nums3);
        System.out.println("输入: [1]");
        System.out.println("输出: " + result3);
        System.out.println("期望: [[1]]");
    }
}
