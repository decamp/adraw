/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

import bits.math3d.*;


/**
 * Provides access to one member of a block of UBO data.
 *
 * <p>After altering data (calling any of the {@code apply} commands),
 * you will need to rebind the parent block.
 *
 * @author Philip DeCamp
 */
public interface UboMember {

    Uniform target();

    int   getInt();
    int   getComponentInt( int elem, int row, int col );
    float getFloat();
    float getComponentFloat( int elem, int row, int col );
    void  get( Vec2 vec );
    void  get( Vec3 vec );
    void  get( Mat3 mat );
    void  get( Mat4 mat );
    void  get( int firstElem, int[] out, int off, int len );
    void  get( int firstElem, float[] out, int off, int len );
    void  get( int firstElem, Vec2[] out, int off, int len );
    void  get( int firstElem, Vec3[] out, int off, int len );
    void  get( int firstElem, Vec4[] out, int off, int len );
    void  get( int firstElem, Mat3[] out, int off, int len );
    void  get( int firstElem, Mat4[] out, int off, int len );

    void  set( int val );
    void  set( float val );
    void  set( Vec2 vec );
    void  set( Vec3 vec );
    void  set( Vec4 vec );
    void  set( Mat3 mat );
    void  set( Mat4 mat );
    void  set( int firstElem, int[] vals, int off, int len );
    void  set( int firstElem, float[] vals, int off, int len );
    void  set( int firstElem, Vec2[] vals, int off, int len );
    void  set( int firstElem, Vec3[] vals, int off, int len );
    void  set( int firstElem, Vec4[] vals, int off, int len );
    void  set( int firstElem, Mat3[] vals, int off, int len );
    void  set( int firstElem, Mat4[] vals, int off, int len );

    void  setComponent( int elem, int row, int col, int val );
    void  setComponent( int elem, int row, int col, float val );

}
