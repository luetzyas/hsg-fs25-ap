object Main extends App {
  println("Hello, I was callled from an object extending App.")
}

@main def hello(): Unit = {
  println(s"Hello, $msg")
}

def msg = "I was called from a @main function."