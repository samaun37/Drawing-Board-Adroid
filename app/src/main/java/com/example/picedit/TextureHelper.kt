package com.example.picedit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils

object TextureHelper {
    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureIds = IntArray(1)
        glGenTextures(1, textureIds, 0)

        if (textureIds[0] == 0) {
            throw RuntimeException("Failed to generate texture ID")
        }

        val options = BitmapFactory.Options().apply {
            inScaled = false // Disable scaling of the image
        }
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

        if (bitmap == null) {
            glDeleteTextures(1, textureIds, 0)
            throw RuntimeException("Failed to decode resource: $resourceId")
        }

        glBindTexture(GL_TEXTURE_2D, textureIds[0])
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        glBindTexture(GL_TEXTURE_2D, 0)

        return textureIds[0]
    }
}
