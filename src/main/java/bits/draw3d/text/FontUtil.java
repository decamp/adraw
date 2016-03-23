/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d.text;

import android.graphics.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * TODO: Some of these methods may be more efficient once
 * the JVM has more fully integrated escape analysis. At 
 * that point, the use of alternate implementations for 
 * CharSequence and char arrays may be less optimal than
 * using a single implementation and wrapping parameters
 * where necessary, which would reduce the amount of code.
 * <p>
 * Also, it may eventually be necessary to produce a separate
 * FontMetrics implementation in order to handle kerning more
 * efficiently. Right now, kerning support in this library
 * is pretty limited for pfa and pfb fonts, and it's hard to
 * tell what AWT is actually doing with TT and OTF fonts. 
 * 
 * @author decamp
 */
public class FontUtil {

    private static final Bitmap  METRIC_IMAGE = Bitmap.createBitmap( 1, 1, Bitmap.Config.ARGB_8888 );
    private static final Paint   PAINT        = new Paint();
    private static final Pattern WORD_PAT     = Pattern.compile( "\\S++" );


    public static synchronized Paint createPaint( Typeface font ) {
        Paint ret = new Paint();
        ret.setTypeface( font );
        return ret;
    }


    public static synchronized Paint.FontMetrics metrics( Typeface font ) {
        PAINT.setTypeface( font );
        return PAINT.getFontMetrics();
    }


    public static synchronized float charsWidth( Typeface font, CharSequence s ) {
        PAINT.setTypeface( font );
        return PAINT.measureText( s, 0, s.length() );
    }


    public static synchronized float charsWidth( Typeface font, CharSequence s, int off, int len ) {
        PAINT.setTypeface( font );
        return PAINT.measureText( s, off, off + len );
    }


    public static synchronized float charsWidth( Typeface font, char[] chars, int off, int len ) {
        PAINT.setTypeface( font );
        return PAINT.measureText( chars, off, len );
    }

    /**
     * Determines how many characters may be laid in sequence
     * before reaching a set width.
     *
     * @param paint     Paint object used for layout.
     * @param chars     Input array of chars.
     * @param off       Offset into array of chars.
     * @param len       Number of chars in input.
     * @param maxWidth  Maximum width for layout.
     * @param outWidth  {@code outWidth[0]} will hold the exact width
     *                  of the fitted characters on return. May be {@code null}
     *                  May be {@code null}.
     *
     * @return The number of characters that fit within the specified width.
     */
    public static int findWidth(
            Paint paint,
            char[] chars,
            int off,
            int len,
            float maxWidth,
            float[] outWidth
    ) {
        int i = 0;
        float lineWidth = 0;
        for( ; i < len; i++ ) {
            float charWidth = paint.measureText( chars, i + off, 1 );
            lineWidth += charWidth;
            if( lineWidth > maxWidth ) {
                lineWidth -= charWidth;
                break;
            }
        }

        if( outWidth != null ) {
            outWidth[0] = lineWidth;
        }

        return i;

        /*
        // Version to use if kerning tables become available.
        if( len == 0 ) {
            if( outWidth != null ) {
                outWidth[0] = 0f;
            }
            return 0;
        }

        float width = metrics.charWidth( chars[off] );
        if( width > maxWidth ) {
            if( outWidth != null ) {
                outWidth[0] = 0f;
            }
            return 0;
        }

        float prevAdvance = width;

        for( int i = 1; i < len; i++ ) {
            float advance = metrics.charsWidth( chars, off + i - 1, 2 );
            prevAdvance   = advance - prevAdvance;
            width += prevAdvance;

            if( width > maxWidth ) {
                if( outWidth != null ) {
                    outWidth[0] = width - prevAdvance;
                }
                return i;
            }
        }

        if( outWidth != null ) {
            outWidth[0] = width;
        }
        return len;
        */
    }


    /**
     * Determines how many characters may be laid in sequence
     * before reaching a setOn width.
     *
     * @param paint     Paint object to use for layout.
     * @param seq       Input CharSequence to layout.
     * @param off       Offset into seq where input starts.
     * @param len       Number of chars in input.
     * @param maxWidth  Maximum width for layout.
     * @param outWidth  {@code outWidth[0]} will hold the exact width
     *                  the exact width of the fitted characters on return.
     *                  May be {@code null}.
     *
     * @return The number of characters that fit within the specified width.
     */
    public static int findWidth(
            Paint paint,
            CharSequence seq,
            int off,
            int len,
            float maxWidth,
            float[] outWidth
    ) {
        int i = 0;
        float lineWidth = 0;
        for( ; i < len; i++ ) {
            float charWidth = paint.measureText( seq, i + off, i + off + 1 );
            lineWidth += charWidth;
            if( lineWidth > maxWidth ) {
                lineWidth -= charWidth;
                break;
            }
        }

        if( outWidth != null ) {
            outWidth[0] = lineWidth;
        }

        return i;

        /*
        // Version to use if kerning tables become available.
        if( len == 0 ) {
            if( outWidth != null ) {
                outWidth[0] = 0f;
            }
            return 0;
        }

        char[] arr = { 0, seq.charAt( off ) };
        float width = metrics.charsWidth( arr, 1, 1 );
        if( width > maxWidth ) {
            if( outWidth[0] != 0f ) {
                outWidth[0] = 0f;
            }
            return 0;
        }

        float prevAdvance = width;

        for( int i = 1; i < len; i++ ) {
            arr[0] = arr[1];
            arr[1] = seq.charAt( i + off );
            float advance = metrics.charsWidth( arr, 0, 2 );
            prevAdvance   = advance - prevAdvance;
            width += prevAdvance;

            if( width > maxWidth ) {
                if( outWidth != null ) {
                    outWidth[0] = width - prevAdvance;
                }
                return i;
            }
        }

        if( outWidth != null ) {
            outWidth[0] = width;
        }
        return len;
        */
    }


    /**
     * Computes the right-edges of every character in a string. This is much
     * more efficient than calling charsWidth() on many substrings to find
     * intermediate positions of characters. Note that the first position is
     * implied to be 0, so on return, the output array holds only the right edge
     * of each glyph.
     *
     * @param out         Holds the right position of each character in the input
     *                    sequence. {@code out.length >= seq.length()}
     * @param outOffset   Offset into {@code out} where output will be written.
     */
    public static void charPositions( Paint paint, CharSequence seq, float[] out, int outOffset ) {
        charPositions( paint, seq, 0, seq.length(), out, outOffset );
    }


    /**
     * Computes the right-edges of every character in a string. This is much
     * more efficient than calling charsWidth() on many substrings to find
     * intermediate positions of characters. Note that the first position is
     * implied to be 0, so on return, the output array holds only the right edge
     * of each glyph.
     *
     * @param off         Offset into sequence where input begins.
     * @param len         Length of input to use.
     * @param out         Holds the right position of each character in the input
     *                    sequence. {@code out.length >= seq.length()}
     * @param outOffset   Offset into {@code out} where output will be written.
     */
    public static void charPositions(
            Paint paint,
            CharSequence seq,
            int off,
            int len,
            float[] out,
            int outOffset
    ) {
        int pos = 0;
        for( int i = 0; i < len; i++ ) {
            pos += paint.measureText( seq, i + off, i + off + 1 );
            out[ i + outOffset ] = pos;
        }

        /*
        // Only useful when kerning tables become available.
        if( len <= 0 ) {
            return;
        }
        char[] arr = { seq.charAt( off ), 0 };
        float width = metrics.charWidth( arr[0] );
        float prevAdvance = width;
        out[outOffset++] = width;

        for( int i = 1; i < len; i++ ) {
            arr[1] = seq.charAt( off + i );
            float advance = metrics.charsWidth( arr, 0, 2 );
            prevAdvance = advance - prevAdvance;
            width += prevAdvance;
            arr[0] = arr[1];

            out[outOffset++] = width;
        }
        */
    }


    /**
     * Computes the right-edges of every character in a string. This is much
     * more efficient than calling charsWidth() on many substrings to find
     * intermediate positions of characters. Note that the first position is
     * implied to be 0, so on return, the output array holds only the right edge
     * of each glyph.
     *
     * @param off         Offset into chars where input begins.
     * @param len         Length of input to use.
     * @param out         Holds the right position of each character in the input
     *                    sequence. {@code out.length >= seq.length()}
     * @param outOffset   Offset into {@code out} where output will be written.
     */
    public static void charPositions( Paint paint, char[] chars, int off, int len, float[] out, int outOffset ) {
        int width = 0;
        for( int i = 0; i < len; i++ ) {
            width += paint.measureText( chars, i + off, 1 );
            out[ i + outOffset ] = width;
        }

        /*
        // Not useful until kerning tables are available.
        if( len <= 0 ) {
            return;
        }

        float width = metrics.charWidth( chars[off] );
        float prevAdvance = width;
        out[outOffset++] = width;

        for( int i = 1; i < len; i++ ) {
            float advance = metrics.charsWidth( chars, off + i - 1, 2 );
            prevAdvance = advance - prevAdvance;
            width += prevAdvance;

            out[outOffset++] = width;
        }
        */
    }


    /**
     * Splits a single string into multiple lines to fit into defined space. Splits are made
     * at word boundaries.
     *
     * @param text       Input text to split.
     * @param paint    Metrics being used to render font.
     * @param lineWidth  Width of line into which the text must fit.
     *
     * @return A lost of Strings, with each string representing a single line.
     */
    public static List<String> splitAtWordBoundaries( String text, Paint paint, float lineWidth ) {
        final float spaceWidth = paint.measureText( " " );
        final StringBuilder s  = new StringBuilder();
        Matcher m = WORD_PAT.matcher( text );

        boolean newLine = true;
        float pos       = 0f;

        List<String> ret = new ArrayList<>( 5 );

        while( m.find() ) {
            float advance = paint.measureText( m.group( 0 ) );

            if( !newLine && pos + spaceWidth + advance > lineWidth ) {
                ret.add( s.toString() );
                s.setLength( 0 );
                pos = 0;
                newLine = true;
            }

            if( newLine ) {
                newLine = false;
            } else {
                s.append( ' ' );
                pos += spaceWidth;
            }

            s.append( m.group( 0 ) );
            pos += advance;
        }

        if( s.length() > 0 ) {
            ret.add( s.toString() );
        }

        return ret;
    }

}
