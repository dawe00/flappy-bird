package hu.bme.aut.android.flappybird.rendering

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView : SurfaceView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var renderLoop: RenderLoop? = null
    private var gameRunning: Boolean = false

    init {
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {

            }
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                var retry = true
                renderLoop?.running = false
                while (retry) {
                    try {
                        renderLoop?.join()
                        retry = false
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                val loop = RenderLoop(
                    context,
                    this@GameView,
                    width,
                    height
                )
                loop.running = true
                loop.start()

                renderLoop = loop
            }
        })
    }

    fun startGame(){
        gameRunning = true
        renderLoop?.startGame()
    }

    fun toMenu(){
        gameRunning = false
        renderLoop?.toMenu()
    }

    fun pause(){
        renderLoop?.pauseGame()
    }

    fun resume(){
        renderLoop?.resumeGame()
    }

    fun getScore() = renderLoop?.getScore()
    private fun getBird() = renderLoop?.getBird()
    fun getGamestate() = renderLoop?.getGamestate()

    @Override
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (gameRunning) {
                renderLoop?.flyBird()
            } else if (clickOnBird(event)) {
                renderLoop?.changeColor()
            }
        }
        return true
    }

    private fun clickOnBird(event: MotionEvent): Boolean{
        val bird = getBird()!!
        if (event.x > bird.getX() && event.x < bird.getX() + bird.getWidthToScale()) {
            if (event.y > bird.getY() && event.y < bird.getY() + bird.getHeightToScale()) {
                return true
            }
        }
        return false
    }
}