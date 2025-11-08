import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun main() {
    val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1)
    fun async(callback: () -> Unit) = executor.execute(callback)

    print("A")
    async {
        print("B")
        async { print("C") }
        async { print("D") }
        print("E")
    }
    print("F")
    executor.awaitTermination(10000, TimeUnit.MILLISECONDS)
}



//y = 0
//future(0).map(x => x * x).map(x / y).map(x => x + 1).observe(success = println, error = println)
//
//fn f(x, s, e) =
//        async: try s(x * x) catch t => e(t)
//
//fn g(x, s, e) =
//        async: try s(x / y) catch t => e(t)
//
//fn h(x, s, e) =
//        async: try s(x + 1) catch t => e(t)
//
//f(0, x => g(x, x => h(x, x => std.println(x), x => err.println(x)), x => err.println(x)), x => std.println(x))


