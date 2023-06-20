package com.example.picedit
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
/*
phone pixel co-ordinate 0,0 upper left
opengl frame middle of screen -1,1
texture co-ordinate 0,1 upper left

 */

class MyGLRenderer(val context: Context) : GLSurfaceView.Renderer {

    private var programId: Int = 0
    private var textureId: Int = 0

    private var vertexBuffer: FloatBuffer? = null
    private var touchX = -1.0f
    private var touchY = -1.0f
    private var screenWidth = 0
    private var screenHeight = 0

    private val vertexData = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    )
    fun setTouchPosition(x: Float, y: Float) {
        touchX = (x / screenWidth)
        touchY = (y / screenHeight)
    }
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        val options = BitmapFactory.Options()
        options.inScaled = false
        val initialBitmap = BitmapFactory.decodeResource(
            context.resources,
            R.drawable.sloth, // Replace with your initial texture resource
            options
        )
        textureId = loadTexture(initialBitmap)
        initialBitmap.recycle()
        glClearColor(1.0f, 1.0f, 0.0f, 1.0f)
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
        programId = createProgram(vertexShader, fragmentShader)
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexBuffer?.put(vertexData)
        vertexBuffer?.position(0)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(unused: GL10) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glUseProgram(programId)
        val touchPositionHandle = glGetUniformLocation(programId, "uTouchPosition")
        glUniform2f(touchPositionHandle, touchX, touchY)
        val positionHandle = glGetAttribLocation(programId, "aPosition")
        glEnableVertexAttribArray(positionHandle)
        glVertexAttribPointer(positionHandle, 2, GL_FLOAT, false, 0, vertexBuffer)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
        val bitmap = getCurrentFrameBitmapFromGL()
        textureId = loadTexture(bitmap!!)
        if (bitmap != null) {
            bitmap.recycle()
        }
        glDisableVertexAttribArray(positionHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = glCreateShader(type)
        glShaderSource(shader, shaderCode)
        glCompileShader(shader)
        return shader
    }

    private fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
        val program = glCreateProgram()
        glAttachShader(program, vertexShader)
        glAttachShader(program, fragmentShader)
        glLinkProgram(program)
        return program
    }

    private fun loadTexture(bitmap: Bitmap): Int {
        val textureHandles = IntArray(1)
        glGenTextures(1, textureHandles, 0)
        if (textureHandles[0] != 0) {
            glBindTexture(GL_TEXTURE_2D, textureHandles[0])
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        }
        return textureHandles[0]
    }

    private fun getCurrentFrameBitmapFromGL(): Bitmap? {
        val screenshotBuffer = ByteBuffer.allocateDirect(screenWidth * screenHeight * 4)
        screenshotBuffer.order(ByteOrder.LITTLE_ENDIAN)

        glReadPixels(
            0, 0, screenWidth, screenHeight,
            GL_RGBA, GL_UNSIGNED_BYTE, screenshotBuffer
        )

        val bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        screenshotBuffer.rewind()
        bitmap.copyPixelsFromBuffer(screenshotBuffer)
        val matrix = android.graphics.Matrix()
        matrix.postScale(1f, -1f)
        val flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight, matrix, true)

        return flippedBitmap
    }

    companion object {
        private const val vertexShaderCode = """
            attribute vec2 aPosition; // openGL coordinate [-1,1]
            varying vec2 vTextureCoord; 
            
            void main() {
                gl_Position = vec4(aPosition.x,-aPosition.y, 0.0, 1.0);
                vTextureCoord = (aPosition + 1.0) * 0.5; 
            } 
        """
        private const val fragmentShaderCode = """
            precision mediump float; 
            varying vec2 vTextureCoord; 
            uniform vec2 uTouchPosition; 
            uniform sampler2D uTexture; 
            
            void main() {
                vec4 color = texture2D(uTexture, vTextureCoord);
                float x1 = vTextureCoord.x,y1 = vTextureCoord.y; /// this is causing  the error
                float x0 = uTouchPosition.x,y0 = uTouchPosition.y; 
                float D = (x0-x1)*(x0-x1)+(y0-y1)*(y0-y1);
                float lim = (0.05*0.05)/50.0;
                if (D*D < lim && x0>= 0.0) { 
                    discard;
                   // gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0); // Set alpha to 0 for the pixels within the touch area
                } else {
                    gl_FragColor = vec4(color.r, color.g, color.b, color.a);
                }
            }
        """
    }
}