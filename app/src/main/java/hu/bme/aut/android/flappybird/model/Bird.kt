package hu.bme.aut.android.flappybird.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import hu.bme.aut.android.flappybird.R

class Bird(context: Context) : Object() {
    companion object {
        const val SPRITE_HORIZONTAL = 3
        const val SPRITE_VERTICAL = 4
        const val GRAVITY = 4
        const val THRUST = 50
    }

    private var velocity : Int = -THRUST
    private var color: Int = 0
    private var wing: Int = 0

    override val image: Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.birds)

    init {
        posX = screenWidth
        posY = screenHeight
        scaledWidth = (image.width / SPRITE_HORIZONTAL) * defaultScale
        scaledHeight = (image.height / SPRITE_VERTICAL) * defaultScale
    }

    override fun setSize(x: Int, y:Int){
        super.setSize(x, y)
        posX = x/2 - scaledWidth/2
        posY = y/3
    }

    fun reset(){
        posX = screenWidth/2 - scaledWidth/2
        posY = screenHeight/3
        velocity = -20
    }

    fun changeColor(){
        if(color < 2)
            color ++
        else
            color = 0
    }

    fun fly(){
        velocity -= THRUST
    }

    fun hover(tick: Int){
        val movement: Int = screenHeight/280
        when (tick % 18) {
            0 -> posY += movement
            1 -> posY += movement
            2 -> posY += movement
            3 -> posY += movement
            4 -> posY += 0
            5 -> posY -= movement
            6 -> posY -= movement
            7 -> posY -= movement
            8 -> posY -= movement
            9 -> posY -= movement
            10 -> posY -= movement
            11 -> posY -= movement
            12 -> posY -= movement
            13 -> posY -= 0
            14 -> posY += movement
            15 -> posY += movement
            16 -> posY += movement
            else -> posY += movement
        }
        wing++
    }

    fun dies(){
        if(posY + velocity < screenHeight*4/5) {
            velocity += GRAVITY
            posY += velocity
        } else {
            velocity = -velocity/8
            posY = screenHeight*4/5
        }
    }

    fun kill(){
        velocity = 0
    }

    override fun step() {
        if(posY + velocity < screenHeight*4/5) {
            velocity += GRAVITY
        } else {
            velocity = -velocity/8
            posY = screenHeight*4/5
        }

        if(posY + velocity > 0) {
            posY += velocity
        } else {
            velocity = -velocity/8
            posY = 0
        }
        wing++
    }

    override fun setSpriteSizes() {
        spriteWidth = image.width / SPRITE_HORIZONTAL
        spriteHeight = image.height / SPRITE_VERTICAL
    }

    override fun render(canvas: Canvas) {
        setSpriteSizes()

        val wingPos = wing % 4

        val x = color * spriteWidth
        val y = wingPos * spriteHeight

        val src = Rect(x, y, x + spriteWidth, y + spriteHeight)
        val dst = Rect(posX, posY, posX + spriteWidth * defaultScale, posY + spriteHeight * defaultScale)

        canvas.drawBitmap(image, src, dst, null)
    }
}