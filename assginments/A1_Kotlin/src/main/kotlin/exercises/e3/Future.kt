package exercises.e3

import labutils.Scenarios
import labutils.robot.RobotBehavior
import labutils.simulator.Simulator
import java.awt.Color
import java.util.concurrent.CompletableFuture

fun main() {
    Scenarios.OneRobotOneLight.runScenario(
        behavior = Future.blinkingSOSAndFollowingTheLight,
        width = 600.0,
        height = 600.0,
    )
}

object Future {
    // Note: `CompletableFuture` is the standard implementation of futures in Java
    fun <T> setTimeout(durationMillis: Long, callback: suspend () -> T): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        Simulator.runAfter(durationMillis){
            val result = callback()
            future.complete(result)
        }
        return future
    }

    // Note: futures can be combined in sequence, flattening the callback hell
    fun blinkingSOS(): RobotBehavior = { robot ->
        setTimeout(100) { robot.led.switch(on = true, color = Color.RED) }
            .thenCompose { setTimeout(100) { robot.led.switch(on = false, color = Color.RED) } }
            .thenCompose { setTimeout(100) { robot.led.switch(on = true, color = Color.RED) } }
            .thenCompose { setTimeout(100) { robot.led.switch(on = false, color = Color.RED) } }
            .thenCompose { setTimeout(100) { robot.led.switch(on = true, color = Color.RED) } }
            .thenCompose { setTimeout(100) { robot.led.switch(on = false, color = Color.RED) } }
            .thenCompose { setTimeout(200) { robot.led.switch(on = true, color = Color.GREEN) } }
            .thenCompose { setTimeout(200) { robot.led.switch(on = false, color = Color.GREEN) } }
            .thenCompose { setTimeout(200) { robot.led.switch(on = true, color = Color.GREEN) } }
            .thenCompose { setTimeout(200) { robot.led.switch(on = false, color = Color.GREEN) } }
            .thenCompose { setTimeout(200) { robot.led.switch(on = true, color = Color.GREEN) } }
            .thenCompose { setTimeout(200) { robot.led.switch(on = false, color = Color.GREEN) } }
            .thenCompose { setTimeout(100) { robot.led.switch(on = true, color = Color.RED) } }
            .thenCompose { setTimeout(100) { robot.led.switch(on = false, color = Color.RED) } }
            .thenCompose { setTimeout(100) { robot.led.switch(on = true, color = Color.RED) } }
            .thenCompose { setTimeout(100) { robot.led.switch(on = false, color = Color.RED) } }
            .thenCompose { setTimeout(100) { robot.led.switch(on = true, color = Color.RED) } }
            .thenCompose { setTimeout(100) { robot.led.switch(on = false, color = Color.RED) } }
            .thenCompose { setTimeout(1000) { blinkingSOS()(robot) } }
    }

    val blinkingSOSAndFollowingTheLight: RobotBehavior = { robot ->
        val blinking = blinkingSOS()(robot)
        val following = AsyncAwait.async { AsyncAwait.followingTheLight()(robot) }
    }
}