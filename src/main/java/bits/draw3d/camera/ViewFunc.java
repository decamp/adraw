/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d.camera;

import bits.draw3d.actor.Actor;
import bits.vec.Mat4;


public interface ViewFunc {
    void computeViewMat( Actor camera, Mat4 out );
}
