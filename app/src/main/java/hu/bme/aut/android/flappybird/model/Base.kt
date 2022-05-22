package hu.bme.aut.android.flappybird.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import hu.bme.aut.android.flappybird.R

class Base(context: Context) : Object() {
    companion object {
        const val SPRITE_HORIZONTAL = 1
        const val SPRITE_VERTICAL = 1
        const val SPEED = 20
    }

    override val image: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.base)

    init {
        posX = 0
        posY = screenHeight
        scaledWidth = (image.width / SPRITE_HORIZONTAL) * defaultScale
        scaledHeight = (image.height / SPRITE_VERTICAL) * defaultScale
    }

    override fun setSize(x: Int, y:Int){
        super.setSize(x, y)
        posX = x
        posY = screenHeight*4/5
    }

    override fun step() {
        super.step()
        posX -= SPEED
    }

    override fun setSpriteSizes() {
        spriteWidth = image.width / SPRITE_HORIZONTAL
        spriteHeight = image.height / SPRITE_VERTICAL
    }

    override fun render(canvas: Canvas) {
        setSpriteSizes()

        val src = Rect(0, 0, spriteWidth, spriteHeight)
        val dst = Rect(posX, posY, posX + spriteWidth * defaultScale, posY + spriteHeight * defaultScale)

        canvas.drawBitmap(image, src, dst, null)
    }
}