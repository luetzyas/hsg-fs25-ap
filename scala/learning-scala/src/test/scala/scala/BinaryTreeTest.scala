package scala

import org.scalatest.funsuite.AnyFunSuite

class BinaryTreeTest extends AnyFunSuite:
  import BinaryTree.*

//  TODO de-comment after Task 2
//  private val btree: BTree =
//    BTree.Node(1,
//      BTree.Node(2,
//        BTree.Empty,
//        BTree.Node(4, BTree.Empty, BTree.Empty)
//      ),
//      BTree.Node(3,
//        BTree.Node(5, BTree.Empty, BTree.Empty),
//        BTree.Empty
//      )
//    )
//  test("Task 2 - BTree - structure"):
//    assert(btree.toString == "Node(1,Node(2,Empty,Node(4,Empty,Empty)),Node(3,Node(5,Empty,Empty),Empty))")

//  TODO de-comment after Task 3
//  private val traversable: Traversable[Int] = TraversableBTree(btree)
//  test("Task 3 - TraversableBTree - traverse"):
//    val traversed = traversable.traverse()
//    assert(List(1, 2, 4, 3, 5).forall(traversed.contains(_)))

//  TODO de-comment after Task 4
//  private val dfs: Traversable[Int] = OrderedTraversableBTree(btree, TraversalStrategy.DepthFirstSearch)
//  test("Task 4 - OrderedTraversableBTree - DFS"):
//    assert(dfs.traverse() == List(1, 2, 4, 3, 5))
//  private val bfs: Traversable[Int] = OrderedTraversableBTree(btree, TraversalStrategy.BreadthFirstSearch)
//  test("Task 4 - OrderedTraversableBTree - BFS"):
//    assert(bfs.traverse() == List(1, 2, 3, 4, 5))
