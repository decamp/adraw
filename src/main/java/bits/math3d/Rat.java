/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.math3d;

/**
 * Rationals Math
 * @Deprecated use bits.microtime.Frac
  */
public class Rat {

    public static final int ROUND_ZERO     = 0x0000; ///< Round toward zero.
    public static final int ROUND_INF      = 0x0001; ///< Round away from zero.
    public static final int ROUND_DOWN     = 0x0002; ///< Round toward -infinity.
    public static final int ROUND_UP       = 0x0003; ///< Round toward +infinity.
    public static final int ROUND_NEAR_INF = 0x0005; ///< Round to nearest and halfway cases away from zero.

    /**
     * Rescale a 64-bit integer, val * append / div,
     * with protection against overflow.
     *
     * @return long mVal nearest to val * append / div
     */
    public static long mult( long val, long mult, long div ) {
        return mult( val, mult, div, ROUND_NEAR_INF );
    }

    /**
     * Rescale a 64-bit integer, val * append / div,
     * with requested rounding and protection against overflow.
     *
     * @return long mVal nearest to val * append / div
     */
    public static long mult( long val, long mult, long div, int round ) {
        assert( div > 0 );
        assert( mult >= 0 );

        if( val < 0 && val != Long.MIN_VALUE ) {
            return -mult( -val, mult, div, round ^ ((round >> 1) & 1) );
        }

        long r = 0;
        if( round == ROUND_NEAR_INF ) {
            r = div / 2;
        } else if( ( round & 1 ) != 0 ) {
            r = div -1;
        }

        if( mult <= Integer.MAX_VALUE && div <= Integer.MAX_VALUE ) {
            if( val <= Integer.MAX_VALUE ) {
                return ( val * mult + r ) / div;
            } else {
                return val / div * mult + ( val % div * mult + r ) / div;
            }

        } else {
            long a0 = val & 0xFFFFFFFFL;
            long a1 = val >> 32;
            long b0 = mult & 0xFFFFFFFFL;
            long b1 = mult >> 32;
            long t1 = a0 * b1 + a1 * b0;
            long t1a = t1 << 32;

            a0 = a0 * b0 + t1a;
            a1 = a1 * b1 + ( t1 >> 32 ) + ( a0 < t1a ? 1 : 0 );
            a0 += r;
            a1 += ( a0 < r ? 1 : 0 );

            for( int i = 63; i >= 0; i-- ) {
                a1 += a1 + (( a0 >> i ) & 1 );
                t1 += t1;
                if( div <= a1 ) {
                    a1 -= div;
                    t1++;
                }
            }

            return t1;
        }
    }

    /**
     * Computes greatest common divisor of two terms using Euclid's method.
     *
     * @param a Arbitrary number
     * @param b Arbitrary number
     * @return Largest number that evenly divides both a and b.
     */
    public static int gcd( int a, int b ) {
        while( b != 0 ) {
            int c = a % b;
            a = b;
            b = c;
        }
        return a;
    }

    /**
     * Computes greatest common divisor of two terms using Euclid's method.
     *
     * @param a Arbitrary number
     * @param b Arbitrary number
     * @return Largest number that evenly divides both a and b.
     */
    public static long gcd( long a, long b ) {
        while( b != 0 ) {
            long c = a % b;
            a = b;
            b = c;
        }

        return a;
    }

}
