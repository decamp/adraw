/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d.text;

import android.graphics.Typeface;

import java.util.Map;
import java.util.WeakHashMap;


/**
 * Stores FontTextures associated with different GLContexts.
 *
 * @author decamp
 */
public class FontManager {

    private final Map<Typeface, FontTexture> mMap = new WeakHashMap<>();

    public synchronized FontTexture getFontTexture( Typeface font ) {
        FontTexture tex = mMap.get( font );

        if( tex == null ) {
            tex = new FontTexture( font );
            mMap.put( font, tex );
        }

        return tex;
    }


}
