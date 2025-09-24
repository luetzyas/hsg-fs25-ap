error id: file:///D:/HSG/hsg-fs25-ap/scala/learning-scala/src/main/scala/scala/Overview.scala:local119
file:///D:/HSG/hsg-fs25-ap/scala/learning-scala/src/main/scala/scala/Overview.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -MyList2.Cons.
	 -MyList2.Cons#
	 -MyList2.Cons().
	 -scala/Predef.MyList2.Cons.
	 -scala/Predef.MyList2.Cons#
	 -scala/Predef.MyList2.Cons().
offset: 7990
uri: file:///D:/HSG/hsg-fs25-ap/scala/learning-scala/src/main/scala/scala/Overview.scala
text:
```scala
package scala

import scala.compiletime.ops.int

/**
 * This is an overview of the most common Scala features.
 * Let's complete it together.
 */
object Overview:

  /** Here is how you can print stuff. */
  @main def printing() =
    // Print Hello World
    println("Hello, World!")

  /** There are variables, values, and definitions. */
  @main def varValAndDef() =
    // Mutable Variable = constant
    var mutable = 42
    println(s"mutable before assignment: $mutable")

    var x: Int = 0
    // Assignment
    mutable = 43
    println(s"mutable after assignment:  $mutable")
    // Immutable Variable (Constant)
    val immutable = 42
    println(s"immutable: $immutable")
    //
    x = 1
    // Assignment Error
    //immutable = 43 // <- uncommenting this line should give you an error
    println(s"immutable after assignment error: $immutable")
    // Definitions: like val, but recomputed every time (just like a function)
    def z = 0
    //z = 0 // <- uncommenting this line should give you an error
    def foo(x:Int): Int = x + 1
  
  /** There are no statements: everything is an expression (i.e., returns something). */
  @main def expressions() =
    // Diff Expressions and Statements
    // Expressions are evaluated to a value, while statements are not
    val expr = 1 + 2 // Expression returning Int
    val stmt = println("Hello")
    // Int
    val int = 1 + 2
    // Block Returning Int
    val block = {
      val x = 1
      val y = 2
      x + y // The value of the block is the value of the last expression
    }
    // If-Then-Else: same as functional if-then-else in Java (e.g., `true ? 0 : 10`)
    //condition ? value1 : value2
    val ite = if (true) 0 else 10
    // Empty Block: what about this?
    val emptyBlock = {} // returns Unit
    // For Loop: ...and this?
    val forLoop = for (i <- 1 to 5) yield i * 2
    // While Loop: ...and this?
    val whileLoop = {
      var i = 1
      var result = 0
      while (i <= 5) {
        result += i * 2
        i += 1
      }
      result
    }
    // Print all values
    println(s"expr: $expr, stmt: $stmt, int: $int, block: $block, ite: $ite, emptyBlock: $emptyBlock, forLoop: $forLoop, whileLoop: $whileLoop")

  /** Have a look a scala type system: https://docs.scala-lang.org/tour/unified-types.html */
  @main def typeSystem() =
    // Any: can be Int or List or anything else
    //https://docs.scala-lang.org/resources/images/tour/unified-types-diagram.svg
    val any: Any = List()
    // AnyVal: can be Boolean or Int or Unit or any other value
    val value: AnyVal = 'a'
    // Unit: has an instance! (and only one)
    val unit: Unit = ()
    // Nothing: has no instances. It is the type thrown errors! In fact, ??? throws unimplemented exception.
    //val nothing: Nothing = throw new Exception("you created a nothing type!") // if this occurs, the program crashes
    //val nothing2: Nothing = ???
    // AnyRef: alias for java.lang.Object
    val reference: AnyRef = java.lang.Object() //new is not mandaroty in Scala, but it is needed for Java interop
    //print all values
    println(s"any: $any, value: $value, unit: $unit, reference: $reference")

  /** Functions are first-class citizen in Scala: they are values like anything else. */
  /** Even more, anything implementing the `apply` method can be used as a function. */
  @main def functions() =
    // These unary functions are all the same: no conceptual difference between lambdas and functions
    // Unary Function
    def increment(x:Int): Int = x + 1 //if in a block the last line is the return value, no need for return keyword
    // Lambda
    val inc = (x: Int) => x + 1
    val inc2: Int => Int = x => x + 1  // you can also specify the type of the lambda
    // Lambda with anonymous argument
    val plusOne: Int => Int = _ + 1 // _ is a placeholder for the single argument of the lambda
    val sum: (Int, Int) => Int = _ + _ // _ is a placeholder for the two arguments of the lambda
    println(s"increment: ${increment(1)}")
    println(s"inc      : ${inc(1)}")
    println(s"plusOne  : ${plusOne(1)}")
    println(s"sum      : ${sum(1,2)}")

    // These binary functions are all the same
    // Binary Function
    def add(x: Int, y: Int): Int = x + y
    // Lambda with anonymous arguments
    val add2: (Int, Int) => Int = (x, y) => x + y
    val add3: (Int, Int) => Int = _ + _
    println(s"sum: ${sum(1,2)}")
    println(s"add: ${add(1,2)}")

  /** If functions are values, functions can accept and return other functions! */
  @main def higherOrderFunctions() =
    // Higher-Order Function: here we take a function as first argument, and apply it to the second argument
    def apply(f: Int => Int, x: Int): Int = f(x)
    println(s"apply: ${apply(x => x + 1, 10)}")
    def inc(x: Int): Int = x + 1
    println(s"apply: ${apply(inc, 10)}")
    println(s"apply: ${apply(_ + 1, 10)}")

    // Curried Binary Function: currying is a way to define functions as higher-order functions taking one argument at a time
    def sum(x: Int)(y: Int): Int = x + y
    // def sum: Int => Int => Int = x => y => x + y // this is what the compiler does
    //def sum: Int => (Int => Int) = x => (y => x + y) 
    println(s"sum: ${sum(1)(2)}")
    val five = sum(3)(2) 
    val inc2 = sum(1)
    val two = inc2(1)// partial application
    println(s"five: $five, two: $two, inc(1): ${inc(1)}")

  /** Scala ha the same object-oriented features as Java, and some more. */
  @main def classesAndInterfaces() =
    // Interface: Animal is an interface for things that can make noise
    trait Animal:
      def makeNoise(): String

    // Abstract Class: Mammal is an abstract class implementing Animal
    abstract class Mammal extends Animal

    // Class: Dog is a class extending Mammal (the constructor is after the name)
    class Dog(name: String) extends Mammal:
      override def makeNoise(): String = s"$name says Woof!"
      // handle multiple constructors with auxiliary constructors
      def this() = this("Dog name?") // auxiliary constructor

    val dog = Dog("Buddy") // new is optional
    println(s"dog: ${dog.makeNoise()}")
    println(s"dog with auxiliary constructor: ${Dog().makeNoise()}")
    // Object: Maya is an object extending Dog (object is a singleton class: a class with a single instance)
    object Maya extends Dog("Maya")
    println(s"maya: ${Maya.makeNoise()}")

    // Static Methods: can be defined as methods of homonymous objects
    object Animal:
      def staticMethod(): String = "I am a static method of Animal"
    println(s"staticMethod: ${Animal.staticMethod()}")

  /** Product types are restricted classes, for which you get some interfaces for free (e.g. ToString). */
  @main def productTypes() =
    // Case Class: immutable fields by default! Similar to records in Java.
    case class Person(name: String, age: Int) // restricted class with immutable fields, and more!
    // ToString for free!
    println(s"alice: ${Person("Alice", 25)}")
    // Equals for free! ... and more!
    println(s"alice = bob? ${Person("Alice", 25) == Person("Bob", 30)}")

  /** ADTs are types that can be one of multiple known choices: better than enumerations! */
  @main def abstractDataTypes() =
    // Standard Enumeration (over Values): RGB is either Red, Green, or Blue.
    enum RGB:
      case Red, Green, Blue
    println(s"RGB: ${RGB.Red}, ${RGB.Green}, ${RGB.Blue}")

    // General Enumeration (over Records): MyList is either the empty list or an element followed by a list.
    enum MyList:
      case Empty                          // [] Nil
      case Cons(head: Int, tail: MyList) //x :: xs
    println(s"EmptyList []: ${MyList.Empty}")
    println(s"List [1,2,3]: ${MyList.Cons(1, MyList.Cons(2, MyList.Cons(3, MyList.Empty)))}")
      
    enum MyList2[+A]:
      case Empty                          // [] Nil
      case Cons(head: A, tail: MyList2[A]) //x :: xs
    println(s"EmptyList []: ${MyList2.Empty}")
    println(s"List [1,2,3]: ${MyList2.Cons@@(1, MyList2.Cons(2, MyList2.Cons(3, MyList2.Empty)))}")


  /** One of the most common ADT in the Scala library is Lists! */
  @main def scalaLists() =
    // Empty List of Int: note Nil is the same as MyList.Empty
    val l1 = Nil
    // List of Int with parenthesis: note x :: xs is the same as MyList.Cons(x,xs)
    val l2 = List(1, 2, 3)
    // Cleaner without parenthesis
    val l3 = 1 :: 2 :: 3 :: Nil
    println(s"l1: $l1 \nl2: $l2 \nl3: $l3")
    // Mapping: multiply all elements by 3. Similar to Java Streams. Note: map is higher-order!
    println(s"Mapping l3 with _*3:        ${TODO}")
    // Filtering: all elements greater than 2.
    println(s"Filtering l3 with _>2:      ${TODO}")
    // Folding: the sum of all elements in the list. Same as (0 + (1 + (2 + 3))).
    println(s"Folding l3 from 0 with _+_: ${TODO}")

  /** ADTs enable a powerful control flow mechanism based on destructuring! */
  @main def patternMatching() =
    def isEmpty(l: List[Int]): Boolean = ???

    def size(l: List[Int]): Int = ???

    def sumEveryTwo(l: List[Int]): List[Int] = ???

  /**
   * A no-op function to mark missing code gracefully.
   * Usually, you don't want to mark it gracefully and you would use `???`.
   */
  private def TODO = ()
```


#### Short summary: 

empty definition using pc, found symbol in pc: 