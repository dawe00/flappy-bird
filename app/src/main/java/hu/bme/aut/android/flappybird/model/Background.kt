package hu.bme.aut.android.flappybird.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import hu.bme.aut.android.flappybird.R
import java.util.*

class Background(context: Context) : Object() {
    companion object {
        const val SPRITE_HORIZONTAL = 1
        const val SPRITE_VERTICAL = 1
        const val SPEED = 10
    }

    override var image: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.background_night)

    init {
        posX = 0
        posY = screenHeight
        scaledWidth = (image.width / Base.SPRITE_HORIZONTAL) * defaultScale
        scaledHeight = (image.height / Base.SPRITE_VERTICAL) * defaultScale

        val rightNow: Calendar = Calendar.getInstance()
        val currentHour: Int = rightNow.get(Calendar.HOUR_OF_DAY)

        if(currentHour in 7..19) {
            image = BitmapFactory.decodeResource(context.resources, R.drawable.background_day)
        }
    }

    override fun setSize(x: Int, y:Int){
        super.setSize(x, y)
        posX = x
        posY = 0
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