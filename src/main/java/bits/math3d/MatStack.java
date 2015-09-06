/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.math3d;

import java.util.Arrays;
import java.util.EmptyStackException;


public class MatStack {

    private Mat4[] mStack;
    private      int  mPos = 0;
    public final Mat4 mMat = new Mat4();


    public MatStack() {
        mStack = new Mat4[4];
        for( int i = 0; i < mStack.length; i++ ) {
            mStack[i] = new Mat4();
        }
        identity();
    }


    public Mat4 get() {
        return mMat;
    }


    public void get( Mat4 out ) {
        Mat.put( mMat, out );
    }


    public void set( Mat4 m ) {
        Mat.put( m, mMat );
    }


    public void set( Mat3 m ) {
        Mat.put( m, mMat );
    }
    
    
    public void push() {
        ensureCapacity( mPos + 1 );
        Mat.put( mMat, mStack[mPos++] );
    }

    
    public void pop() {
        if( --mPos < 0 ) {
            mPos = 0;
            throw new EmptyStackException();
        }
        Mat.put( mStack[mPos], mMat );
    }

    
    public void identity() {
        Mat.identity( mMat );
    }


    public void mult( Mat4 m ) {
        Mat.mult( mMat, m, mMat );
    }


    public void mult( Mat3 m ) {
        Mat.mult( mMat, m, mMat );
    }

    
    public void premult( Mat4 m ) {
        Mat.mult( m, mMat, mMat );
    }


    public void premult( Mat3 m ) {
        Mat.mult( m, mMat, mMat );
    }


    public void invert() {
        Mat.invert( mMat, mMat );
    }


    public void translate( float dx, float dy, float dz ) {
        Mat.translate( mMat, dx, dy, dz, mMat );
    }


    public void preTranslate( float dx, float dy, float dz ) {
        Mat.preTranslate( dx, dy, dz, mMat, mMat );
    }

    
    public void rotate( float radians, float x, float y, float z ) {
        Mat.rotate( mMat, radians, x, y, z, mMat );
    }


    public void preRotate( float radians, float x, float y, float z ) {
        Mat.preRotate( radians, x, y, z, mMat, mMat );
    }


    public void scale( float sx, float sy, float sz, float sw ) {
        Mat.scale( mMat, sx, sy, sz, sw, mMat );
    }


    public void preScale( float sx, float sy, float sz, float sw ) {
        Mat.preScale( sx, sy, sz, sw, mMat, mMat );
    }


    public void multOrtho( float left, float right, float bottom, float top, float near, float far ) {
        Mat.multOrtho( mMat, left, right, bottom, top, near, far, mMat );
    }


    public void setOrtho( float left, float right, float bottom, float top, float near, float far ) {
        Mat.getOrtho( left, right, bottom, top, near, far, mMat );
    }


    public void multFrustum( float left, float right, float bottom, float top, float near, float far ) {
        Mat.multFrustum( mMat, left, right, bottom, top, near, far, mMat );
    }


    public void setFrustum( float left, float right, float bottom, float top, float near, float far ) {
        Mat.getFrustum( left, right, bottom, top, near, far, mMat );
    }


    public int size() {
        return mPos;
    }


    public void clear() {
        mPos = 0;
        identity();
    }

    
    public void ensureCapacity( int minCapacity ) {
        int oldCapacity = mStack.length;
        
        if( minCapacity > oldCapacity ) {
            int newCapacity = ( oldCapacity * 3 ) / 2 + 1;
            if( newCapacity < minCapacity ) {
                newCapacity = minCapacity;
            }
            mStack = Arrays.copyOf( mStack, newCapacity );
            for( int i = oldCapacity; i < newCapacity; i++ ) {
                mStack[i] = new Mat4();
            }
        }
    }
    
}
