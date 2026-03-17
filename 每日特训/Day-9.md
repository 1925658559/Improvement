# Day 9: MySQL 索引类型 + 二叉树后序遍历

## 🔥 算法热身

**题目**: LeetCode 145 - 二叉树的后序遍历
**链接**: https://leetcode.cn/problems/binary-tree-postorder-traversal/
**状态**: ✅ Pass
**代码位置**: `代码练习/homework/BinaryTreePostorder.java`

**题目描述**:
给你二叉树的根节点 `root`，返回它节点值的**后序遍历**。

**示例**:
```
输入：root = [4,2,6,1,3]

       4
      / \
     2   6
    / \
   1   3

输出：[1,3,2,6,4]
```

---

## 📝 算法解法详解

### 解法一：递归实现（推荐）

**核心思想**：按照"左-右-根"的顺序递归遍历

```java
public List<Integer> postorderTraversal(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    postorder(root, result);
    return result;
}

private void postorder(TreeNode node, List<Integer> result) {
    // 终止条件
    if (node == null) {
        return;
    }

    // 递归体：左 - 右 - 根
    postorder(node.left, result);    // 1. 递归遍历左子树
    postorder(node.right, result);   // 2. 递归遍历右子树
    result.add(node.val);            // 3. 访问根节点
}
```

**复杂度分析**:
- 时间复杂度: O(n) - 每个节点访问一次
- 空间复杂度: O(h) - h 为树的高度，递归栈深度

---

### 解法二：迭代实现（使用栈 + prev）

**核心思想**：
- 后序遍历的难点：访问根节点前，必须确保左右子树都已访问
- 用 `prev` 记录上一个访问的节点，判断右子树是否已访问

```java
public List<Integer> postorderTraversalIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;

    Stack<TreeNode> stack = new Stack<>();
    TreeNode curr = root;
    TreeNode prev = null;  // 记录上一个访问的节点

    while (curr != null || !stack.isEmpty()) {
        // 1. 一直往左走，把所有左节点入栈
        while (curr != null) {
            stack.push(curr);
            curr = curr.left;
        }

        // 2. 查看栈顶节点（不弹出）
        curr = stack.peek();

        // 3. 判断是否可以访问当前节点
        if (curr.right == null || curr.right == prev) {
            // 右子树为空 或 右子树已访问 → 可以访问根节点
            result.add(curr.val);
            stack.pop();
            prev = curr;  // 记录已访问的节点
            curr = null;  // 避免重复访问左子树
        } else {
            // 右子树还未访问，转向右子树
            curr = curr.right;
        }
    }

    return result;
}
```

**复杂度分析**:
- 时间复杂度: O(n)
- 空间复杂度: O(n)

---

### 🔍 遍历过程演示

**给定树**：
```
       4
      / \
     2   6
    / \
   1   3
```

**迭代实现执行过程**：

| 步骤 | curr | stack | prev | 操作 | result |
|------|------|-------|------|------|--------|
| 1 | 4 | [] | null | 往左走 | [] |
| 2 | 2 | [4] | null | 往左走 | [] |
| 3 | 1 | [4,2] | null | 往左走 | [] |
| 4 | null | [4,2,1] | null | 左走到头 | [] |
| 5 | 1 | [4,2,1] | null | peek(1)，无右子树 | [] |
| 6 | null | [4,2] | 1 | 访问1，curr=null | [1] |
| 7 | 2 | [4,2] | 1 | peek(2)，有右子树且未访问 | [1] |
| 8 | 3 | [4,2] | 1 | 转向右子树 | [1] |
| 9 | null | [4,2,3] | 1 | 往左走 | [1] |
| 10 | 3 | [4,2,3] | 1 | peek(3)，无右子树 | [1] |
| 11 | null | [4,2] | 3 | 访问3，curr=null | [1,3] |
| 12 | 2 | [4,2] | 3 | peek(2)，右子树=prev | [1,3] |
| 13 | null | [4] | 2 | 访问2，curr=null | [1,3,2] |
| 14 | 4 | [4] | 2 | peek(4)，有右子树且未访问 | [1,3,2] |
| 15 | 6 | [4] | 2 | 转向右子树 | [1,3,2] |
| 16 | null | [4,6] | 2 | 往左走 | [1,3,2] |
| 17 | 6 | [4,6] | 2 | peek(6)，无右子树 | [1,3,2] |
| 18 | null | [4] | 6 | 访问6，curr=null | [1,3,2,6] |
| 19 | 4 | [4] | 6 | peek(4)，右子树=prev | [1,3,2,6] |
| 20 | null | [] | 4 | 访问4，curr=null | [1,3,2,6,4] |

---

### 🔍 两种解法对比

| 解法 | 时间复杂度 | 空间复杂度 | 优点 | 缺点 |
|------|------------|------------|------|------|
| **递归** | O(n) | O(h) | 代码简洁，易理解 | 递归深度过大可能栈溢出 |
| **迭代** | O(n) | O(n) | 无递归开销 | 代码复杂，需要 prev 辅助 |

---

### ⚠️ 常见错误

**1. 递归终止条件遗漏**
```java
// ❌ 错误代码：没有检查 null
private void postorder(TreeNode node, List<Integer> result) {
    postorder(node.left, result);  // NPE！
    postorder(node.right, result);
    result.add(node.val);
}

// ✅ 正确代码
if (node == null) return;
```

**2. 混淆三种遍历顺序**
```
前序遍历：根 → 左 → 右
中序遍历：左 → 根 → 右
后序遍历：左 → 右 → 根
```

**3. 迭代实现忘记设置 curr = null**
```java
// ❌ 错误代码：访问完节点后没有设置 curr = null
result.add(curr.val);
stack.pop();
prev = curr;
// 缺少：curr = null;  ← 会导致重复访问左子树

// ✅ 正确代码
result.add(curr.val);
stack.pop();
prev = curr;
curr = null;  // 避免重复访问左子树
```

**4. 迭代实现使用 pop() 而不是 peek()**
```java
// ❌ 错误代码：直接 pop，无法判断右子树
curr = stack.pop();  // 弹出后无法再次访问
if (curr.right == null || curr.right == prev) {
    result.add(curr.val);
}

// ✅ 正确代码：先 peek 判断，再决定是否 pop
curr = stack.peek();  // 只查看，不弹出
if (curr.right == null || curr.right == prev) {
    result.add(curr.val);
    stack.pop();  // 确认可以访问后再弹出
}
```

---


## 🎯 今日技能评估

| 技能点 | 状态 | 说明 |
|--------|------|------|
| LeetCode 145 (二叉树后序遍历) | ✅ Proficient | 递归 + 迭代两种实现 |
| 聚簇索引 vs 非聚簇索引 | ✅ Proficient | 理解叶子节点存储内容的区别 |
| 回表机制 | ✅ Proficient | 理解回表过程和性能影响 |
| 覆盖索引 | ✅ Proficient | 理解如何避免回表 |
| 索引失效场景 | ✅ Proficient | 掌握6种常见索引失效场景 |

---

## 🚀 明日计划 (Day 10)

1. **MySQL MVCC 原理**：
   - 版本链（Undo Log）
   - ReadView 机制
   - 可重复读的实现

2. **算法**：二叉树层序遍历（BFS）

---
