/**
 * LeetCode 200 - 岛屿数量
 */
public class NumberOfIslands {

    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }

        int count = 0;

        // TODO: 双层循环遍历网格，遇到'1'时count++并调用dfs
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == '1') {
                    count++;
                    dfs(grid, row, col);
                }
            }
        }
        return count;
    }

    private void dfs(char[][] grid, int row, int col) {
        // TODO: 写出递归终止条件（越界 或 遇到'0'）
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[row].length || grid[row][col] == '0') {
            return;
        }

        // TODO: 标记当前位置为'0'
        grid[row][col] = '0';

        // TODO: 递归四个方向（上下左右）
        dfs(grid, row + 1, col);
        dfs(grid, row - 1, col);
        dfs(grid, row, col + 1);
        dfs(grid, row, col - 1);
    }

    public static void main(String[] args) {
        NumberOfIslands solution = new NumberOfIslands();

        char[][] grid1 = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        System.out.println("预期: 3, 实际: " + solution.numIslands(grid1));

        char[][] grid2 = {
            {'1','1','1','1','0'},
            {'1','1','0','1','0'},
            {'1','1','0','0','0'},
            {'0','0','0','0','0'}
        };
        System.out.println("预期: 1, 实际: " + solution.numIslands(grid2));
    }
}
