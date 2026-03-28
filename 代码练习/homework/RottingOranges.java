import java.util.LinkedList;
import java.util.Queue;

public class RottingOranges {

    public int orangesRotting(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        int freshCount = 0;

        // Step 1: 遍历网格，所有腐烂橘子入队，统计新鲜橘子数量
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 2) {
                    queue.offer(new int[]{i, j});
                } else if (grid[i][j] == 1) {
                    freshCount++;
                }
            }
        }

        // Step 2: 没有新鲜橘子，直接返回 0
        if (freshCount == 0) {
            return 0;
        }

        int minutes = 0;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

        // Step 3: 多源 BFS 层序遍历
        while (!queue.isEmpty()) {
            int size = queue.size();
            boolean infected = false;

            for (int k = 0; k < size; k++) {
                int[] cur = queue.poll();
                for (int[] d : dirs) {
                    int nr = cur[0] + d[0];
                    int nc = cur[1] + d[1];
                    if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2;
                        freshCount--;
                        infected = true;
                        queue.offer(new int[]{nr, nc});
                    }
                }
            }

            if (infected) {
                minutes++;
            }
        }

        // Step 4: 还有新鲜橘子则返回 -1
        return freshCount == 0 ? minutes : -1;
    }
}
