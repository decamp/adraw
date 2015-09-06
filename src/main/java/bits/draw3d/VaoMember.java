/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

import android.opengl.GLES30;


/**
 * Vertex Attribute
 *
 * @author Philip DeCamp
 */
public class VaoMember {

    public int      mLocation;
    public int      mCompNum;
    public int      mType;
    public boolean  mNormalize;

    public int      mStride        = -1;
    public int      mOffset        = -1;


    public VaoMember() {}


    public VaoMember( int location, int compNum, int type, boolean normalize ) {
        this( location, compNum, type, normalize, -1, -1 );
    }


    public VaoMember( int location, int compNum, int type, boolean normalize, int stride, int offset ) {
        mLocation  = location;
        mCompNum   = compNum;
        mType      = type;
        mNormalize = normalize;
        mStride    = stride;
        mOffset    = offset;

    }


    public void enable() {
        GLES30.glVertexAttribPointer( mLocation, mCompNum, mType, mNormalize, mStride, mOffset );
        GLES30.glEnableVertexAttribArray( mLocation );
    }


    public void disable() {
        GLES30.glVertexAttribPointer( mLocation, mCompNum, mType, mNormalize, mStride, mOffset );
        GLES30.glDisableVertexAttribArray( mLocation );
    }

}
