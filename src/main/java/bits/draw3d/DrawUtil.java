/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLException;

import java.nio.*;

import static android.opengl.GLES30.*;


/**
 * @author Philip DeCamp
 */
public class DrawUtil {

    /**
     * @throws GLException if {@code gl.glGetError() != GL_NO_ERROR}
     */
    public static void checkErr() throws GLException {
        int err = GLES30.glGetError();
        if( err == 0 ) {
            return;
        }
        String msg = String.format( "Err 0x%08X: %s", err, errString( err ) );
        throw new GLException( err, msg );
    }


    public static String errString( int err ) {
        switch( err ) {
        case GL_NO_ERROR:
            return "No error";
        case GL_INVALID_ENUM:
            return "Invalid enum";
        case GL_INVALID_VALUE:
            return "Invalid value";
        case GL_INVALID_OPERATION:
            return "Invalid operation";
//        case GL_STACK_OVERFLOW:
//            return "Stack overflow";
//        case GL_STACK_UNDERFLOW:
//            return "Stack underflow";
        case GL_OUT_OF_MEMORY:
            return "Out of memory";
        default:
            return "Unknown error";
        }
    }


    public static ByteBuffer alloc( int size ) {
        return ByteBuffer.allocateDirect( size ).order( ByteOrder.nativeOrder() );
    }


    public static FloatBuffer allocFloats( int size ) {
        return alloc( size * 4 ).asFloatBuffer();
    }


    public static ByteBuffer ensureCap( ByteBuffer buf, int size ) {
        if( buf == null || buf.capacity() < size ) {
            return alloc( size );
        }
        buf.clear();
        return buf;
    }


    public static FloatBuffer ensureCap( FloatBuffer buf, int size ) {
        if( buf == null || buf.capacity() < size ) {
            return allocFloats( size );
        }
        buf.clear();
        return buf;
    }

    /**
     * Returns OpenGL [internalFormat, format and data type] equivalents for a BufferedImage.
     * (e.g., GL_BGRA and GL_UNSIGNED_BYTE). It will also specify if the ordering of
     * the DataBuffer component values must be reversed to achieve a GL-compatible format.
     *
     * @param image Some image
     * @param out4  Length-4 array to hold output. On return: <br>
     *              out3[0] will hold INTERNAL FORMAT for the texture. <br>
     *              out3[1] will hold PIXEL FORMAT of the image data. <br>
     *              out3[2] will hold DATA TYPE for image.
     *              out3[3] will equal 8 if component values must be left-barrel-shifted by 8 bits.
     * @return true if equivalent format and data type were found
     */
    public static boolean getTextureFormat( Bitmap image, int[] out4 ) {
        switch( image.getConfig() ) {
        case ALPHA_8:
            out4[0] = GL_ALPHA;
            out4[1] = GL_ALPHA;
            out4[2] = GL_UNSIGNED_SHORT;
            out4[3] = 0;
            return true;

        case RGB_565:
            out4[0] = GL_RGB;
            out4[1] = GL_RGB;
            out4[2] = GL_UNSIGNED_SHORT_5_5_5_1;
            out4[3] = 0;
            return true;

        case ARGB_4444:
            out4[0] = GL_RGBA;
            out4[1] = GL_RGBA;
            out4[2] = GL_UNSIGNED_SHORT_4_4_4_4;
            out4[3] = 0;
            return true;

        case ARGB_8888:
            out4[0] = GL_RGBA;
            out4[1] = GL_RGBA;
            out4[2] = GL_UNSIGNED_BYTE;
            out4[3] = 8;
            return true;

        default:
            return false;
        }
    }

    /**
     * Converts a BitMap to a ByteBuffer of the smallest format possible.
     *
     * @param image      Input image to convert.
     * @param optWork    [Optional] array that may be used if {@code workSpace.length >= image.getWidth() }.
     * @param optOut     [Optional][Out] byte buffer to place image into.
     *                   {@code optOut.remaining() >= image.getWidth() * image.getHeight() *  4},
     *                   otherwise a new directly-allocated ByteBuffer will be created with
     *                   native byte order.
     * @param optFormat  [Optional][Out] if not null and {@code length >= 2}, will receive output format: <br>
     *                   optFormat[0] = internal format <br>
     *                   optFormat[1] = format <br>

     * @return ByteBuffer containing unsigned 1-byte samples in RGBA format and sRGB colorub space.

     *
     */
    public static ByteBuffer imageToBuffer( Bitmap image, int[] optWork, ByteBuffer optOut, int[] optFormat ) {
        switch( image.getConfig() ) {
        case ALPHA_8:
            optOut = imageToAlphaBuffer( image, optWork, optOut );
            if( optFormat != null && optFormat.length >= 2 ) {
                optFormat[0] = GL_ALPHA;
                optFormat[1] = GL_RED;
            }
            break;
        case RGB_565:
            optOut = imageToRgbBuffer( image, optWork, optOut );
            if( optFormat != null && optFormat.length >= 2 ) {
                optFormat[0] = GL_RGB;
                optFormat[1] = GL_RGB;
            }
            break;
        case ARGB_4444:
        case ARGB_8888:
        default:
            optOut = imageToRgbaBuffer( image, optWork, optOut );
            if( optFormat != null && optFormat.length >= 2 ) {
                optFormat[0] = GL_RGBA;
                optFormat[1] = GL_RGBA;
            }
        }

        return optOut;
    }

    /**
     * Converts a Bitmap to a ByteBuffer in 32-bit GL_RGBA format.
     *
     * @param image    Input image to convert.
     * @param optWork  [Optional] array that may be used if {@code workSpace.length >= image.getWidth() }.
     * @param optOut   [Optional] byte buffer to place image into.
     *                 {@code optOut.remaining() >= image.getWidth() * image.getHeight() *  4},
     *                 otherwise a new directly-allocated ByteBuffer will be created with
     *                 native byte order.
     * @return ByteBuffer containing unsigned 1-byte samples in RGBA order and sRGB colorub space.
     */
    public static ByteBuffer imageToRgbaBuffer( Bitmap image, int[] optWork, ByteBuffer optOut ) {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] row = optWork != null && optWork.length >= w ? optWork : new int[w];
        ByteOrder order = ByteOrder.nativeOrder();

        if( optOut != null && optOut.remaining() >= 4 * w * h ) {
            optOut = ByteBuffer.allocateDirect( 4 * w * h );
        } else {
            order = optOut.order();
        }

        optOut.order( ByteOrder.BIG_ENDIAN );

        for( int i = 0; i < h; i++ ) {
            image.getPixels( row, 0,  w, 0, i, w, 1 );
            for( int x = 0; x < w; x++ ) {
                int v = row[x];
                optOut.putInt( v << 8 | v >>> 24 );
            }
        }

        optOut.order( order );
        return optOut;
    }

    /**
     * Converts a Bitmap to a ByteBuffer in 24-bit GL_RGB format.
     *
     * @param image    Input image to convert.
     * @param optWork  [Optional] array that may be used if {@code workSpace.length >= image.getWidth() }.
     * @param optOut   [Optional] byte buffer to place image into.
     *                 {@code optOut.remaining() >= image.getWidth() * image.getHeight() *  4},
     *                 otherwise a new directly-allocated ByteBuffer will be created with
     *                 native byte order.
     * @return ByteBuffer containing unsigned 1-byte samples in RGB order and sRGB colorub space.
     */
    public static ByteBuffer imageToRgbBuffer( Bitmap image, int[] optWork, ByteBuffer optOut ) {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] row = optWork != null && optWork.length >= w ? optWork : new int[w];

        if( optOut != null && optOut.remaining() >= 3 * w * h ) {
            optOut = ByteBuffer.allocateDirect( 3 * w * h ).order( ByteOrder.nativeOrder() );
        }

        for( int i = 0; i < h; i++ ) {
            image.getPixels( row, 0,  w, 0, i, w, 1 );
            for( int x = 0; x < w; x++ ) {
                int v = row[x];
                optOut.put( (byte)( v >> 16 ) );
                optOut.put( (byte)( v >>  8 ) );
                optOut.put( (byte)(v) );
            }
        }

        return optOut;
    }

    /**
     * Converts a BufferedImage and places alpha layer into a ByteBuffer in 8-bit GL_ALPHA format.
     *
     * @param image    Input image to convert.
     * @param optWork  [Optional] array that may be used if {@code workSpace.length >= image.getWidth() }.
     * @param optOut   [Optional] byte buffer to place image into.
     *                 {@code optOut.remaining() >= image.getWidth() * image.getHeight()},
     *                 otherwise a new directly-allocated ByteBuffer will be created with
     *                 native byte order.
     * @return ByteBuffer containing unsigned 1-byte samples in sRGB colorub space.
     */
    public static ByteBuffer imageToAlphaBuffer( Bitmap image, int[] optWork, ByteBuffer optOut ) {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] row = optWork != null && optWork.length >= w ? optWork : new int[w];

        if( optOut != null && optOut.remaining() >= w * h ) {
            optOut = ByteBuffer.allocateDirect( w * h ).order( ByteOrder.nativeOrder() );
        }

        for( int i = 0; i < h; i++ ) {
            image.getPixels( row, 0,  w, 0, i, w, 1 );
            for( int x = 0; x < w; x++ ) {
                int v = row[x];
                optOut.put( (byte)v );
            }
        }

        return optOut;
    }

}
