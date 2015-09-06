/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.math3d;

import java.util.NoSuchElementException;


/**
 * Functions for length-2 vectors.
 *
 * @author Philip DeCamp  
 */
public class Vec2 {

    public float x;
    public float y;


    public Vec2() {}


    public Vec2( float x, float y ) {
        this.x = x;
        this.y = y;
    }


    public Vec2( Vec2 copy ) {
        x = copy.x;
        y = copy.y;
    }



    public float el( int d ) {
        switch( d ) {
        case 0: return x;
        case 1: return y;
        default:
            throw new NoSuchElementException();
        }
    }


    public void el( int d, float v ) {
        switch( d ) {
        case 0: x = v; break;
        case 1: y = v; break;
        default:
            throw new NoSuchElementException();
        }
    }


    @Override
    public boolean equals( Object obj ) {
        if( !( obj instanceof Vec2) ) {
            return false;
        }
        Vec2 v = (Vec2)obj;
        // "v == this" is needed to protect against NaNs.
        return v == this || x == v.x && y == v.y;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits( x + y );
    }

    @Override
    public String toString() {
        return Vec.format( this );
    }

}
