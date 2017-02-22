package com.facepp.library.util;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.content.ContentValues.TAG;

/**
 * Created by root on 17-2-22.
 */

public class TextureMatrix {

    protected static final String VERTEX_SHADER = "" +
            "uniform mat4 uMVPMatrix;\n" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            " \n" +
            "varying vec2 textureCoordinate;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = position * uMVPMatrix;\n" +
            "    textureCoordinate = inputTextureCoordinate.xy;\n" +
            "}";
    protected static final String FRAGMENT_SHADER = "" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    private FloatBuffer vertexBuffer, textureVerticesBuffer;
    private ShortBuffer drawListBuffer;
    private int textureID = 0;

    private static final short drawOrder[] = { 0, 1, 2, 0, 2, 3 };
    private static final int COORDS_PER_VERTEX = 3;
    private final int texVertextStride = 2 * 4;
    private final int vertexStride = COORDS_PER_VERTEX * 4;

    private static final float COORD1[] = {
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };
    private static final float textureVertices[] = {
            1.0f, 1.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f, };

    private int programId;
    private int positionHandle;
    private int mvpMatrixHandle;
    private int textureHandle;
    private int texCoordHandle;

    public TextureMatrix(int textureID) {
        this.textureID = textureID;

        drawListBuffer = OpenGLHelper.getShortBuffer(drawOrder);
        textureVerticesBuffer = OpenGLHelper.getFloatBuffer(textureVertices);
        programId = OpenGLHelper.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        positionHandle = GLES20.glGetAttribLocation(programId, "position");
        mvpMatrixHandle = GLES20.glGetUniformLocation(programId, "uMVPMatrix");
        textureHandle = GLES20.glGetUniformLocation(programId, "inputImageTexture");
        texCoordHandle = GLES20.glGetAttribLocation(programId, "inputTextureCoordinate");
    }

    public void draw(float[] mvpMatrix) {

        if (vertexBuffer == null) {
            Log.d(TAG, "draw: vertexBuffer == null");
            return;
        }

        GLES20.glUseProgram(programId);
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        vertexBuffer.position(0);

        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false, texVertextStride, textureVerticesBuffer);
        textureVerticesBuffer.position(0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
        GLES20.glUniform1i(textureHandle, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
    }

    public void setSquareCoords(FloatBuffer vertexBuffer) {
//        this.vertexBuffer = ByteBuffer.allocateDirect(COORD1.length * 4)
//                .order(ByteOrder.nativeOrder())
//                .asFloatBuffer();
//        this.vertexBuffer.put(COORD1).position(0);
        this.vertexBuffer  = vertexBuffer;
    }
}
