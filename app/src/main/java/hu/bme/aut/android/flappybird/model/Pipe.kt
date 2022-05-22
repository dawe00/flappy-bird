package hu.bme.aut.android.flappybird.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import hu.bme.aut.android.flappybird.R

class Pipe(context: Context) : Object() {
    companion object {
        const val SPRITE_HORIZONTAL = 2
        const val SPRITE_VERTICAL = 1
        var SPEED = 20
    }

    override val image: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pipes)

    private var isTop: Boolean = true
    fun isTopTube(): Boolean{ return isTop }

    init {
        posX = screenWidth
        posY = screenHeight
        scaledWidth = (image.width / SPRITE_HORIZONTAL) * defaultScale
        scaledHeight = (image.height / SPRITE_VERTICAL) * defaultScale
    }

    override fun step() {
        super.step()
        posX -= SPEED
    }

    fun setSize(x: Int, y:Int, isTop: Boolean){
        super.setSize(x, y)
        this.isTop = isTop
        posX = x
        posY = y
    }

    override fun setSpriteSizes() {
        spriteWidth = image.width / SPRITE_HORIZONTAL
        spriteHeight = image.height / SPRITE_VERTICAL
    }

    override fun render(canvas: Canvas) {
        setSpriteSizes()

        val x = if(isTop) { spriteWidth } else { 0 }
        val y = 0

        val src = Rect(x, y, x + spriteWidth, y + spriteHeight)
        val dst = Rect(posX, posY, posX + spriteWidth * defaultScale, posY + spriteHeight * defaultScale)

        canvas.drawBitmap(image, src, dst, null)
    }
}