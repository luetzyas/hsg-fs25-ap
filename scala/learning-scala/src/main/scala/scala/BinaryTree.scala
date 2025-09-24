package scala

/**
 * This exercise aims to practice basic features of Scala, focusing on
 * object-oriented programming more than functional programming.
 *
 * After each task, check your solution against the tests in
 * `src/test/scala/scala/BinaryTreeTest`.
 */
object BinaryTree:
  /**
   * Task 1: Traversable (Design)
   * Define an interface for Traversable collections.
   *
   * A Traversable has a single method `traverse` that allows you to look
   * at all the elements in a generic collection. You can use List[A] to
   * return the elements and the order used to look at the elements.
   */
  TODO("write the Traversable interface")

  /**
   * Task 2: Binary Tree (Implementation)
   * Define a BTree collection, modelling a binary tree.
   *
   * A binary tree is an inductive data structure consisting of nodes,
   * where each node has at most two children (left and right).
   * 1. What is the base case of a binary tree?
   * 2. What is the inductive case of a binary tree?
   * 3. Which Scala features is better suited for modelling such *inductive* data structure?
   *
   * Extend your definition so that each node contains an Int value.
   */
  TODO("write the BTree implementation")

  /**
   * Task 3: Traversable Binary Tree (Implementation)
   * Define a TraversableBTree, which wraps a BTree (i.e., strategy pattern)
   * and extends the Traversable interface.
   */
  TODO("write the TraversableBTree implementation")

  /**
   * Task 4: Ordered Traversable Binary Tree (Implementation)
   * Define an OrderedTraversableBTree, which behaves exactly like a TraversableBTree,
   * except it is parametrized by the order of traversal: either depth-first-search
   * (DFS) or breadth-first-search (BFS).
   *
   * 1. How can you model the traversal strategy?
   * 2. How can you integrate the traversal strategy with TraversableBTree?
   */
  TODO("write the OrderedTraversableBTree implementation")

  // ------------------------------------------------------------------------------------
  // --- Skip below this line -----------------------------------------------------------
  // ------------------------------------------------------------------------------------
  /**
   * A no-op function to mark missing code gracefully.
   * Usually, you don't want to mark it gracefully and you would use `???`.
   */
  private def TODO(msg: String) = ()
