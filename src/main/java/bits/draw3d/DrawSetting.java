/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

import android.opengl.GLES30;
import bits.math3d.Vec2;
import bits.math3d.Vec4;

import java.util.Arrays;

import static android.opengl.GLES30.*;


/**
 * @author Philip DeCamp
 */
public interface DrawSetting {

    void push();
    void pop();
    void apply();
    int  stackDepth();


    class Blend extends Stack<Blend> {
        public boolean mOn       = false;
        public int     mSrcRgb   = GL_ONE;
        public int     mDstRgb   = GL_ZERO;
        public int     mSrcAlpha = GL_ONE;
        public int     mDstAlpha = GL_ZERO;

        private final DrawEnv mD;


        public Blend( DrawEnv g ) {
            super( DEFAULT_CAP );
            mD = g;
        }


        private Blend() {
            mD = null;
        }


        public void apply( boolean on ) {
            mOn = on;
            apply();
        }


        public void apply( boolean on, int src, int dst ) {
            mOn = on;
            mSrcRgb = src;
            mSrcAlpha = src;
            mDstRgb = dst;
            mDstAlpha = dst;
            apply();
        }


        public void apply( boolean on, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha ) {
            mOn       = on;
            mSrcRgb   = srcRgb;
            mDstRgb   = dstRgb;
            mSrcAlpha = srcAlpha;
            mDstAlpha = dstAlpha;
            apply();
        }

        @Override
        public void apply() {
            if( mOn ) {
                GLES30.glEnable( GL_BLEND );
            } else {
                GLES30.glDisable( GL_BLEND );
            }
            GLES30.glBlendFuncSeparate( mSrcRgb, mDstRgb, mSrcAlpha, mDstAlpha );
        }


        @Override
        Blend alloc() {
            return new Blend();
        }

        @Override
        void getState( Blend a ) {
            a.setState( this );
        }

        @Override
        void setState( Blend a ) {
            mOn       = a.mOn;
            mSrcRgb   = a.mSrcRgb;
            mDstRgb   = a.mDstRgb;
            mSrcAlpha = a.mSrcAlpha;
            mDstAlpha = a.mDstAlpha;
        }
    }


    class BlendColor extends Stack<BlendColor> {
        public float mRed;
        public float mGreen;
        public float mBlue;
        public float mAlpha;

        private final DrawEnv mG;


        public BlendColor( DrawEnv g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private BlendColor() {
            mG = null;
        }


        public void apply( float red, float green, float blue, float alpha ) {
            mRed = red;
            mGreen = green;
            mBlue = blue;
            mAlpha = alpha;
            apply();
        }

        @Override
        public void apply() {
            GLES30.glBlendColor( mRed, mGreen, mBlue, mAlpha );
        }

        @Override
        BlendColor alloc() {
            return new BlendColor();
        }

        @Override
        void getState( BlendColor a ) {
            a.setState( this );
        }

        @Override
        void setState( BlendColor a ) {
            mRed = a.mRed;
            mGreen = a.mGreen;
            mBlue = a.mBlue;
            mAlpha = a.mAlpha;
        }
    }


    class Buffer extends Stack<int[]> {

        public final int mTarget;
        public       int mId;

        private final DrawEnv mEnv;
        private final int[] mWork = { 0 };


        Buffer( DrawEnv d, int target ) {
            mEnv = d;
            mTarget = target;
        }


        public int gen() {
            GLES30.glGenBuffers( 1, mWork, 0 );
            return mWork[0];
        }


        public void delete( int id ) {
            mWork[0] = id;
            GLES30.glDeleteBuffers( 1, mWork, 0 );
        }


        public void bind( int bufferObjectId ) {
            mId = bufferObjectId;
            apply();
        }


        public java.nio.Buffer map( int off, int len, int access ) {
            return GLES30.glMapBufferRange( mTarget, off, len, access );
        }


        public void unmap() {
            GLES30.glUnmapBuffer( mTarget );
        }

        @Override
        public void apply() {
            GLES30.glBindBuffer( mTarget, mId );
        }


        @Override
        int[] alloc() {
            return new int[1];
        }

        @Override
        void getState( int[] out ) {
            out[0] = mId;
        }

        @Override
        void setState( int[] item ) {
            mId = item[0];
        }
    }


    class ColorMask extends Stack<ColorMask> {
        public boolean mRed;
        public boolean mGreen;
        public boolean mBlue;
        public boolean mAlpha;

        private final DrawEnv mG;


        public ColorMask( DrawEnv g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private ColorMask() {
            mG = null;
        }


        public void apply( boolean red, boolean green, boolean blue, boolean alpha ) {
            mRed = red;
            mGreen = green;
            mBlue = blue;
            mAlpha = alpha;
            apply();
        }

        @Override
        public void apply() {
            GLES30.glColorMask( mRed, mGreen, mBlue, mAlpha );
        }


        @Override
        ColorMask alloc() {
            return new ColorMask();
        }

        @Override
        void getState( ColorMask a ) {
            a.setState( this );
        }

        @Override
        void setState( ColorMask a ) {
            mRed = a.mRed;
            mGreen = a.mGreen;
            mBlue = a.mBlue;
            mAlpha = a.mAlpha;
        }

    }


    class CullFace extends Stack<CullFace> {
        public boolean mOn   = false;
        public int     mFunc = GL_LESS;

        private final DrawEnv mG;


        public CullFace( DrawEnv g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private CullFace() {
            mG = null;
        }


        public void apply( boolean on ) {
            mOn = on;
            apply();
        }

        @Override
        public void apply() {
            if( mOn ) {
                GLES30.glEnable( GL_CULL_FACE );
            } else {
                GLES30.glDisable( GL_CULL_FACE );
            }
        }


        @Override
        CullFace alloc() {
            return new CullFace();
        }

        @Override
        void getState( CullFace a ) {
            a.mOn = mOn;
        }

        @Override
        void setState( CullFace a ) {
            mOn = a.mOn;
        }
    }


    class DepthMask extends Stack<DepthMask> {

        public boolean mOn = false;
        private final DrawEnv mG;


        public DepthMask( DrawEnv g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private DepthMask() {
            mG = null;
        }


        public void apply( boolean on ) {
            mOn = on;
            apply();
        }

        @Override
        public void apply() {
            GLES30.glDepthMask( mOn );
        }

        @Override
        DepthMask alloc() {
            return new DepthMask();
        }


        void getState( DepthMask out ) {
            out.mOn = mOn;
        }


        void setState( DepthMask copy ) {
            mOn = copy.mOn;
        }
    }


    class DepthTest extends Stack<DepthTest> {
        public boolean mOn   = false;
        public int     mFunc = GL_LESS;

        private final DrawEnv mG;


        public DepthTest( DrawEnv g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private DepthTest() {
            mG = null;
        }


        public void apply( boolean on ) {
            mOn = on;
            apply();
        }


        public void apply( boolean on, int func ) {
            mOn = on;
            mFunc = func;
            apply();
        }

        @Override
        public void apply() {
            if( mOn ) {
                GLES30.glEnable( GL_DEPTH_TEST );
            } else {
                GLES30.glDisable( GL_DEPTH_TEST );
            }
            GLES30.glDepthFunc( mFunc );
        }


        @Override
        DepthTest alloc() {
            return new DepthTest();
        }

        @Override
        void getState( DepthTest a ) {
            a.mOn = mOn;
            a.mFunc = mFunc;
        }

        @Override
        void setState( DepthTest a ) {
            mOn = a.mOn;
            mFunc = a.mFunc;
        }
    }


    class Fog extends Stack<Fog.State> {

        private final DrawEnv mEnv;

        public final Ubo       mUbo;
        public final UboMember mColor;
        public final UboMember mParams;

        private final Vec2 mWork = new Vec2();


        public Fog( DrawEnv d ) {
            mEnv = d;
            mUbo = new Ubo();
            mUbo.bindLocation( Uniforms.defaultBlockBinding( "FOG" ) );
            mColor = mUbo.addUniform( 1, GL_FLOAT_VEC4, "COLOR" );
            mParams = mUbo.addUniform( 1, GL_FLOAT_VEC2, "PARAMS" );
            mUbo.allocMembersBuffer();
        }


        public void apply( int bindLoc ) {
            mUbo.bindLocation( bindLoc );
            apply();
        }


        public void apply( int bindLoc, Vec4 color, float density, float start ) {
            mUbo.bindLocation( bindLoc );
            mColor.set( color );
            mWork.x = density;
            mWork.y = start;
            mParams.set( mWork );
            apply();
        }


        public void apply( Vec4 color, float density, float start ) {
            mColor.set( color );
            mWork.x = density;
            mWork.y = start;
            mParams.set( mWork );
            apply();
        }


        public void apply( FogParams params ) {
            apply( params.mColor, params.mStart, params.mDensity );
        }


        public int bindLocation() {
            return mUbo.bindLocation();
        }


        public void bindLocation( int loc ) {
            mUbo.bindLocation( loc );
        }


        public void getColor( Vec4 color ) {
            mColor.get( color );
        }


        public void setColor( Vec4 color ) {
            mColor.set( color );
        }


        public float density() {
            return mParams.getComponentFloat( 0, 0, 0 );
        }


        public void density( float density ) {
            mParams.setComponent( 0, 0, 0, density );
        }


        public float startDist() {
            return mParams.getComponentFloat( 0, 1, 0 );
        }


        public void startDist( float val ) {
            mParams.setComponent( 0, 1, 0, val );
        }

        @Override
        public int stackDepth() {
            return 0;
        }

        @Override
        public void apply() {
            mUbo.bind( mEnv );
        }


        @Override
        State alloc() {
            return new State();
        }

        @Override
        void getState( State out ) {
            out.mBindLocation = mUbo.bindLocation();
            mColor.get( out.mColor );
            mParams.get( out.mParams );
        }

        @Override
        void setState( State item ) {
            mUbo.bindLocation( item.mBindLocation );
            mColor.set( item.mColor );
            mParams.set( item.mParams );
        }


        static class State extends FogParams {
            int mBindLocation = -1;
            final Vec4 mColor  = new Vec4();
            final Vec2 mParams = new Vec2();
        }
    }


    class LineWidth extends Stack<LineWidth> {
        public float mValue = 1f;

        private final DrawEnv mG;

        public LineWidth( DrawEnv g ) {
            mG = g;
        }


        public void apply( float value ) {
            mValue = value;
        }

        @Override
        public void apply() {}


        @Override
        LineWidth alloc() {
            return new LineWidth( mG );
        }

        @Override
        void getState( LineWidth out ) {
            out.mValue = mValue;
        }

        @Override
        void setState( LineWidth item ) {
            mValue = item.mValue;
        }

    }


    class PointSize extends Stack<PointSize> {
        public float mValue = 1f;

        private final DrawEnv mG;

        public PointSize( DrawEnv g ) {
            mG = g;
        }


        public void apply( float value ) {
            mValue = value;
        }

        @Override
        public void apply() {}


        @Override
        PointSize alloc() {
            return new PointSize( mG );
        }

        @Override
        void getState( PointSize out ) {
            out.mValue = mValue;
        }

        @Override
        void setState( PointSize item ) {
            mValue = item.mValue;
        }

    }


    class PolygonOffset extends Stack<PolygonOffset> {
        public boolean mFillOn = false;
        public float   mFactor = 0f;
        public float   mUnits  = 0f;

        private final DrawEnv mG;


        public PolygonOffset( DrawEnv g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private PolygonOffset() {
            mG = null;
        }


        public void apply( boolean on ) {
            mFillOn = on;
            apply();
        }

        public void apply( boolean on, float factor, float units ) {
            mFillOn = on;
            mFactor = factor;
            mUnits = units;
            apply();
        }


        public void apply( boolean fillOn, boolean lineOn, boolean pointOn, float factor, float units ) {
            mFillOn = fillOn;
            mFactor = factor;
            mUnits = units;
            apply();
        }

        @Override
        public void apply() {
            if( mFillOn ) {
                GLES30.glEnable( GL_POLYGON_OFFSET_FILL );
            } else {
                GLES30.glDisable( GL_POLYGON_OFFSET_FILL );
            }
            GLES30.glPolygonOffset( mFactor, mUnits );
        }


        @Override
        PolygonOffset alloc() {
            return new PolygonOffset();
        }

        @Override
        void getState( PolygonOffset a ) {
            a.mFillOn = mFillOn;
            a.mFactor = mFactor;
            a.mUnits = mUnits;
        }

        @Override
        void setState( PolygonOffset a ) {
            mFillOn = a.mFillOn;
            mFactor = a.mFactor;
            mUnits = a.mUnits;
        }
    }


    class Program extends Stack<int[]> {

        private final DrawEnv mEnv;

        public int mId;


        Program( DrawEnv d ) {
            mEnv = d;
        }


        Program() {
            mEnv = null;
        }


        public void apply( int programId ) {
            mId = programId;
            GLES30.glUseProgram( mId );
        }

        @Override
        public void apply() {
            GLES30.glUseProgram( mId );
        }


        @Override
        int[] alloc() {
            return new int[1];
        }

        @Override
        void getState( int[] out ) {
            out[0] = mId;
        }

        @Override
        void setState( int[] item ) {
            mId = item[0];
        }

    }


    class ScissorTest extends Stack<ScissorTest> {
        public boolean mOn      = false;
        public int[]   mScissor = { 0, 0, 1, 1 };

        private final DrawEnv mG;


        public ScissorTest( DrawEnv g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private ScissorTest() {
            mG = null;
        }


        public void apply( boolean on ) {
            mOn = on;
            apply();
        }


        public void apply( boolean on, int x, int y, int w, int h ) {
            mOn = on;
            mScissor[0] = x;
            mScissor[1] = y;
            mScissor[2] = w;
            mScissor[3] = h;
            apply();
        }

        @Override
        public void apply() {
            if( mOn ) {
                GLES30.glEnable( GL_SCISSOR_TEST );
            } else {
                GLES30.glDisable( GL_SCISSOR_TEST );
            }
            GLES30.glScissor( mScissor[0], mScissor[1], mScissor[2], mScissor[3] );
        }


        @Override
        ScissorTest alloc() {
            return new ScissorTest();
        }

        @Override
        void getState( ScissorTest a ) {
            a.setState( this );
        }

        @Override
        void setState( ScissorTest a ) {
            mOn = a.mOn;
            System.arraycopy( a.mScissor, 0, mScissor, 0, 4 );
        }
    }


    class StencilTest extends Stack<StencilTest> {
        public boolean mOn        = false;
        public int     mFrontFunc = GL_ALWAYS;
        public int     mFrontRef  = 0;
        public int     mFrontMask = 0xFFFFFFFF;
        public int     mBackFunc  = GL_ALWAYS;
        public int     mBackRef   = 0;
        public int     mBackMask  = 0xFFFFFFFF;

        private final DrawEnv mG;


        public StencilTest( DrawEnv g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private StencilTest() {
            mG = null;
        }


        public void front( int frontFunc, int frontRef, int frontMask ) {
            mFrontFunc = frontFunc;
            mFrontRef = frontRef;
            mFrontMask = frontMask;
        }


        public void back( int backFunc, int backRef, int backMask ) {
            mBackFunc = backFunc;
            mBackRef = backRef;
            mBackMask = backMask;
        }


        public void apply( boolean on ) {
            mOn = on;
            apply();
        }


        public void apply( boolean on, int func, int ref, int mask ) {
            mOn = on;
            mFrontFunc = mBackFunc = func;
            mFrontRef = mBackRef = ref;
            mFrontMask = mBackMask = mask;
            apply();
        }

        @Override
        public void apply() {
            if( mOn ) {
                GLES30.glEnable( GL_STENCIL_TEST );
            } else {
                GLES30.glDisable( GL_STENCIL_TEST );
            }
            GLES30.glStencilFuncSeparate( GL_FRONT, mFrontFunc, mFrontRef, mFrontMask );
            GLES30.glStencilFuncSeparate( GL_BACK, mBackFunc, mBackRef, mBackMask );
        }


        @Override
        StencilTest alloc() {
            return new StencilTest();
        }

        @Override
        void getState( StencilTest out ) {
            out.setState( this );
        }

        @Override
        void setState( StencilTest copy ) {
            mOn = copy.mOn;
            mFrontFunc = copy.mFrontFunc;
            mFrontRef = copy.mFrontRef;
            mFrontMask = copy.mFrontMask;
            mBackFunc = copy.mBackFunc;
            mBackRef = copy.mBackRef;
            mBackMask = copy.mBackMask;
        }
    }


    class StencilOp extends Stack<StencilOp> {
        public int mFrontStencilFail = GL_KEEP;
        public int mFrontDepthFail   = GL_KEEP;
        public int mFrontPass        = GL_KEEP;
        public int mBackStencilFail  = GL_KEEP;
        public int mBackDepthFail    = GL_KEEP;
        public int mBackPass         = GL_KEEP;

        private final DrawEnv mG;


        public StencilOp( DrawEnv g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private StencilOp() {
            mG = null;
        }


        public void apply( int stencilFail, int depthFail, int pass ) {
            mFrontStencilFail = mBackStencilFail = stencilFail;
            mFrontDepthFail = mBackDepthFail = depthFail;
            mFrontPass = mBackPass = pass;
            apply();
        }


        public void apply() {
            GLES30.glStencilOpSeparate( GL_FRONT, mFrontStencilFail, mFrontDepthFail, mFrontPass );
            GLES30.glStencilOpSeparate( GL_BACK, mBackStencilFail, mBackStencilFail, mBackPass );
        }

        @Override
        StencilOp alloc() {
            return new StencilOp();
        }

        @Override
        void getState( StencilOp a ) {
            a.setState( this );
        }

        @Override
        void setState( StencilOp a ) {
            mFrontStencilFail = a.mFrontStencilFail;
            mFrontDepthFail = a.mFrontDepthFail;
            mFrontPass = a.mFrontPass;
            mBackStencilFail = a.mBackStencilFail;
            mBackDepthFail = a.mBackDepthFail;
            mBackPass = a.mBackPass;
        }
    }


    class Texture extends Stack<int[]> {

        public final int mTarget;
        public       int mId;

        private final DrawEnv mEnv;
        private final int[] mWork = { 0 };


        Texture( DrawEnv d, int target ) {
            mEnv = d;
            mTarget = target;
        }


        public int gen() {
            GLES30.glGenTextures( 1, mWork, 0 );
            return mWork[0];
        }


        public void delete( int id ) {
            mWork[0] = id;
            GLES30.glDeleteTextures( 1, mWork, 0 );
        }


        public void bind( int textureObjectId ) {
            mId = textureObjectId;
            apply();
        }

        @Override
        public void apply() {
            GLES30.glBindTexture( mTarget, mId );
        }


        @Override
        int[] alloc() {
            return new int[1];
        }

        @Override
        void getState( int[] out ) {
            out[0] = mId;
        }

        @Override
        void setState( int[] item ) {
            mId = item[0];
        }
    }


    class Viewport extends Stack<Viewport> {
        public int mX = 0;
        public int mY = 0;
        public int mW = 1;
        public int mH = 1;

        private final DrawEnv mG;


        public Viewport( DrawEnv g ) {
            super( DEFAULT_CAP );
            mG = g;
        }


        private Viewport() {
            mG = null;
        }


        public void apply( int x, int y, int w, int h ) {
            mX = x;
            mY = y;
            mW = w;
            mH = h;
            apply();
        }


        public void get( Rect out ) {
            out.x0 = mX;
            out.y0 = mY;
            out.x1 = mX + mW;
            out.y1 = mY + mH;
        }


        @Override
        public void apply() {
            GLES30.glViewport( mX, mY, mW, mH );
        }

        @Override
        Viewport alloc() {
            return new Viewport();
        }

        @Override
        void getState( Viewport out ) {
            out.setState( this );
        }

        @Override
        void setState( Viewport a ) {
            mX = a.mX;
            mY = a.mY;
            mW = a.mW;
            mH = a.mH;
        }

    }


    abstract class Stack<T> implements DrawSetting {

        protected static final int DEFAULT_CAP = 8;

        T[] mArr;
        int mPos;

        Stack() {
            mArr = null;
            mPos = 0;
        }

        @SuppressWarnings( "unchecked" )
        Stack( int initialCapacity ) {
            T t = alloc();
            mArr = (T[])java.lang.reflect.Array.newInstance( t.getClass(), initialCapacity );
            mArr[0] = t;
            for( int i = 1; i < mArr.length; i++ ) {
                mArr[i] = alloc();
            }
        }


        public void push() {
            ensureCapacity( mPos + 1 );
            getState( mArr[mPos++] );
        }


        public void pop() {
            setState( mArr[--mPos] );
            apply();
        }


        public int stackDepth() {
            return mPos;
        }


        abstract T alloc();


        abstract void getState( T out );


        abstract void setState( T item );

        @SuppressWarnings( "unchecked" )
        void ensureCapacity( int minCap ) {
            if( minCap <= mArr.length ) {
                return;
            }

            final int oldCap = mArr.length;
            int newCap = (oldCap * 3) / 2 + 1;
            if( newCap < minCap ) {
                newCap = minCap;
            }
            mArr = Arrays.copyOf( mArr, newCap );
            for( int i = oldCap; i < newCap; i++ ) {
                mArr[i] = alloc();
            }
        }
    }

}
