package com.example.picedit

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Helper {
    /*

    package com.example.picedit   hafiz, ami, rakib(ami dilam) rudra,asim,web er sob



    import android.content.Context
    import android.graphics.BitmapFactory
    import android.graphics.PointF
    import android.opengl.GLES20
    import android.opengl.GLES20.*
    import android.opengl.GLES30.GL_UNIFORM_BUFFER
    import android.opengl.GLSurfaceView
    import android.opengl.GLUtils
    import java.nio.ByteBuffer
    import java.nio.ByteOrder
    import java.nio.FloatBuffer
    import javax.microedition.khronos.egl.EGLConfig
    import javax.microedition.khronos.opengles.GL10

    class MyGLRenderer(val context: Context) : GLSurfaceView.Renderer {

        private var programId: Int = 0
        val mPoints = ArrayList<Float>()
        private var textureId: Int = 0
        private val lockPoints = Any()
        private var screenHeight = 0
        private var screenWidth = 0
        private var shaderPrograms = 0
        private var shaderPositionHandle = 0
        private var shaderColorHandle = 0
        private var rednessLevelLocation = 0
        private var pixelLocationsLocation = 0
        private var pixelLocationsBufferId = 0

        private val touchColor = floatArrayOf(0.0f,0.0f,0.0f,0.0f)


        private var vertexBuffer: FloatBuffer? = null

        private val vertexData = floatArrayOf(
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f, 1.0f,
            1.0f, 1.0f
        )

        private val vertexShaderCode2 = """
        attribute vec4 vPosition;
        void main(){
            gl_Position = vPosition;
            gl_PointSize = 20.0;
        }
    """.trimIndent()
        private val FragmentShaderCode2 = """
        precision mediump float;

        varying vec2 vTextureCoord;
        uniform sampler2D uTexture;

        uniform vec4 vColor;
        void main(){
           // gl_FragColor = vColor;
           gl_FragColor = vec4(vColor.rgb, 0.5);
        }
    """.trimIndent()

        fun addPoints(x: Float, y: Float){
            synchronized(lockPoints){
                // Log.d("look here ","x is $x y is $y")
                val normalizedX = (x / screenWidth) * 2 - 1
                val normalizedY = 1 - (y / screenHeight) * 2
                mPoints.add(normalizedX)
                mPoints.add(normalizedY)
            }
        }
        override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
            GLES20.glClearColor(0.5f, 0.8f, 9.0f, 1.0f)
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)
            programId = createProgram(vertexShader, fragmentShader)
            textureId = loadTexture()
            vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
            vertexBuffer?.put(vertexData)
            vertexBuffer?.position(0)

            ///
            // dealProgram()
        }
//    private fun dealProgram() {
//        val vertexShader = loadShader(GL_VERTEX_SHADER,vertexShaderCode2)
//        val fragmentShader = loadShader(GL_FRAGMENT_SHADER,FragmentShaderCode2)
//        shaderPrograms = glCreateProgram()
//        glAttachShader(shaderPrograms, vertexShader)
//        glAttachShader(shaderPrograms, fragmentShader)
//        glLinkProgram(shaderPrograms)
//        shaderPositionHandle = glGetAttribLocation(shaderPrograms, "vPosition")
//        shaderColorHandle = glGetUniformLocation(shaderPrograms, "vColor")
//    }

        override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
            screenHeight = height
            screenWidth = width
            GLES20.glViewport(0, 0, width, height)
        }

        override fun onDrawFrame(unused: GL10) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            GLES20.glUseProgram(programId)
            val pixelLocations = arrayListOf(
                PointF(0.2f, 0.3f), // Example pixel locations, replace with your actual list
                PointF(0.4f, 0.6f),
                PointF(0.8f, 0.9f)
            )
            val pixelLocationsArray = FloatArray(pixelLocations.size * 2)
            for (i in pixelLocations.indices) {
                val pixelLocation = pixelLocations[i]
                pixelLocationsArray[i * 2] = pixelLocation.x
                pixelLocationsArray[i * 2 + 1] = pixelLocation.y
            }

            // Update the uniform buffer object with the new data
            pixelLocationsBufferId = GLES20.glGetUniformLocation(programId, "uPixelLocationBuffer")
            GLES20.glBindBuffer(GLES30.GL_UNIFORM_BUFFER, pixelLocationsBufferId)
            GLES20.glBufferSubData(
                GLES30.GL_UNIFORM_BUFFER,
                0,
                pixelLocationsArray.size * 4,
                FloatBuffer.wrap(pixelLocationsArray)
            )
            GLES20.glBindBuffer(GLES30.GL_UNIFORM_BUFFER, 0)
            val positionHandle = GLES20.glGetAttribLocation(programId, "aPosition")
            GLES20.glEnableVertexAttribArray(positionHandle)
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
            GLES20.glDisableVertexAttribArray(positionHandle)


            ///
            // drawPoints()
        }
//    private fun drawPoints() {
//        glUseProgram(shaderPrograms)
//        val points: ArrayList<Float> = synchronized(lockPoints){
//            ArrayList(mPoints)
//        }
//        val vertexBuffer: FloatBuffer = ByteBuffer.allocateDirect(points.size * 4).run {
//            order(ByteOrder.nativeOrder())
//            asFloatBuffer().apply {
//                put(points.toFloatArray())
//                position(0)
//            }
//        }
//        glVertexAttribPointer(
//            shaderPositionHandle,
//            2,
//            GL_FLOAT,
//            false,
//            2 * 4,
//            vertexBuffer
//
//        )
//        glEnableVertexAttribArray(shaderPositionHandle)
//
//        glUniform4fv(shaderColorHandle, 1, touchColor, 0)
//        glDrawArrays(GL_POINTS, 0, mPoints.size / 2)
//        glDisableVertexAttribArray(shaderPositionHandle)
//
//    }

        private fun loadShader(type: Int, shaderCode: String): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }

        private fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
            val program = GLES20.glCreateProgram()
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)
            return program
        }

        private fun loadTexture(): Int {
            val textureHandles = IntArray(1)
            GLES20.glGenTextures(1, textureHandles, 0)
            if (textureHandles[0] != 0) {
                val options = BitmapFactory.Options()
                options.inScaled = false // Ensure no pre-scaling

                // Load your texture image here using BitmapFactory
                val bitmap = BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.sloth,
                    options
                )

                // Bind the texture
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandles[0])

                // Set texture parameters
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR
                )
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
                bitmap.recycle()
            }
            return textureHandles[0]
        }

        companion object {
            private const val vertexShaderCode = """
            attribute vec2 aPosition; // openGL coordinate [-1,1]
            varying vec2 vTextureCoord;

            void main() {
                gl_Position = vec4(aPosition.x,-aPosition.y, 0.0, 1.0);
                vTextureCoord = (aPosition + 1.0) * 0.5; // normalized texture coordinate [0,1], all these to
                //convert [-1,1] coordinate to [0,1] coordinate
            }
        """
            private const val fragmentShaderCode = """
            precision mediump float;

            varying vec2 vTextureCoord;
            uniform sampler2D uTexture;
            uniform PixelLocation {
                vec2 pixelLocations[MAX_SIZE];
                int size;
            } uPixelLocationBuffer;

            void main() {
                vec4 color = texture2D(uTexture, vTextureCoord);
                float gray = (color.r + color.g + color.b) / 3.0;
                bool isPixelMatched = false;
                for (int i = 0; i < uPixelLocationBuffer.size; i++) {
                    if (vTextureCoord == uPixelLocationBuffer.pixelLocations[i]) {
                        isPixelMatched = true;
                        break;
                    }
                }
                // Apply redness only to the matched pixels
                if (isPixelMatched) {
                    gl_FragColor = vec4(0.0f + 0.0f, 0.0f, color.a);
                } else {
                    gl_FragColor = vec4(gray, gray, gray, color.a);
                }
            }

        """
        }
    }
/*
                bool isPixelMatched = false;
                for (int i = 0; i < uPixelLocationBuffer.size; i++) {
                    if (vTextureCoord == uPixelLocationBuffer.pixelLocations[i]) {
                        isPixelMatched = true;
                        break;
                    }
                }
                // Apply redness only to the matched pixels
                if (isPixelMatched) {
                    gl_FragColor = vec4(color.r + rednessLevel, color.g, color.b, color.a);
                } else {
                    gl_FragColor = color;
                }
                */
    */
}