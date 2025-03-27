/**
 * COMP1039 - CW 1
 * @author  - Daokuan Wu (20614970)
 * @version - 2.3
 * @date    - 03/20/2025
 */

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Main class demonstrating binary search tree traversal.
 * Depending on the input size, it chooses either a non-recursive or a parallel postorder traversal.
 */
public class BST {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        // Read input from standard input, expecting comma-separated integers
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String[] inputParts = reader.readLine().split(",");

        // Determine the number of threads: limited to a maximum of 4
        int threadCount = Math.min(Runtime.getRuntime().availableProcessors(), 4);

        // For small input sizes, use non-recursive postorder traversal; otherwise, use parallel postorder traversal
        if (inputParts.length < 10000) {
            SimpleBinarySearchTree simpleBST = new SimpleBinarySearchTree();
            for (String part : inputParts) {
                simpleBST.insert(Integer.parseInt(part.trim()));
            }
            List<Integer> postOrderResult = simpleBST.postOrderTraversal();
            for (int value : postOrderResult) {
                System.out.println(value);
            }
        } else {
            ParallelBinarySearchTree parallelBST = new ParallelBinarySearchTree(threadCount);
            for (String part : inputParts) {
                parallelBST.insert(Integer.parseInt(part.trim()));
            }
            List<Integer> postOrderResult = parallelBST.parallelPostOrderTraversal();
            for (int value : postOrderResult) {
                System.out.println(value);
            }
        }
    }
}

/**
 * Represents a node in a binary search tree (package-private).
 */
class Node {
    int value;
    Node left;
    Node right;

    Node(int value) {
        this.value = value;
    }
}

/**
 * A binary search tree implementation that supports parallel postorder traversal using ForkJoinPool.
 */
class ParallelBinarySearchTree {
    private Node root;
    private final ForkJoinPool forkJoinPool;

    /**
     * Constructs a ParallelBinarySearchTree with a limited thread pool.
     *
     * @param threadPoolSize the maximum number of threads to use
     */
    public ParallelBinarySearchTree(int threadPoolSize) {
        this.forkJoinPool = new ForkJoinPool(threadPoolSize);
    }

    /**
     * Inserts a value into the binary search tree.
     *
     * @param value the value to insert
     */
    public void insert(int value) {
        if (root == null) {
            root = new Node(value);
            return;
        }
        Node current = root;
        while (true) {
            if (value < current.value) {
                if (current.left == null) {
                    current.left = new Node(value);
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node(value);
                    return;
                }
                current = current.right;
            }
        }
    }

    /**
     * Performs a parallel postorder traversal of the binary search tree.
     *
     * @return a list of integers representing the postorder traversal
     * @throws ExecutionException if the parallel computation fails
     * @throws InterruptedException if the parallel computation is interrupted
     */
    public List<Integer> parallelPostOrderTraversal() throws ExecutionException, InterruptedException {
        if (root == null) {
            return Collections.emptyList();
        }
        List<Integer> traversalResult = forkJoinPool.invoke(new PostOrderTraversalTask(root, 0));
        forkJoinPool.shutdown();
        return traversalResult;
    }

    /**
     * RecursiveTask to perform parallel postorder traversal.
     */
    private static class PostOrderTraversalTask extends RecursiveTask<List<Integer>> {
        private final Node node;
        private final int depth;
        private static final int MAX_PARALLEL_DEPTH = 10; // Limit the depth of parallel recursion

        PostOrderTraversalTask(Node node, int depth) {
            this.node = node;
            this.depth = depth;
        }

        @Override
        protected List<Integer> compute() {
            if (node == null) {
                return new ArrayList<>();
            }

            List<Integer> result = new ArrayList<>();

            // If the current recursion depth is within the parallel threshold, fork tasks for both subtrees
            if (depth < MAX_PARALLEL_DEPTH) {
                PostOrderTraversalTask leftTask = new PostOrderTraversalTask(node.left, depth + 1);
                PostOrderTraversalTask rightTask = new PostOrderTraversalTask(node.right, depth + 1);
                leftTask.fork();
                rightTask.fork();
                result.addAll(leftTask.join());
                result.addAll(rightTask.join());
            } else {
                // Otherwise, fall back to a single-threaded traversal for deeper recursion levels
                result.addAll(singleThreadedPostOrder(node.left));
                result.addAll(singleThreadedPostOrder(node.right));
            }

            result.add(node.value);
            return result;
        }

        /**
         * Helper method to perform single-threaded postorder traversal.
         *
         * @param node the root node of the subtree
         * @return a list of integers representing the postorder traversal of the subtree
         */
        private List<Integer> singleThreadedPostOrder(Node node) {
            List<Integer> result = new ArrayList<>();
            if (node == null) {
                return result;
            }
            result.addAll(singleThreadedPostOrder(node.left));
            result.addAll(singleThreadedPostOrder(node.right));
            result.add(node.value);
            return result;
        }
    }
}

/**
 * A simple binary search tree implementation that uses a non-recursive method for postorder traversal.
 */
class SimpleBinarySearchTree {
    private Node root;

    /**
     * Inserts a value into the binary search tree.
     *
     * @param value the value to insert
     */
    public void insert(int value) {
        if (root == null) {
            root = new Node(value);
            return;
        }
        Node current = root;
        while (true) {
            if (value < current.value) {
                if (current.left == null) {
                    current.left = new Node(value);
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node(value);
                    return;
                }
                current = current.right;
            }
        }
    }

    /**
     * Performs a non-recursive postorder traversal of the binary search tree using two stacks.
     *
     * @return a list of integers representing the postorder traversal
     */
    public List<Integer> postOrderTraversal() {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        Deque<Node> stack1 = new ArrayDeque<>();
        Deque<Node> stack2 = new ArrayDeque<>();

        // Initialize the first stack with the root node
        stack1.push(root);

        // Process nodes in a way that the second stack will have the nodes in postorder sequence
        while (!stack1.isEmpty()) {
            Node current = stack1.pop();
            stack2.push(current);
            if (current.left != null) {
                stack1.push(current.left);
            }
            if (current.right != null) {
                stack1.push(current.right);
            }
        }

        // Retrieve the postorder traversal by popping from the second stack
        while (!stack2.isEmpty()) {
            result.add(stack2.pop().value);
        }

        return result;
    }
}
