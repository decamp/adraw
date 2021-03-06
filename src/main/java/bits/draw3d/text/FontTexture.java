/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d.text;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLES30;
import bits.draw3d.*;

import static android.opengl.GLES30.GL_TEXTURE0;


/**
 * Manages resources for a single font and prints text to screen. This class
 * uses java.awt graphics to rasterize the fonts at a defined resolution defined
 * by the size of the font, then transfers the rasterized glyphs to an OpenGL
 * texture that can used for fairly efficient printing. AWT is also used to
 * compute kernings during rendering.
 * <p>
 * Note that because the entire typeface is rasterized, each instance of this
 * class can cost significant memory resources. This class is meant to be used
 * in conjuncture with FontManager to minimize the number of textures generated.
 * <p>
 * FontTexture essentially maps pixels onto GL coordinates, so if you allocMembersBuffer
 * a FontTexture with a Font with pointsize 12.0 that maps to 16 pixels high,
 * the FontTexture will render that Font 16.0 GL units tall. You can alter
 * height by scaling your matrix stack, but keep in mind the resolution of the
 * texture is setOn and the quality of the font rendering will be diminished.
 * If you want zero distortation, you should use an orthographic projection
 * with bounds equal to the resolution of your canvas, and make sure the 
 * that your lines of text are rendered at integral coordinates.
 * <p>
 * 
 * @author Philip DeCamp
 */
public class FontTexture implements DrawUnit {

    private final Typeface          mFont;
    private final Paint.FontMetrics mMetrics;
    private final GlyphMap          mGlyphs;
    private final Mipmap2           mTexture;


    public FontTexture( Typeface font ) {
        this( font, CharSet.DEFAULT );
    }


    public FontTexture( Typeface font, CharSet chars ) {
        mFont = font;
        mMetrics = FontUtil.metrics( font );
        mGlyphs = GlyphMaps.createGlyphMap( chars );

        // TODO: Reimplement
//        int dim = 256;
//        int margin = 4;
//        computeGlyphSizes( mMetrics, margin, mGlyphs );
//
//        // Brute force size determination.  Whatevs.
//        while( !layoutGlyphs( dim, dim, mGlyphs, null ) ) {
//            dim <<= 1;
//            if( dim > 1024 * 4 ) {
//                throw new InstantiationError( "Font size too large for memory: " + mFont.getSize() );
//            }
//        }
//
//        BufferedImage im = new BufferedImage( dim, dim, BufferedImage.TYPE_BYTE_GRAY );
//        Graphics2D g = (Graphics2D)im.getGraphics();
//        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
//        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
//        g.setFont( mFont );
//        g.setBackground( Color.BLACK );
//        g.setColor( Color.WHITE );
//
//        layoutGlyphs( dim, dim, mGlyphs, g );
//        mGlyphs.optimize();
//
        mTexture = new Mipmap2();
//        mTexture.param( GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR );
//        mTexture.param( GL_TEXTURE_SWIZZLE_R, GL_ONE );
//        mTexture.param( GL_TEXTURE_SWIZZLE_G, GL_ONE );
//        mTexture.param( GL_TEXTURE_SWIZZLE_B, GL_ONE );
//        mTexture.param( GL_TEXTURE_SWIZZLE_A, GL_RED );
//        mTexture.buffer( im );
    }


    /**
     * You MUST call this method before using texture for rendering.
     */
    public void bind( DrawEnv g ) {
        if( mTexture == null ) {
            init( g );
        }
        mTexture.bind( g );
    }


    public void bind( DrawEnv d, int unit ) {
        GLES30.glActiveTexture( GL_TEXTURE0 + unit );
        bind( d );
    }

    /**
     * You MUST call this method after you are done using texture for rendering.
     */
    public void unbind( DrawEnv g ) {
        if( mTexture == null ) {
            return;
        }
        mTexture.unbind( g );
    }


    public void unbind( DrawEnv g, int unit ) {
        GLES30.glActiveTexture( GL_TEXTURE0 + unit );
        unbind( g );
    }

    /**
     * This method will be called automatically, but you can call it 
     * manually for initialization scheduling purposes.
     */
    public void init( DrawEnv g ) {
        mTexture.init( g );
        mTexture.bind( g );
        mTexture.unbind( g );
        g.checkErr();
    }
    
    /**
     * Call to unload resources. After unloaded, the texture CANNOT be used again.
     */
    public void dispose( DrawEnv g ) {
        mTexture.dispose( g );
    }

    
    public boolean isLoaded() {
        return true;
    }



    public Typeface getFont() {
        return mFont;
    }

    
    public int getStyle() {
        return mFont.getStyle();
    }

    
    public float getPointSize() {
        return mMetrics.descent + mMetrics.ascent;
    }

    
    public float getHeight() {
        return mMetrics.bottom - mMetrics.top;
    }

    
    public float getAscent() {
        return mMetrics.ascent;
    }


    public float getDescent() {
        return mMetrics.descent;
    }


    public float getLeading() {
        return mMetrics.leading;
    }

    
    public float getCharWidth( char c ) {
        return mGlyphs.get( c ).mAdvance;
    }

    /**
     * Computes width of sequence of characters. Because FontTexture
     * precomputes sizes, this is probably faster than using 
     * FontUtil.charsWidth(), and accounts for any glyphs that the 
     * FontTexture may have failed to rasterize. It also handles
     * newlines and returns max length of any line.
     */
    public float getCharsWidth( char[] chars, int off, int len ) {
        float maxWidth = 0f;
        float width    = 0f;
        
        for( int i = 0; i < len; i++ ) {
            char c = chars[i + off];
            
            if( c == '\n' ) {
                if( width > maxWidth ) {
                    maxWidth = width;
                }
                width = 0f;
                continue;
            }
            
            Glyph g = mGlyphs.get( c );
            width += g.mAdvance;
        }
        
        if( width > maxWidth ) {
            maxWidth = width;
        }
        
        return maxWidth;
    }
    
    /**
     * Computes width of sequence of characters. Because FontTexture
     * precomputes kerning tables for each glyph, this is probably
     * faster than using FontUtil.charsWidth(), and accounts for any
     * glyphs that the FontTexture may have failed to rasterize.
     */
    public float getCharsWidth( CharSequence chars ) {
        final int len  = chars.length();
        float maxWidth = 0f;
        float width    = 0f;

        for( int i = 0; i < len; i++ ) {
            char c = chars.charAt( i );
            if( c == '\n' ) {
                if( width > maxWidth ) {
                    maxWidth = width;
                }

                width = 0f;
                continue;
            }

            Glyph g = mGlyphs.get( c );
            width += g.mAdvance;
        }
        
        if( width > maxWidth ) {
            maxWidth = width;
        }

        return maxWidth;
    }



    /**
     * Call this before using texture for rendering.
     */
    public void beginRenderChars( DrawEnv g ) {
        g.mBlend.push();
        g.mBlend.apply( true );
        bind( g );
        DrawStream s = g.drawStream();
        s.config( true, true, false );
        s.beginQuads();
    }

    /**
     * You MUST call this method after you are done using texture for rendering.
     */
    public void endRenderChars( DrawEnv d ) {
        if( mTexture == null ) {
            return;
        }
        d.drawStream().end();
        mTexture.unbind( d );
        d.mBlend.pop();
    }

    /**
     * Renders character array to screen using [0, 0, 0]
     * as the start of the baseline.
     * <p>
     * You MUST push the FontTexture before calling this method.
     * The characters will be rendered using the current GL colorub.
     */
    public void renderChars( DrawEnv d, char[] chars, int off, int len ) {
        renderChars( d, 0.0f, 0.0f, 0.0f, chars, off, len );
    }

    /**
     * Renders character sequence to screen using [x, y, z]
     * as the start of the baseline.
     * <p> 
     * You MUST push the FontTexture before calling this method.
     * The characters will be rendered using the current GL colorub.
     */
    public void renderChars( DrawEnv d,
                             float x, 
                             float y, 
                             float z,
                             char[] chars,
                             int off,
                             int len ) 
    {
//        float xx = x;
//        float yy = y;
//        DrawStream s = d.drawStream();
//
//        for( int i = 0; i < len; i++ ) {
//            char c = chars[i + off];
//            if( c == '\n' ) {
//                xx = x;
//                yy -= mMetrics.getHeight();
//                continue;
//            }
//
//            Glyph g = mGlyphs.get( c );
//            s.tex( g.mS0, g.mT0 );
//            s.vert( g.mX0 + xx, g.mY0 + yy, z );
//            s.tex( g.mS1, g.mT0 );
//            s.vert( g.mX1 + xx, g.mY0 + yy, z );
//            s.tex( g.mS1, g.mT1 );
//            s.vert( g.mX1 + xx, g.mY1 + yy, z );
//            s.tex( g.mS0, g.mT1 );
//            s.vert( g.mX0 + xx, g.mY1 + yy, z );
//            xx += g.mAdvance;
//        }
    }
    
    /**
     * Renders character sequence to screen using [0, 0, 0]
     * as the start of the baseline.
     * <p>
     * You MUST push the FontTexture before calling this method.
     * The characters will be rendered using the current GL colorub.
     */
    public void renderChars( DrawEnv d, CharSequence chars ) {
        renderChars( d, 0.0f, 0.0f, 0.0f, chars );
    }

    /**
     * Renders character sequence to screen using [x, y, z]
     * as the start of the baseline.
     * <p> 
     * You MUST push the FontTexture before calling this method.
     * The characters will be rendered using the current GL colorub.
     */
    public void renderChars( DrawEnv d, float x, float y, float z, CharSequence chars ) {
//        final int len = chars.length();
//        float xx = x;
//        float yy = y;
//        DrawStream s = d.drawStream();
//
//        for( int i = 0; i < len; i++ ) {
//            char c = chars.charAt( i );
//            if( c == '\n' ) {
//                xx = x;
//                yy -= mMetrics.getHeight();
//                continue;
//            }
//
//            Glyph g = mGlyphs.get( c );
//            s.tex( g.mS0, g.mT0 );
//            s.vert( g.mX0 + xx, g.mY0 + yy, z );
//            s.tex( g.mS1, g.mT0 );
//            s.vert( g.mX1 + xx, g.mY0 + yy, z );
//            s.tex( g.mS1, g.mT1 );
//            s.vert( g.mX1 + xx, g.mY1 + yy, z );
//            s.tex( g.mS0, g.mT1 );
//            s.vert( g.mX0 + xx, g.mY1 + yy, z );
//
//            xx += g.mAdvance;
//        }
   }

    /**
     * Call this before using texture for rendering.
     */
    public void beginRenderBox( DrawEnv d ) {
        d.mBlend.push();
        d.mBlend.apply( true );
        DrawStream s = d.drawStream();
        s.config( true, false, false );
        s.beginQuads();
    }

    /**
     * You MUST call this method after you are done using texture for rendering boxes.
     */
    public void endRenderBox( DrawEnv d ) {
        d.drawStream().end();
        d.mBlend.pop();
    }

    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Rect will be rendered using current GL color.
     * 
     * @param margin  Margin by with box will exceed bounds of text.
     */
    public void renderBox( DrawEnv d, CharSequence s, float margin ) {
        renderBox( d, 0.0f, 0.0f, 0.0f, getCharsWidth( s ), margin );
    }
    
    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Rect will be rendered using current GL color.
     */
    public void renderBox( DrawEnv d, float x, float y, float z, CharSequence s, float margin ) {
        renderBox( d, x, y, z, getCharsWidth( s ), margin );
    }
    
    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Rect will be rendered using current GL color.
     */
    public void renderBox( DrawEnv d, char[] chars, int off, int len, float margin ) {
        float width = getCharsWidth( chars, off, len );
        renderBox( d, width, margin );
    }

    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Rect will be rendered using current GL color.
     */
    public void renderBox( DrawEnv d, float x, float y, float z, char[] chars, int off, int len, float margin ) {
        renderBox( d, x, y, z, getCharsWidth( chars, off, len ), margin );
    }

    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Rect will be rendered using current GL color.
     */
    public void renderBox( DrawEnv d, float width, float margin ) {
        renderBox( d, 0.0f, 0.0f, 0.0f, width, margin );
    }

    /**
     * Convenience method for rendering a box that will surround text.
     * The FontTexture MUST NOT be pushed before calling this method.
     * <p>
     * Not that the box will be rendered at the same depth as the text,
     * so if you're using depth testing, you might want to use a poly
     * offset on your boxes.
     * <p>
     * Rect will be rendered using current GL color.
     */
    public void renderBox( DrawEnv d, float x, float y, float z, float width, float margin ) {
//        DrawStream s = d.drawStream();
//        float descent = getDescent();
//        float ascent  = getAscent();
//
//        s.vert( x - margin, y - descent, z );
//        s.vert( x + width + margin, y - descent, z );
//        s.vert( x + width + margin, y + ascent, z );
//        s.vert( x - margin, y + ascent, z );
    }

    
    
//    private static void computeGlyphSizes( FontMetrics metrics, int margin, GlyphMap glyphs ) {
//        final Font font = metrics.getFont();
//        final FontRenderContext context = metrics.getFontRenderContext();
//        final CharSequence chars = glyphs.chars();
//        final int len = chars.length();
//        final char[] carr = new char[1];
//
//        for( int i = 0; i < len; i++ ) {
//            char c = chars.charAt( i );
//            carr[0] = c;
//            float advance  = metrics.charWidth( c );
//            // TODO: Check if this can be done faster.
//            // I'm not sure, but I think I tried getMaxCharBounds() and it wasn't pixel accurate.
//            Rectangle rect = font.createGlyphVector( context, carr ).getOutline().getBounds();
//            glyphs.put( c,
//                        advance,
//                        rect.x - margin,
//                        -rect.y - rect.height - margin,
//                        0,
//                        0,
//                        rect.x + rect.width + margin,
//                        -rect.y + margin,
//                        0,
//                        0 );
//        }
//    }
//
//
//    private static boolean layoutGlyphs( int texWidth, int texHeight, GlyphMap glyphs, Graphics2D g ) {
//        final CharSequence chars = glyphs.chars();
//        final int len = chars.length();
//        final char[] carr = new char[1];
//
//        int x = 0;
//        int y = 0;
//        int lineHeight = 0;
//
//        for( int i = 0; i < len; i++ ) {
//            char c  = chars.charAt( i );
//            carr[0] = c;
//            Glyph glyph = glyphs.get( c );
//
//            int bx = glyph.mX0;
//            int by = glyph.mY0;
//            int bw = ( glyph.mX1 - glyph.mX0 );
//            int bh = ( glyph.mY1 - glyph.mY0 );
//
//            // Check if character will fit horizontally on line.
//            if( x + bw > texWidth ) {
//                // If single character does not fit on line,
//                // no point going to next line.
//                if( x == 0 ) {
//                    return false;
//                }
//                // Go to next line.
//                x = 0;
//                y += lineHeight;
//                lineHeight = 0;
//            }
//            // Check if character will fit vertically into texture.
//            if( bh > lineHeight ) {
//                lineHeight = bh;
//                if( y + lineHeight > texHeight ) {
//                    return false;
//                }
//            }
//            // Draw glyph if there's a graphics object.
//            if( g != null ) {
//                g.drawChars( carr, 0, 1, x - bx, texHeight - y + by );
//
//                // Update texture bounds on glyph.
//                glyphs.put( c,
//                            glyph.mAdvance,
//                            bx,
//                            by,
//                            (float)x / texWidth,
//                            1f - (float)y / texHeight,
//                            bx + bw,
//                            by + bh,
//                            (float)( x + bw ) / texWidth,
//                            1f - (float)( y + bh ) / texHeight );
//            }
//
//            x += bw;
//        }
//
//        return true;
//    }
    
}
