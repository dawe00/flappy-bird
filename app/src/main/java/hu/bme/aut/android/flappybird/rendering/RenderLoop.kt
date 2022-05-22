package hu.bme.aut.android.flappybird.rendering

import android.content.Context
import android.graphics.Canvas

class RenderLoop(
    context: Context,
    private val view: GameView,
    width: Int,
    height: Int
) : Thread() {
    companion object {
        private const val FPS: Long = 30
        private const val TIME_BETWEEN_FRAMES = 1000 / FPS
    }
    var running = false

    private val renderer = Renderer(context, width, height)

    private fun getTime() = System.currentTimeMillis()

    private fun sleepThread(time: Long) {
        try {
            sleep(time)
        } catch (e: InterruptedException) {}
    }

    override fun run() {
        while (running) {
            val renderStart = getTime()
            draw()
            val renderEnd = getTime()
            val sleepTime = TIME_BETWEEN_FRAMES - (renderEnd - renderStart)
            if (sleepTime > 0) {
                sleepThread(sleepTime)
            } else {
                sleepThread(5)
            }
        }
    }

    private fun draw() {
        renderer.step()

        if(renderer.checkCollision() && renderer.getGameState() == 1){
            renderer.gameOver()
        }

        var canvas: Canvas? = null

        try {
            canvas = view.holder.lockCanvas()
            synchronized(view.holder) {
                if (canvas != null)
                    renderer.draw(canvas)
            }
        } finally {
            if (canvas != null) {
                view.holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    fun startGame(){ renderer.startGame() }
    fun toMenu(){ renderer.toMenu() }
    fun pauseGame(){ renderer.pauseGame() }
    fun resumeGame(){ renderer.resumeGame() }

    fun getScore() = renderer.getScore()
    fun getGamestate() = renderer.getGameState()
    fun getBird() = renderer.getBird()

    fun flyBird(){ renderer.flyBird() }
    fun changeColor(){ renderer.changeBirdColor() }
}