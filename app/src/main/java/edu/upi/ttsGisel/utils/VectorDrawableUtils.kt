package edu.upi.ttsGisel.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

object VectorDrawableUtils {
    fun getDrawable(context: Context, drawableResId: Int): Drawable? {
        return VectorDrawableCompat.create(context.resources, drawableResId, context.theme)
    }

    fun getDrawable(context: Context, drawableResId: Int, colorFilter: Int): Drawable {
        val drawable = getDrawable(context, drawableResId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable!!.colorFilter = BlendModeColorFilter(colorFilter, BlendMode.SRC_IN)
        } else {
            drawable!!.setColorFilter(colorFilter, PorterDuff.Mode.SRC_IN)
        }

        return drawable
    }

    fun getBitmap(context: Context, drawableId: Int): Bitmap {
        val drawable = getDrawable(context, drawableId)

        val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }
}