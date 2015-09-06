/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

import android.graphics.Bitmap;
import android.opengl.GLES30;

import java.nio.ByteBuffer;

import static android.opengl.GLES30.*;


/**
 * @author decamp
  */
public final class Texture2 extends AbstractTexture {


    private ByteBuffer mBuf = null;
    private int mStride = 0;


    public Texture2() {
        super( GL_TEXTURE_2D );
        param( GL_TEXTURE_MIN_FILTER, GL_LINEAR );
    }


    public void buffer( Bitmap image ) {
        if( image == null ) {
            buffer( null, 0, 0, 0, -1, -1, -1 );
        } else {
            int[] format = new int[2];
            ByteBuffer buf = DrawUtil.imageToBuffer( image, null, null, format );
            int w = image.getWidth();
            buffer( buf, format[0], format[1], GL_UNSIGNED_BYTE, w, image.getHeight(), w );
        }
    }


    public synchronized void buffer( ByteBuffer buf,
                                     int intFormat,
                                     int format,
                                     int dataType,
                                     int w,
                                     int h,
                                     int stride )
    {
        if( buf == null ) {
            if( mBuf == null ) {
                return;
            }
            super.format( -1, -1, -1 );
            super.size( -1, -1 );
            mBuf = null;
            mStride = 0;
        } else {
            super.format( intFormat, format, dataType );
            super.size( w, h );
            mBuf = buf.duplicate();
            mStride = stride < 0 ? 0 : stride;
        }

        fireAlloc();
    }

    @Override
    public void dispose( DrawEnv g ) {
        super.dispose( g );
        mBuf = null;
    }

    @Override
    protected synchronized void doAlloc( DrawEnv g ) {
        GLES30.glPixelStorei( GL_UNPACK_ROW_LENGTH, mStride );
        GLES30.glTexImage2D( GL_TEXTURE_2D,
                             0, //level
                             internalFormat(),
                             width(),
                             height(),
                             0, // border
                             format(),
                             dataType(),
                             mBuf );
        GLES30.glPixelStorei( GL_UNPACK_ROW_LENGTH, 0 );
        mBuf = null;
    }

}
