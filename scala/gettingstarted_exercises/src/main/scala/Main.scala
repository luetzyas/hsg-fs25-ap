@main def hello(): Unit =
  //E1
  average(List(1, 2, 3, 4, 5, 10, 200))
  //E2
  isPalindrome("Test")
  isPalindrome("Never odd or even")
  //E3
  menu()

//[Ex 1](): Write a program that receives a list of numbers as input, computes the average of the numbers and prints it on the screen
def average(numbers: List[Int]): Unit =
  println("*** Ecercise 1 ***")
  //attrbute result
  var result = 0.0

  if numbers.nonEmpty then
    println(s"Numbers: ${numbers.mkString(", ")}")
    println(s"Average: ${numbers.sum / numbers.length}")
  else
    println("The list is empty, cannot compute average.")

//[Ex 2](): Write a program that checks if a string is a palindrome, i.e., it is the same if you read it left to right and right to left.
def isPalindrome(s: String): Unit =
  println("\n*** Ecercise 2 ***")
  val cleaned = s.filter(_.isLetterOrDigit).toLowerCase
  cleaned == cleaned.reverse
  if cleaned == cleaned.reverse then
    println(s""""$s" is a palindrome.""")
  else 
    println(s""""$s" is not a palindrome.""")

//[Ex 3](): Write a program that displays a textual menu to the user: </br>
//1 – Sum numbers </br>
//2 – Multiply numbers </br>
//3 – Exit
def menu(): Unit =
  println("\n*** Ecercise 3 ***")

  var choice = 0
  while choice != 3 do
    println("Menu:")
    println("1 - Sum numbers")
    println("2 - Multiply numbers")
    println("3 - Exit")
    print("Enter your choice: ")
    choice = scala.io.StdIn.readInt()
    choice match
      case 1 =>
        print("Enter numbers to sum (space-separated): ")
        val numbers = scala.io.StdIn.readLine().split(" ").map(_.toDouble).toList
        println(s">> Sum: ${numbers.sum}\n")
      case 2 =>
        print("Enter numbers to multiply (space-separated): ")
        val numbers = scala.io.StdIn.readLine().split(" ").map(_.toDouble).toList
        println(s">> Product: ${numbers.product}\n")
      case 3 => println("Exiting...")
      case _ => println("Invalid choice, please try again.")