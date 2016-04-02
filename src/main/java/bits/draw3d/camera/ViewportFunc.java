/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d.camera;

import bits.draw3d.Rect;
import bits.vec.Mat4;

public interface ViewportFunc {
    void computeViewportMat( Rect viewport, Rect tileViewport, Mat4 out );
}
