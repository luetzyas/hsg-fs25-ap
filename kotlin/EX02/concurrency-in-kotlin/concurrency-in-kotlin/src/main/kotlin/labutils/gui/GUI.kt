package labutils.gui

import kotlinx.coroutines.runBlocking
import labutils.environment.Environment
import labutils.environment.Light
import labutils.math.Point
import labutils.robot.Robot
import labutils.simulator.Tickable
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.*
import kotlin.concurrent.scheduleAtFixedRate

class RobotSimulatorGUI(environment: Environment) : JFrame() {
    companion object {
        private val LIGHT_COLORS: List<Color> = listOf(
            Color.YELLOW, Color.ORANGE, Color.RED, Color.GREEN,
            Color.CYAN, Color.BLUE, Color.MAGENTA, Color.PINK,
        )
    }
    private val canvas = RobotCanvas(environment)
    private val ticks = Observable<Tickable.Tick>()
    private val timer: java.util.Timer = java.util.Timer()
    private var timerTask: java.util.TimerTask? = null

    init {
        title = "Robot Simulator"
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()

        val controlPanel = JPanel()
        controlPanel.layout = BoxLayout(controlPanel, BoxLayout.Y_AXIS)

        val labelPanel = JPanel()
        labelPanel.layout = FlowLayout()
        val simulationLabel = JLabel("Mode: Manual")
        val tickLabel = JLabel("Ticks: 0")
        val periodLabel = JLabel("Period (ms):")
        val periodSlider = JSlider(1, 1000, 16)
        val periodValueLabel = JLabel(periodSlider.value.toString())

        labelPanel.add(simulationLabel)
        labelPanel.add(Box.createRigidArea(Dimension(10, 0)))
        labelPanel.add(tickLabel)
        labelPanel.add(Box.createRigidArea(Dimension(10, 0)))
        labelPanel.add(periodLabel)
        labelPanel.add(periodSlider)
        labelPanel.add(periodValueLabel)

        val buttonPanel = JPanel()
        buttonPanel.layout = FlowLayout()
        val stepButton = JButton("Step")
        val startButton = JButton("Start")
        val stopButton = JButton("Stop")
        stopButton.isEnabled = false

        buttonPanel.add(stepButton)
        buttonPanel.add(startButton)
        buttonPanel.add(stopButton)

        controlPanel.add(labelPanel)
        controlPanel.add(buttonPanel)

        add(canvas, BorderLayout.CENTER)
        add(controlPanel, BorderLayout.SOUTH)

        var tickCount = 0
        this.ticks.observe {
            tickCount += 1
            tickLabel.text = "Ticks: $tickCount"
        }

        periodSlider.addChangeListener {
            periodValueLabel.text = periodSlider.value.toString()
        }
        stepButton.addActionListener {
            this.stepSimulation(periodSlider.value)
        }
        startButton.addActionListener {
            periodSlider.isEnabled = false
            stepButton.isEnabled = false
            startButton.isEnabled = false
            stopButton.isEnabled = true
            simulationLabel.text = "Mode: Auto"
            this.startSimulation(periodSlider.value)
        }
        stopButton.addActionListener {
            periodSlider.isEnabled = true
            stepButton.isEnabled = true
            startButton.isEnabled = true
            stopButton.isEnabled = false
            simulationLabel.text = "Mode: Manual"
            this.stopSimulation()
        }
        this.canvas.addMouseListener(onMouseClick { e ->
            val clickedLight = environment.lights().find { it.position.distance(Point(e.x.toDouble(), e.y.toDouble())) < it.radius }
            if (clickedLight == null) {
                environment.withLights(Light(Point(e.x.toDouble(), e.y.toDouble()), color = LIGHT_COLORS[0]))
            } else {
                environment.withoutLights(clickedLight)
                LIGHT_COLORS.getOrNull(LIGHT_COLORS.indexOf(clickedLight.color) + 1)?.let {
                    environment.withLights(Light(clickedLight.position, clickedLight.radius, it))
                }
            }
            SwingUtilities.invokeLater { repaint() }
        })

        this.pack()
        this.setLocationRelativeTo(null)
        this.isVisible = true
        this.isResizable = false
    }

    fun ticks(): Observable<Tickable.Tick> {
        return this.ticks
    }

    private fun startSimulation(periodMs: Int) {
        if (this.timerTask == null) {
            this.timerTask = this.timer.scheduleAtFixedRate(0L, periodMs.toLong()){ stepSimulation(periodMs) }
        }
    }

    private fun stepSimulation(periodMs: Int) {
        this.ticks.push(Tickable.Tick(periodMs))
        SwingUtilities.invokeLater { repaint() }
    }

    private fun stopSimulation() {
        this.timerTask?.cancel()
        this.timerTask = null
    }

    private fun onMouseClick(callback: (e: MouseEvent) -> Unit): MouseListener =
        object: MouseListener {
            override fun mouseClicked(e: MouseEvent?) { e?.let(callback) }
            override fun mousePressed(e: MouseEvent?) {}
            override fun mouseReleased(e: MouseEvent?) {}
            override fun mouseEntered(e: MouseEvent?) {}
            override fun mouseExited(e: MouseEvent?) {}
        }
}

class RobotCanvas(private val environment: Environment) : JPanel() {
    init {
        this.preferredSize = Dimension(this.environment.configuration.width.toInt(), this.environment.configuration.height.toInt())
        this.background = Color.WHITE
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g as Graphics2D
        this.environment.lights().map { LightView(it) }.forEach { it.paint(g2d, 10, this.environment.configuration.scale) }
        this.environment.robots().map { RobotView(it) }.forEach { it.paint(g2d, this.environment.configuration.scale) }
    }
}

class RobotView(private val robot: Robot) {
    fun paint(g2d: Graphics2D, scale: Double = 1.0) {
        // Draw body
        ViewUtils.drawCircle(g2d, robot.body.position.x, robot.body.position.y, robot.body.bodyRadius, robot.body.bodyColor, Color.BLACK, scale)
        // Draw front
        val frontXY = this.coordinatesFromRadial(3 * robot.body.frontRadius, robot.body.angle, scale)
        ViewUtils.drawCircle(g2d, frontXY.x, frontXY.y, robot.body.frontRadius, robot.body.frontColor, Color.BLACK, scale)
        // Draw LED
        val ledXY = this.coordinatesFromRadial(2 * robot.led.radius, (robot.body.angle + 90) % 360, scale)
        ViewUtils.drawCircle(g2d, ledXY.x, ledXY.y, robot.led.radius, if (robot.led.isLit()) robot.led.color() else Color.BLACK, robot.led.color().darker(), scale)
        // Draw light sensor
        val sensorXY = this.coordinatesFromRadial(2 * robot.lightSensor.radius, (robot.body.angle + 270) % 360, scale)
        val closestLight = robot.lightSensor.lastSensed()
        ViewUtils.drawCircle(g2d, sensorXY.x, sensorXY.y, robot.lightSensor.radius, closestLight?.color ?: Color.WHITE, Color.ORANGE, scale)
    }
    private fun coordinatesFromRadial(radius: Double, angle: Double, scale: Double = 1.0): Point {
        val tx = radius * kotlin.math.cos(Math.toRadians(angle)) * scale
        val ty = radius * kotlin.math.sin(Math.toRadians(angle)) * scale
        return Point(robot.body.position.x + tx, robot.body.position.y - ty)
    }
}

class LightView(private val light: Light) {
    fun paint(g2d: Graphics2D, layers: Int, scale: Double = 1.0) {
        val deltaTransparency = 1.0 / layers
        val deltaRadius = light.radius / layers
        (0 ..< layers).forEach { i ->
            val color = light.color.withTransparency(1.0 - (deltaTransparency * i))
            val radius = light.radius - (deltaRadius * i)
            ViewUtils.drawCircle(g2d, light.position.x, light.position.y, radius, color, color, scale)
        }
    }
    private fun Color.withTransparency(transparency: Double): Color {
        val alpha = ((1 - transparency) * 255).toInt()
        return Color(this.red, this.green, this.blue, alpha)
    }
}

object ViewUtils {
    fun drawCircle(g2d: Graphics2D, x: Double, y: Double, radius: Double, fillColor: Color, strokeColor: Color, scale: Double = 1.0) {
        drawCircle(g2d, x.toInt(), y.toInt(), (radius * scale).toInt(), fillColor, strokeColor)
    }
    private fun drawCircle(g2d: Graphics2D, x: Int, y: Int, radius: Int, fillColor: Color, strokeColor: Color) {
        g2d.color = fillColor
        g2d.fillOval(x - radius, y - radius, 2 * radius, 2 * radius)
        g2d.color = strokeColor
        g2d.stroke = BasicStroke(2.0F)
        g2d.drawOval(x - radius, y - radius, 2 * radius, 2 * radius)
    }
}
