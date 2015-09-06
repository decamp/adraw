/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.math3d;


/**
 * Trans3 (3D Transform) represents a getRotation and getTranslation, in that order.
 */
public class Trans3 {
    public final Vec3 mPos = new Vec3();
    public final Mat3 mRot = new Mat3( 1, 0, 0, 0, 1, 0, 0, 0, 1 );


    public Trans3() {}


    public Trans3( Vec3 pos, Mat3 rot ) {
        Vec.put( pos, mPos );
        Mat.put( rot, mRot );
    }


    public Trans3( Trans3 copy ) {
        this( copy.mPos, copy.mRot );
    }


    @Override
    public boolean equals( Object obj ) {
        if( !(obj instanceof Trans3) ) {
            return false;
        }
        Trans3 tr = (Trans3)obj;
        return tr == this || mPos.equals( tr.mPos ) && mRot.equals( tr.mRot );
    }

    @Override
    public int hashCode() {
        return mPos.hashCode() ^ mRot.hashCode();
    }

    @Override
    public String toString() {
        return Trans.format( this );
    }
}
