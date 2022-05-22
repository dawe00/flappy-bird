package hu.bme.aut.android.flappybird.model

import android.graphics.Canvas

interface Renderable {
    fun step()
    fun setSize(x: Int, y: Int)
    fun render(canvas: Canvas)
}