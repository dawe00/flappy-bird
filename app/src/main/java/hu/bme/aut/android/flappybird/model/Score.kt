package hu.bme.aut.android.flappybird.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import hu.bme.aut.android.flappybird.R
import kotlin.math.log10

class Score(context: Context) : Object() {
    companion object {
        const val SPRITE_HORIZONTAL = 10
        const val SPRITE_VERTICAL = 1
    }

    private var digit: Int = 0
    private var score: Int = -1

    override val image: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.numbers)

    init {
        posX = screenWidth
        posY = screenHeight
        scaledWidth = (image.width / SPRITE_HORIZONTAL) * defaultScale
        scaledHeight = (image.height / SPRITE_VERTICAL) * defaultScale
    }

    override fun setSize(x: Int, y:Int){
        super.setSize(x, y)
        posX = x/2 - scaledWidth/2
        posY = y/6
    }

    override fun step() {}

    override fun setSpriteSizes() {
        spriteWidth = image.width / SPRITE_HORIZONTAL
        spriteHeight = image.height / SPRITE_VERTICAL
    }

    fun getScore(): Int{ return if(score < 0) { 0 } else { score } }
    fun resetScore(){ score = -1 }
    fun stepScore(){ score++ }

    fun drawScore(pos: Int, canvas: Canvas){
        var currentScore: Int = score
        val drawOffset: Int = scaledWidth/2

        posY = if(pos == 0) { screenHeight/6 } else { pos }

        if(score < 1){
            digit = 0
            posX = screenWidth/2 - drawOffset
            render(canvas)
            return
        }

        val length: Int = (log10(score.toDouble()) +1).toInt()
        val digits = IntArray(length)
        for (i in digits.indices) {
            digits[i] = currentScore %10
            currentScore /= 10
        }

        var i: Int = digits.size
        var j: Int = digits.size
        while (i > 0) {
            digit = digits[i-1]
            posX = screenWidth/2 - (j) * drawOffset
            render(canvas)
            i--
            j -= 2
        }
    }

    override fun render(canvas: Canvas) {
        setSpriteSizes()

        val x = digit * spriteWidth
        val y = 0

        val src = Rect(x, y, x + spriteWidth, y + spriteHeight)
        val dst = Rect(posX, posY, posX + spriteWidth * defaultScale, posY + spriteHeight * defaultScale)

        canvas.drawBitmap(image, src, dst, null)
    }
}