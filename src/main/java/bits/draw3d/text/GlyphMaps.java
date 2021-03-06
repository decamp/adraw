/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d.text;

class GlyphMaps {
    static GlyphMap createGlyphMap() {
        return new FlatGlyphMap( CharSet.DEFAULT );
    }

    static GlyphMap createGlyphMap( CharSet chars ) {
        if( chars == null ) {
            chars = CharSet.DEFAULT;
        }
        return new FlatGlyphMap( chars );
    }
}
