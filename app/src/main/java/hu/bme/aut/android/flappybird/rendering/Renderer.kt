package hu.bme.aut.android.flappybird.rendering

import android.content.Context
import android.graphics.Canvas
import hu.bme.aut.android.flappybird.GameActivity
import hu.bme.aut.android.flappybird.model.*
import java.util.*

class Renderer(
    private val context: Context,
    private val width: Int,
    private val height: Int
) {
    private val backgroundToDraw = mutableListOf<Background>()
    private val pipesToDraw = mutableListOf<Pipe>()
    private val baseToDraw = mutableListOf<Base>()

    private val bird = Bird(context)
    private val score = Score(context)

    private var isPaused = false
    private var scoreCount= 0
    private var gameState = 0
    private var tick = 1

    private val pipeGap: Int = (height/4)

    init {
        val base1 = Base(context)
        val base2 = Base(context)

        val background1 = Background(context)
        val background2 = Background(context)

        bird.setSize(width, height)
        score.setSize(width, height)

        base1.setSize(0, height)
        base2.setSize(base1.getWidthToScale(), height)

        background1.setSize(0, height)
        background2.setSize(background1.getWidthToScale(), height)

        backgroundToDraw.add(background1)
        backgroundToDraw.add(background2)
        baseToDraw.add(base1)
        baseToDraw.add(base2)
    }

    fun startGame(){
        resetGame()
        isPaused = false
        gameState = 1
    }

    fun gameOver(){
        bird.kill()
        gameState = 2
        (context as GameActivity).switchView(gameState)
    }

    fun toMenu(){
        resetGame()
        isPaused = false
        gameState = 0
        (context as GameActivity).switchView(gameState)
    }

    fun pauseGame(){
        isPaused = true
    }

    fun resumeGame(){
        isPaused = false
    }

    fun flyBird(){
        if(gameState == 1)
            bird.fly()
    }

    fun changeBirdColor(){
        bird.changeColor()
    }

    fun step(){
        when(gameState){
            0 -> stepInMenu()
            1 -> stepInGame()
            2 -> stepEndScreen()
        }
    }

    private fun stepInMenu(){
        loopBackground()
        loopBase()
        backgroundToDraw.forEach(Background::step)
        baseToDraw.forEach(Base::step)
        bird.hover(tick)
        tick++
    }

    private fun stepInGame() {
        if(!isPaused) {
            loopBackground()
            loopBase()
            loopPipes()
            backgroundToDraw.forEach(Background::step)
            baseToDraw.forEach(Base::step)
            pipesToDraw.forEach(Pipe::step)
            stepScore()
            bird.step()
            scoreCount++
            tick++
        }
    }

    private fun stepEndScreen(){
        bird.dies()
    }

    fun draw(canvas: Canvas){
        when(gameState){
            0 -> drawMenu(canvas)
            1 -> drawGame(canvas)
            2 -> drawEndScreen(canvas)
        }
    }

    private fun drawMenu(canvas: Canvas) {
        backgroundToDraw.forEach { it. render(canvas) }
        baseToDraw.forEach { it. render(canvas) }
        bird.render(canvas)
    }

    private fun drawGame(canvas: Canvas) {
        backgroundToDraw.forEach { it. render(canvas) }
        val tempPipeList = pipesToDraw.toMutableList()
        tempPipeList.forEach { it.render(canvas) }
        baseToDraw.forEach { it. render(canvas) }
        score.drawScore(0, canvas)
        bird.render(canvas)
    }

    private fun drawEndScreen(canvas: Canvas){
        backgroundToDraw.forEach { it.render(canvas) }
        val tempPipeList = pipesToDraw.toMutableList()
        tempPipeList.forEach { it.render(canvas) }
        baseToDraw.forEach { it.render(canvas) }
        score.drawScore(height/2 - height/5, canvas)
        bird.render(canvas)
    }

    fun checkCollision(): Boolean {
        pipesToDraw.forEach {
            if (!it.isTopTube()) {
                if (bird.getX() + bird.getWidthToScale() > it.getX() && bird.getX() < it.getX() + it.getWidthToScale()) {
                    if (bird.getY() + bird.getHeightToScale() > it.getY()) {
                        return true
                    }
                }
            } else {
                if (bird.getX() + bird.getWidthToScale() > it.getX() && bird.getX() < it.getX() + it.getWidthToScale()) {
                    if (bird.getY() < it.getY() + it.getHeightToScale()) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun getGameState() = gameState
    fun getScore() = score.getScore()
    fun getBird() = bird

    private fun resetGame(){
        pipesToDraw.clear()
        score.resetScore()
        bird.reset()
        scoreCount = 0
    }

    private fun stepScore(){
        if((scoreCount-10) % (Pipe.SPEED *1.5).toInt() == 0){
            score.stepScore()
        }
    }

    private fun loopBackground() {
        val lastBackground = backgroundToDraw[0]
        val rightSide = lastBackground.getX() + lastBackground.getWidthToScale()
        if(rightSide <= 0){
            val background = Background(context)
            background.setSize(rightSide + lastBackground.getWidthToScale(), height)
            backgroundToDraw.add(background)
            backgroundToDraw.removeAt(0)
        }
    }

    private fun loopBase(){
        val lastBase = baseToDraw[0]
        val rightSide = lastBase.getX() + lastBase.getWidthToScale()
        if(rightSide <= 0){
            val newBase = Base(context)
            newBase.setSize(rightSide + newBase.getWidthToScale(), height)
            baseToDraw.add(newBase)
            baseToDraw.removeAt(0)
        }
    }

    private fun loopPipes(){
        if (scoreCount % (Pipe.SPEED*1.5).toInt() == 0) {
            val topPipe = Pipe(context)
            val bottomPipe = Pipe(context)

            val randomPos = Random().nextInt(height/3) + height/5
            val topPipePos: Int = randomPos - pipeGap/2 - topPipe.getHeightToScale()
            val bottomPipePos: Int = randomPos + pipeGap/2
            topPipe.setSize(width, topPipePos, true)
            bottomPipe.setSize(width, bottomPipePos, false)

            pipesToDraw.add(bottomPipe)
            pipesToDraw.add(topPipe)
        }

        val lastPipe = pipesToDraw[0]
        val rightSide = lastPipe.getX() + lastPipe.getWidthToScale()
        if(rightSide <= 0){
            pipesToDraw.removeAt(0)
        }
    }
}