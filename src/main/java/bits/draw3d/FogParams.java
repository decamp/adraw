/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

import bits.vec.Vec;
import bits.vec.Vec4;


/**
 * @author Philip DeCamp
 */
public class FogParams {
    public final Vec4  mColor   = new Vec4( 0, 0, 0, 0 );
    public       float mStart   = 0f;
    public       float mDensity = 0f;

    public FogParams() {}

    public FogParams( FogParams copy ) {
        Vec.put( copy.mColor, mColor );
        mStart = copy.mStart;
        mDensity = copy.mDensity;
    }

}
