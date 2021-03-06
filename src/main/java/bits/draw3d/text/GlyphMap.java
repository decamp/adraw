/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d.text;

interface GlyphMap {
    
    Glyph get( char c );
    
    Glyph put(
            char c,
            float advance,
            int x0,
            int y0,
            float s0,
            float t0,
            int x1,
            int y1,
            float s1,
            float t1
    );
    
    /**
     * After constructing the table through a series of
     * {@code write()} commands, {@code optimize()}
     * must be called to prepare the table for use.
     */
    void optimize();
    
    /**
     * @return glyph to use for unrecognized characters.
     */
    Glyph unknownGlyph();
    
    CharSequence chars();
    char getChar( int index );
}
