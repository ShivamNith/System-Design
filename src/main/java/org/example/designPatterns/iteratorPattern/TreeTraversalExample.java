package org.example.designPatterns.iteratorPattern;

import java.util.*;

/**
 * Iterator Pattern Example: Binary Tree Traversal
 * 
 * This example demonstrates the Iterator Pattern with a binary tree
 * that supports different traversal algorithms (in-order, pre-order, post-order, level-order).
 */

// Tree node class
class TreeNode {
    int value;
    TreeNode left;
    TreeNode right;
    
    public TreeNode(int value) {
        this.value = value;
        this.left = null;
        this.right = null;
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

// Abstract tree iterator
abstract class TreeIterator implements Iterator<TreeNode> {
    protected Stack<TreeNode> stack;
    protected TreeNode current;
    
    public TreeIterator(TreeNode root) {
        this.stack = new Stack<>();
        this.current = root;
        initializeIterator();
    }
    
    protected abstract void initializeIterator();
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported for tree traversal");
    }
}

// In-order traversal iterator (Left -> Root -> Right)
class InOrderIterator extends TreeIterator {
    
    public InOrderIterator(TreeNode root) {
        super(root);
    }
    
    @Override
    protected void initializeIterator() {
        pushLeftNodes(current);
    }
    
    private void pushLeftNodes(TreeNode node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }
    
    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }
    
    @Override
    public TreeNode next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements in in-order traversal");
        }
        
        TreeNode node = stack.pop();
        
        // Push right subtree's left nodes
        if (node.right != null) {
            pushLeftNodes(node.right);
        }
        
        return node;
    }
}

// Pre-order traversal iterator (Root -> Left -> Right)
class PreOrderIterator extends TreeIterator {
    
    public PreOrderIterator(TreeNode root) {
        super(root);
    }
    
    @Override
    protected void initializeIterator() {
        if (current != null) {
            stack.push(current);
        }
    }
    
    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }
    
    @Override
    public TreeNode next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements in pre-order traversal");
        }
        
        TreeNode node = stack.pop();
        
        // Push right first, then left (since stack is LIFO)
        if (node.right != null) {
            stack.push(node.right);
        }
        if (node.left != null) {
            stack.push(node.left);
        }
        
        return node;
    }
}

// Post-order traversal iterator (Left -> Right -> Root)
class PostOrderIterator extends TreeIterator {
    private Stack<TreeNode> outputStack;
    
    public PostOrderIterator(TreeNode root) {
        super(root);
        this.outputStack = new Stack<>();
    }
    
    @Override
    protected void initializeIterator() {
        if (current != null) {
            stack.push(current);
            
            // Use two stacks to achieve post-order
            while (!stack.isEmpty()) {
                TreeNode node = stack.pop();
                outputStack.push(node);
                
                if (node.left != null) {
                    stack.push(node.left);
                }
                if (node.right != null) {
                    stack.push(node.right);
                }
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        return !outputStack.isEmpty();
    }
    
    @Override
    public TreeNode next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements in post-order traversal");
        }
        
        return outputStack.pop();
    }
}

// Level-order traversal iterator (Breadth-First)
class LevelOrderIterator implements Iterator<TreeNode> {
    private Queue<TreeNode> queue;
    
    public LevelOrderIterator(TreeNode root) {
        this.queue = new LinkedList<>();
        if (root != null) {
            queue.offer(root);
        }
    }
    
    @Override
    public boolean hasNext() {
        return !queue.isEmpty();
    }
    
    @Override
    public TreeNode next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements in level-order traversal");
        }
        
        TreeNode node = queue.poll();
        
        if (node.left != null) {
            queue.offer(node.left);
        }
        if (node.right != null) {
            queue.offer(node.right);
        }
        
        return node;
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported for tree traversal");
    }
}

// Binary Tree class with iterator support
class BinaryTree {
    private TreeNode root;
    
    public BinaryTree() {
        this.root = null;
    }
    
    public BinaryTree(TreeNode root) {
        this.root = root;
    }
    
    public void setRoot(TreeNode root) {
        this.root = root;
    }
    
    public TreeNode getRoot() {
        return root;
    }
    
    // Iterator factory methods
    public Iterator<TreeNode> inOrderIterator() {
        return new InOrderIterator(root);
    }
    
    public Iterator<TreeNode> preOrderIterator() {
        return new PreOrderIterator(root);
    }
    
    public Iterator<TreeNode> postOrderIterator() {
        return new PostOrderIterator(root);
    }
    
    public Iterator<TreeNode> levelOrderIterator() {
        return new LevelOrderIterator(root);
    }
    
    // Helper method to insert nodes (for demo purposes)
    public void insert(int value) {
        root = insertRec(root, value);
    }
    
    private TreeNode insertRec(TreeNode root, int value) {
        if (root == null) {
            return new TreeNode(value);
        }
        
        if (value < root.value) {
            root.left = insertRec(root.left, value);
        } else if (value > root.value) {
            root.right = insertRec(root.right, value);
        }
        
        return root;
    }
}

// Utility class for tree operations
class TreeTraversalUtils {
    
    public static void printTraversal(String traversalType, Iterator<TreeNode> iterator) {
        System.out.print(traversalType + ": ");
        List<String> values = new ArrayList<>();
        while (iterator.hasNext()) {
            values.add(iterator.next().toString());
        }
        System.out.println(String.join(" -> ", values));
    }
    
    public static void demonstrateMultipleIterators(BinaryTree tree) {
        System.out.println("Demonstrating Multiple Simultaneous Iterators:");
        
        Iterator<TreeNode> inOrder = tree.inOrderIterator();
        Iterator<TreeNode> preOrder = tree.preOrderIterator();
        
        System.out.println("Interleaving in-order and pre-order traversals:");
        while (inOrder.hasNext() || preOrder.hasNext()) {
            if (inOrder.hasNext()) {
                System.out.print("InOrder: " + inOrder.next() + " ");
            }
            if (preOrder.hasNext()) {
                System.out.print("PreOrder: " + preOrder.next() + " ");
            }
            System.out.println();
        }
    }
}

// Client code demonstrating the Iterator Pattern with trees
public class TreeTraversalExample {
    public static void main(String[] args) {
        System.out.println("=== Binary Tree Iterator Pattern Demo ===\n");
        
        // Create a binary search tree
        BinaryTree tree = new BinaryTree();
        int[] values = {50, 30, 70, 20, 40, 60, 80, 10, 25, 35, 45};
        
        for (int value : values) {
            tree.insert(value);
        }
        
        System.out.println("Binary Search Tree created with values: " + Arrays.toString(values));
        System.out.println("\nTree structure:");
        System.out.println("       50");
        System.out.println("      /  \\");
        System.out.println("     30   70");
        System.out.println("    / \\   / \\");
        System.out.println("   20 40 60 80");
        System.out.println("  / \\ / \\");
        System.out.println(" 10 25 35 45");
        System.out.println();
        
        // Demonstrate different traversal iterators
        TreeTraversalUtils.printTraversal("In-Order Traversal", tree.inOrderIterator());
        TreeTraversalUtils.printTraversal("Pre-Order Traversal", tree.preOrderIterator());
        TreeTraversalUtils.printTraversal("Post-Order Traversal", tree.postOrderIterator());
        TreeTraversalUtils.printTraversal("Level-Order Traversal", tree.levelOrderIterator());
        
        System.out.println();
        
        // Demonstrate multiple simultaneous iterators
        TreeTraversalUtils.demonstrateMultipleIterators(tree);
        
        // Demonstrate iterator reusability
        System.out.println("\nDemonstrating Iterator Reusability:");
        Iterator<TreeNode> firstInOrder = tree.inOrderIterator();
        Iterator<TreeNode> secondInOrder = tree.inOrderIterator();
        
        System.out.println("First iterator (first 5 elements): ");
        for (int i = 0; i < 5 && firstInOrder.hasNext(); i++) {
            System.out.print(firstInOrder.next() + " ");
        }
        System.out.println();
        
        System.out.println("Second iterator (all elements): ");
        while (secondInOrder.hasNext()) {
            System.out.print(secondInOrder.next() + " ");
        }
        System.out.println();
        
        System.out.println("First iterator (remaining elements): ");
        while (firstInOrder.hasNext()) {
            System.out.print(firstInOrder.next() + " ");
        }
        System.out.println();
        
        // Demonstrate with a manually created tree for better visualization
        System.out.println("\n=== Custom Tree Example ===");
        TreeNode customRoot = new TreeNode(1);
        customRoot.left = new TreeNode(2);
        customRoot.right = new TreeNode(3);
        customRoot.left.left = new TreeNode(4);
        customRoot.left.right = new TreeNode(5);
        customRoot.right.left = new TreeNode(6);
        customRoot.right.right = new TreeNode(7);
        
        BinaryTree customTree = new BinaryTree(customRoot);
        
        System.out.println("Custom Tree structure:");
        System.out.println("    1");
        System.out.println("   / \\");
        System.out.println("  2   3");
        System.out.println(" / \\ / \\");
        System.out.println("4  5 6  7");
        System.out.println();
        
        TreeTraversalUtils.printTraversal("In-Order", customTree.inOrderIterator());
        TreeTraversalUtils.printTraversal("Pre-Order", customTree.preOrderIterator());
        TreeTraversalUtils.printTraversal("Post-Order", customTree.postOrderIterator());
        TreeTraversalUtils.printTraversal("Level-Order", customTree.levelOrderIterator());
    }
}

/*
Expected Output:
=== Binary Tree Iterator Pattern Demo ===

Binary Search Tree created with values: [50, 30, 70, 20, 40, 60, 80, 10, 25, 35, 45]

Tree structure:
       50
      /  \
     30   70
    / \   / \
   20 40 60 80
  / \ / \
 10 25 35 45

In-Order Traversal: 10 -> 20 -> 25 -> 30 -> 35 -> 40 -> 45 -> 50 -> 60 -> 70 -> 80
Pre-Order Traversal: 50 -> 30 -> 20 -> 10 -> 25 -> 40 -> 35 -> 45 -> 70 -> 60 -> 80
Post-Order Traversal: 10 -> 25 -> 20 -> 35 -> 45 -> 40 -> 30 -> 60 -> 80 -> 70 -> 50
Level-Order Traversal: 50 -> 30 -> 70 -> 20 -> 40 -> 60 -> 80 -> 10 -> 25 -> 35 -> 45

Demonstrating Multiple Simultaneous Iterators:
Interleaving in-order and pre-order traversals:
InOrder: 10 PreOrder: 50 
InOrder: 20 PreOrder: 30 
InOrder: 25 PreOrder: 20 
InOrder: 30 PreOrder: 10 
InOrder: 35 PreOrder: 25 
InOrder: 40 PreOrder: 40 
InOrder: 45 PreOrder: 35 
InOrder: 50 PreOrder: 45 
InOrder: 60 PreOrder: 70 
InOrder: 70 PreOrder: 60 
InOrder: 80 PreOrder: 80 

Demonstrating Iterator Reusability:
First iterator (first 5 elements): 
10 20 25 30 35 
Second iterator (all elements): 
10 20 25 30 35 40 45 50 60 70 80 
First iterator (remaining elements): 
40 45 50 60 70 80 

=== Custom Tree Example ===
Custom Tree structure:
    1
   / \
  2   3
 / \ / \
4  5 6  7

In-Order: 4 -> 2 -> 5 -> 1 -> 6 -> 3 -> 7
Pre-Order: 1 -> 2 -> 4 -> 5 -> 3 -> 6 -> 7
Post-Order: 4 -> 5 -> 2 -> 6 -> 7 -> 3 -> 1
Level-Order: 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7
*/