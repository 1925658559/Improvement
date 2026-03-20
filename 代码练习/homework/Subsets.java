import java.util.ArrayList;
import java.util.List;

/**
 * LeetCode 78 - 子集
 *
 * 题目：给你一个整数数组 nums，数组中的元素互不相同。返回该数组所有可能的子集（幂集）。
 *
 * 示例：
 * 输入：nums = [1,2,3]
 * 输出：[[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
 */
public class Subsets {

    /**
     * 主函数：返回所有子集
     */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> path = new ArrayList<>();

        // 从索引 0 开始回溯
        backtrack(nums, 0, path, result);

        return result;
    }

    /**
     * 回溯函数
     *
     * @param nums   原始数组
     * @param start  从哪个索引开始选择（避免重复）
     * @param path   当前正在构建的子集
     * @param result 结果集
     */
    private void backtrack(int[] nums, int start,
                          List<Integer> path, List<List<Integer>> result) {
        // 1. 把当前 path 加入结果集（每个节点都是一个有效的子集）
        result.add(new ArrayList<>(path));

        // 2. 遍历：从 start 开始，到最后一个元素
        for (int i = start; i < nums.length; i++) {
            // 3. 做选择：选择 nums[i]
            path.add(nums[i]);

            // 4. 递归：从 i+1 开始（避免重复选择）
            backtrack(nums, i + 1, path, result);

            // 5. 撤销选择（回溯）
            path.remove(path.size() - 1);
        }
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        Subsets solution = new Subsets();

        // 测试用例 1
        int[] nums1 = {1, 2, 3};
        List<List<Integer>> result1 = solution.subsets(nums1);
        System.out.println("输入: [1,2,3]");
        System.out.println("输出: " + result1);
        System.out.println("子集数量: " + result1.size() + " (期望: 8)");

        System.out.println();

        // 测试用例 2
        int[] nums2 = {0};
        List<List<Integer>> result2 = solution.subsets(nums2);
        System.out.println("输入: [0]");
        System.out.println("输出: " + result2);
        System.out.println("子集数量: " + result2.size() + " (期望: 2)");
    }
}
