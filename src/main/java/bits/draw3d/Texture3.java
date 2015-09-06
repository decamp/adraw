/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

import android.opengl.GLES30;

import java.nio.ByteBuffer;

import static android.opengl.GLES30.*;


/**
 * @author decamp
 */
public final class Texture3 extends AbstractTexture {


    private ByteBuffer mBuf = null;
    private int mStride;


    public Texture3() {
        super( GL_TEXTURE_3D );
    }


    public synchronized void buffer( ByteBuffer buf,
                                     int intFormat,
                                     int format,
                                     int dataType,
                                     int w,
                                     int h,
                                     int depth,
                                     int stride )
    {
        if( buf == null ) {
            if( mBuf == null ) {
                return;
            }
            super.format( -1, -1, -1 );
            super.size( -1, -1 );
            super.depth( -1 );
            mBuf = null;
        } else {
            super.format( intFormat, format, dataType );
            super.size( w, h );
            super.depth( depth );
            mBuf = buf.duplicate();
        }

        fireAlloc();
    }

    @Override
    protected void doAlloc( DrawEnv g ) {
        GLES30.glPixelStorei( GL_UNPACK_ROW_LENGTH, mStride );
        GLES30.glTexImage3D( GL_TEXTURE_3D,
                             0, // Level
                             internalFormat(),
                             width(),
                             height(),
                             depth(),
                             0,
                             format(),
                             dataType(),
                             mBuf );
        GLES30.glPixelStorei( GL_UNPACK_ROW_LENGTH, mStride );
        mBuf = null;
    }
}
