/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.math3d;

import java.util.NoSuchElementException;


/**
 * 3-dimensional vector.
 *
 * Extending {@code Vec2} might seem kind of weird, but I tested allocation time and it was
 * slower by about 2%. I was using arrays before, which are nice and general purpose, but
 * require an extra 4 bytes of memory, are slower to createGeom, and require runtime rangeOf checks.
 *
 * @author Philip DeCamp
 */
public class Vec4 extends Vec3 {

    public float w;


    public Vec4() {}


    public Vec4( float x, float y, float z, float w ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }


    public Vec4( Vec4 copy ) {
        this.x = copy.x;
        this.y = copy.y;
        this.z = copy.z;
        this.w = copy.w;
    }



    public float el( int d ) {
        switch( d ) {
        case 0: return x;
        case 1: return y;
        case 2: return z;
        case 3: return w;
        default:
            throw new NoSuchElementException();
        }
    }


    public void el( int d, float v ) {
        switch( d ) {
        case 0: x = v; return;
        case 1: y = v; return;
        case 2: z = v; return;
        case 3: w = v; return;
        default:
            throw new NoSuchElementException();
        }
    }


    @Override
    public boolean equals( Object obj ) {
        if( !(obj instanceof Vec4) ) {
            return false;
        }

        Vec4 v = (Vec4)obj;
        // v == this is necessary to catch NaNs.
        return v == this || x == v.x && y == v.y && z == v.z && w == v.w;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits( x + y + z + w );
    }

    @Override
    public String toString() {
        return Vec.format( this );
    }

}
