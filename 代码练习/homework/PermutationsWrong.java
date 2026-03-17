import java.util.ArrayList;
import java.util.List;

/**
 * 错误示例：直接 add(path) 而不是 add(new ArrayList<>(path))
 */
public class PermutationsWrong {

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        List<Integer> path = new ArrayList<>();

        backtrack(nums, used, path, result);
        return result;
    }

    private void backtrack(int[] nums, boolean[] used,
                           List<Integer> path, List<List<Integer>> result) {
        if (path.size() == nums.length) {
            // ❌ 错误：直接 add(path)
            result.add(path);  // 没有创建新的 ArrayList
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) {
                continue;
            }

            path.add(nums[i]);
            used[i] = true;

            backtrack(nums, used, path, result);

            path.remove(path.size() - 1);
            used[i] = false;
        }
    }

    public static void main(String[] args) {
        PermutationsWrong solution = new PermutationsWrong();

        int[] nums = {1, 2, 3};
        List<List<Integer>> result = solution.permute(nums);

        System.out.println("输入: [1,2,3]");
        System.out.println("输出: " + result);
        System.out.println("期望: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]");
        System.out.println("\n❌ 所有列表都是空的！因为它们都指向同一个 path 对象，而 path 最终被清空了。");
    }
}
