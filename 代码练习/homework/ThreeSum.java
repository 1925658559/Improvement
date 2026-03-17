import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 15 - 3 Sum
 * 优化解法：排序 + 双指针
 * 时间复杂度：O(n²)
 * 空间复杂度：O(1)
 */
public class ThreeSum {

    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();

        // 边界检查
        if (nums == null || nums.length < 3) {
            return result;
        }

        // 1. 先排序 - O(n log n)
        Arrays.sort(nums);

        // 2. 固定第一个数，双指针找后两个数 - O(n²)
        for (int i = 0; i < nums.length - 2; i++) {

            // 去重：如果当前数和前一个数相同，跳过
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }

            // 剪枝优化：如果最小值都 > 0，后面不可能有解
            if (nums[i] > 0) {
                break; // 排序后，后面的数更大，不可能和为0
            }

            int left = i + 1;           // 左指针
            int right = nums.length - 1; // 右指针

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];

                if (sum == 0) {
                    // 找到一组解
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));

                    // 去重：跳过重复的 left
                    while (left < right && nums[left] == nums[left + 1]) {
                        left++;
                    }
                    // 去重：跳过重复的 right
                    while (left < right && nums[right] == nums[right - 1]) {
                        right--;
                    }

                    // 移动指针继续寻找
                    left++;
                    right--;
                } else if (sum < 0) {
                    // 和太小，left 右移让和变大
                    left++;
                } else {
                    // 和太大，right 左移让和变小
                    right--;
                }
            }
        }

        return result;
    }

    // 测试
    public static void main(String[] args) {
        ThreeSum solution = new ThreeSum();
        int[] nums = {-1, 0, 1, 2, -1, -4};
        List<List<Integer>> result = solution.threeSum(nums);

        System.out.println("输入：nums = " + Arrays.toString(nums));
        System.out.println("输出：" + result);
        // 期望输出：[[-1, -1, 2], [-1, 0, 1]]
    }
}
