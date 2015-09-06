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
public class Vec3 {

    public float x;
    public float y;
    public float z;


    public Vec3() {}


    public Vec3( float x, float y, float z ) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public Vec3( Vec3 copy ) {
        x = copy.x;
        y = copy.y;
        z = copy.z;
    }



    public float el( int d ) {
        switch( d ) {
        case 0: return x;
        case 1: return y;
        case 2: return z;
        default:
            throw new NoSuchElementException();
        }
    }


    public void el( int d, float v ) {
        switch( d ) {
        case 0: x = v; return;
        case 1: y = v; return;
        case 2: z = v; return;
        default:
            throw new NoSuchElementException();
        }
    }


    @Override
    public boolean equals( Object obj ) {
        if( !( obj instanceof Vec3) ) {
            return false;
        }

        Vec3 v = (Vec3)obj;
        // v == this is necessary to catch NaNs.
        return v == this || x == v.x && y == v.y && z == v.z;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits( x + y + z );
    }

    @Override
    public String toString() {
        return Vec.format( this );
    }

}
