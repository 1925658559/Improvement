import java.util.*;

/**
 * LeetCode 90 - 子集 II
 *
 * 题目：给你一个整数数组 nums，其中可能包含重复元素，请你返回该数组所有可能的子集（幂集）。
 * 解集不能包含重复的子集。返回的解集中，子集可以按任意顺序排列。
 *
 * 示例：
 * 输入：nums = [1,2,2]
 * 输出：[[],[1],[1,2],[1,2,2],[2],[2,2]]
 */
public class SubsetsII {

    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> path = new ArrayList<>();

        // TODO 1: 先对数组排序（为什么需要排序？）


        // TODO 2: 从索引 0 开始回溯


        return result;
    }

    private void backtrack(int[] nums, int start,
                          List<Integer> path, List<List<Integer>> result) {
        // TODO 3: 把当前 path 加入结果集


        // TODO 4: 遍历：从 start 开始
        for (int i = start; i < nums.length; i++) {
            // TODO 5: 去重逻辑（关键！）
            // 如果当前元素和前一个元素相同，且不是当前层的第一个选择，就跳过


            // TODO 6: 做选择


            // TODO 7: 递归


            // TODO 8: 撤销选择（回溯）

        }
    }

    public static void main(String[] args) {
        SubsetsII solution = new SubsetsII();

        // 测试用例 1
        int[] nums1 = {1, 2, 2};
        System.out.println("输入: [1,2,2]");
        System.out.println("输出: " + solution.subsetsWithDup(nums1));
        System.out.println();

        // 测试用例 2
        int[] nums2 = {0};
        System.out.println("输入: [0]");
        System.out.println("输出: " + solution.subsetsWithDup(nums2));
    }
}
