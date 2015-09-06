/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

/**
 * @author Philip DeCamp
 */
public class ShaderManager {

    public Shader loadSource( int type, String source ) {
        return new Shader( type, source );
    }

}
