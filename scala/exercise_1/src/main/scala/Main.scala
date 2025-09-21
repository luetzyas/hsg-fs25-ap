@main def hello(): Unit =
  average(List(1, 2, 3, 4, 5))
  //isPalindrome("Racecar")
  //menu()

//[Ex 1](): Write a program that receives a list of numbers as input, computes the average of the numbers and prints it on the screen
def average(numbers: List[Double]): Double =
  if numbers.isEmpty then 0.0
  else numbers.sum / numbers.length

//[Ex 2](): Write a program that checks if a string is a palindrome, i.e., it is the same if you read it left to right and right to left.
def isPalindrome(s: String): Boolean =
  val cleaned = s.filter(_.isLetterOrDigit).toLowerCase
  cleaned == cleaned.reverse

//[Ex 3](): Write a program that displays a textual menu to the user: </br>
//1 – Sum numbers </br>
//2 – Multiply numbers </br>
//3 – Exit
def menu(): Unit =
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
        println(s"Sum: ${numbers.sum}")
      case 2 =>
        print("Enter numbers to multiply (space-separated): ")
        val numbers = scala.io.StdIn.readLine().split(" ").map(_.toDouble).toList
        println(s"Product: ${numbers.product}")
      case 3 => println("Exiting...")
      case _ => println("Invalid choice, please try again.")