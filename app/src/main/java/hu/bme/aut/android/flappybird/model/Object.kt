package hu.bme.aut.android.flappybird.model

import android.graphics.Bitmap
import android.graphics.Canvas

abstract class Object : Renderable {
    protected var spriteHeight: Int = 0
    protected var spriteWidth: Int = 0
    protected var screenWidth = 0
    protected var screenHeight = 0
    protected var defaultScale = 4

    protected abstract val image: Bitmap
    protected var scaledHeight: Int = 0
    protected var scaledWidth: Int = 0

    protected var posX: Int = 0
    protected var posY: Int = 0

    override fun step() {}

    override fun setSize(x: Int, y: Int) {
        this.screenWidth = x
        this.screenHeight = y
    }

    override fun render(canvas: Canvas) {}

    protected abstract fun setSpriteSizes()

    fun getX(): Int { return posX }
    fun getY(): Int { return posY }
    fun getHeightToScale(): Int { return scaledHeight }
    fun getWidthToScale(): Int { return scaledWidth }
}