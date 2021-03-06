/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d.actor;

import bits.vec.Mat4;
import bits.vec.Vec3;

import java.util.Comparator;


/**
 * @author decamp
 */
public interface DepthSortable {
    
    /**
     * @return direct reference to vec that holds object position in Normalized Device Coordinates.
     */
    Vec3 normPosRef();

    /**
     * Updates the Normalize Device Coordinates of an object.
     * 
     * @param projViewMat Matrix that defines transformation from
     *                    model-coordinates to normalized-device-coordinates.
     */
    void updateNormPos( Mat4 projViewMat );
    
    /**
     * @return The depth of the object in normalized device coordinates.
     */
    float normDepth();


    Comparator<DepthSortable> BACK_TO_FRONT_ORDER = new Comparator<DepthSortable>() {
        public int compare( DepthSortable a, DepthSortable b ) {
            float aa = a.normDepth();
            float bb = b.normDepth();
            return aa > bb ? -1 :
                   aa < bb ? 1 : 0;
        }
    };


    Comparator<DepthSortable> FRONT_TO_BACK_ORDER = new Comparator<DepthSortable>() {
        public int compare( DepthSortable a, DepthSortable b ) {
            float aa = a.normDepth();
            float bb = b.normDepth();
            return aa < bb ? -1 :
                   aa > bb ? 1 : 0;
        }
    };

}
