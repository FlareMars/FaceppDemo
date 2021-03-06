package com.facepp.library.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by root on 17-2-22.
 */

public class OpenGLHelper {

    public static boolean detectOpenGLES20(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x20000);
    }

    public static int createTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        return texture[0];
    }

    public static int createTexture2DID(Bitmap bitmap) {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);

        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        return texture[0];
    }

    public static int loadProgram(String vSource, String fSource) {
        int iVShader;
        int iFShader;
        int iProgram;
        int[] link = new int[1];
        iVShader = loadShader(vSource, GLES20.GL_VERTEX_SHADER);
        if (iVShader == 0) {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }
        iFShader = loadShader(fSource, GLES20.GL_FRAGMENT_SHADER);
        if (iFShader == 0) {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;
        }
        iProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(iProgram, iVShader);
        checkGlError("");
        GLES20.glAttachShader(iProgram, iFShader);
        checkGlError("");
        GLES20.glLinkProgram(iProgram);
        checkGlError("");
        GLES20.glGetProgramiv(iProgram, GLES20.GL_LINK_STATUS, link, 0);
        if (link[0] <= 0) {
            Log.d("Load Program", "Linking Failed");
            return 0;
        }
        GLES20.glDeleteShader(iVShader);
        checkGlError("");

        GLES20.glDeleteShader(iFShader);
        checkGlError("");

        return iProgram;
    }

    public static int loadShader(String shaderStr, int type) {
        int[] compiled = new int[1];
        int iShader = GLES20.glCreateShader(type);
        checkGlError("");
        GLES20.glShaderSource(iShader, shaderStr);
        checkGlError("");
        GLES20.glCompileShader(iShader);
        checkGlError("");
        GLES20.glGetShaderiv(iShader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        checkGlError("");
        if (compiled[0] == 0) {
            Log.d("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(iShader));
            return 0;
        }
        return iShader;
    }

    public static FloatBuffer getFloatBuffer(float[] arr) {
        ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 4);
        qbb.order(ByteOrder.nativeOrder());
        FloatBuffer mBuffer = qbb.asFloatBuffer();
        mBuffer.put(arr);
        mBuffer.position(0);
        return mBuffer;
    }

    public static ShortBuffer getShortBuffer(short[] arr) {
        ByteBuffer dlb = ByteBuffer.allocateDirect(arr.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer buffer = dlb.asShortBuffer();
        buffer.put(arr);
        buffer.position(0);

        return buffer;
    }

    public IntBuffer getIntBuffer(int[] arr) {
        ByteBuffer qbb = ByteBuffer.allocateDirect(arr.length * 4);
        qbb.order(ByteOrder.nativeOrder());
        IntBuffer mBuffer = qbb.asIntBuffer();
        mBuffer.put(arr);
        mBuffer.position(0);
        return mBuffer;
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("GLES20_ERROR", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}
