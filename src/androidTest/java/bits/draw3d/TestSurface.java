package bits.draw3d;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * @author Philip DeCamp
 */
public class TestSurface extends GLSurfaceView implements GLSurfaceView.Renderer {

    private static final String TAG = TestSurface.class.getSimpleName();

    private DrawEnv mEnv;


    public TestSurface( Context context ) {
        super( context );
        init();
    }


    public TestSurface( Context context, AttributeSet attrs ) {
        super( context, attrs );
        init();
    }


    private void init() {
        setEGLContextClientVersion( 3 );
        setRenderer( this );
//        setOnTouchListener( new TouchHandler() );
    }


    @Override
    public void onSurfaceCreated( GL10 ignore, EGLConfig config ) {
//        GLES30.glClearColor( 0.5f, 0.15f, 1.0f, 1f );
//
//        List<DrawTri> tris = DrawSphere.genSphere( 0.7f, 70, 120 );
//        DrawSphere.trisToVao( tris, mBos );
//        DrawUtil.throwErr();
//
//        final String basePath = Classes.classToPackagePath( getClass() );
//        final String vertPath = basePath + "/Blob.vert";
//        final String fragPath = basePath + "/Blob.frag";
//        String fragSource;
//        String vertSource;
//
//        try( InputStream in = getContext().getAssets().open( vertPath, AssetManager.ACCESS_STREAMING ) ) {
//            vertSource = Streams.readString( in );
//            vertSource = vertSource.replace( "#version 330", "#version 300 es" );
//        } catch( IOException e ) {
//            Log.e( TAG, "Failed to readString vertex shader source.", e );
//            throw new LinkageError( vertPath );
//        }
//
//        try( InputStream in = getContext().getAssets().open( fragPath, AssetManager.ACCESS_STREAMING ) ) {
//            fragSource = Streams.readString( in );
//            fragSource = fragSource.replace( "#version 330", "#version 300 es" );
//        } catch( IOException e ) {
//            Log.e( TAG, "Failed to readString fragment shader source.", e );
//            throw new LinkageError( fragPath );
//        }
//
//        mProg = GLES30.glCreateProgram();
//        DrawUtil.throwErr();
//        int[] tmp = { 0 };
//
//        for( int i = 0; i < 2; i++ ) {
//            String src;
//            int id;
//
//            if( i == 0 ) {
//                id = GLES30.glCreateShader( GLES30.GL_VERTEX_SHADER );
//                src = vertSource;
//            } else {
//                id = GLES30.glCreateShader( GLES30.GL_FRAGMENT_SHADER );
//                src = fragSource;
//            }
//
//            GLES30.glShaderSource( id, src );
//            GLES30.glCompileShader( id );
//            GLES30.glGetShaderiv( id, GL_COMPILE_STATUS, tmp, 0 );
//
//            if( tmp[0] == GL_FALSE ) {
//                String msg = GLES30.glGetShaderInfoLog( id );
//                msg = "Shader Compilation Failed: " + msg;
//                GLES30.glDeleteShader( id );
//                throw new GLException( tmp[0], msg );
//            }
//
//            GLES30.glAttachShader( mProg, id );
//        }
//
//        GLES30.glLinkProgram( mProg );
//        DrawUtil.throwErr();
//
//        mViewLoc = GLES30.glGetUniformLocation( mProg, "VIEW_MAT" );
//        mProjViewLoc = GLES30.glGetUniformLocation( mProg, "PROJ_VIEW_MAT" );
//        mNormLoc = GLES30.glGetUniformLocation( mProg, "NORM_MAT" );
//        mTimeLoc = GLES30.glGetUniformLocation( mProg, "TIME" );
//        mNoiseMagLoc = GLES30.glGetUniformLocation( mProg, "NOISE_MAG" );
//        mWaveAxesLoc = GLES30.glGetUniformLocation( mProg, "WAVE_AXES" );
//        mWaveParamsLoc = GLES30.glGetUniformLocation( mProg, "WAVE_PARAMS" );
//
//        mVerts.clear();
//        mVerts.putFloat( 0 ).putFloat( 0 ).putFloat( 0 );
//        mVerts.putFloat( 1 ).putFloat( 0 ).putFloat( 0 );
//        mVerts.putFloat( 0 ).putFloat( 1 ).putFloat( 0 );
//        mVerts.flip();
    }

    @Override
    public void onSurfaceChanged( GL10 ggl, int width, int height ) {
//        mWidth = width;
//        mHeight = height;
//        float dim = Math.min( mWidth, mHeight );
//
//        float left = -mWidth / dim;
//        float right = -left;
//        float bottom = -mHeight / dim;
//        float top = -bottom;
//        float near = -4f;
//        float far = -near;
//
//        mProjView[ 0] = 2f / (right - left);
//        mProjView[ 1] = 0;
//        mProjView[ 2] = 0;
//        mProjView[ 3] = 0;
//        mProjView[ 4] = 0;
//        mProjView[ 5] = 2f / (top - bottom);
//        mProjView[ 6] = 0;
//        mProjView[ 7] = 0;
//        mProjView[ 8] = 0;
//        mProjView[ 9] = 0;
//        mProjView[10] = -2f / (far - near);
//        mProjView[11] = 0;
//        mProjView[12] = -(right + left) / (right - left);
//        mProjView[13] = -(top + bottom) / (top - bottom);
//        mProjView[14] = -(far + near) / (far - near);
//        mProjView[15] = 1;
    }

    @Override
    public void onDrawFrame( GL10 gl ) {
        GLES30.glClearColor( 0f, 0f, 0f, 0f );
        GLES30.glClear( GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT );
        mEnv.init( this, null );




//        float time = (float)( System.currentTimeMillis() % 3600000 / 1000.0 ) * 0.7f;
//        mWave.update( time );
//
//        GLES30.glEnable( GL_CULL_FACE );
//        GLES30.glEnable( GL_DEPTH_TEST );
//        GLES30.glUseProgram( mProg );
//        GLES30.glUniformMatrix4fv( mViewLoc, 1, false, mView, 0 );
//        GLES30.glUniformMatrix4fv( mProjViewLoc, 1, false, mProjView, 0 );
//        GLES30.glUniformMatrix3fv( mNormLoc, 1, false, mNorm, 0 );
//        GLES30.glUniform3fv( mWaveAxesLoc, 3, mWave.mAxis, 0 );
//        GLES30.glUniform3fv( mWaveParamsLoc, 3, mWave.mParams, 0 );
//        GLES30.glUniform1f( mTimeLoc, time );
//
//        float noiseMag = Math.max( 0, MAG_SUM - mWave.magSum() ) / MAG_SUM;
//        GLES30.glUniform1f( mNoiseMagLoc, noiseMag );
//
//        GLES30.glBindVertexArray( mBos.mVao );
//        GLES30.glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, mBos.mIbo );
//        GLES30.glDrawElements( GL_TRIANGLES, mBos.mIndLen, GL_UNSIGNED_INT, 0 );
//
//        GLES30.glBindBuffer( GL_ARRAY_BUFFER, 0 );
//        GLES30.glBindVertexArray( 0 );
//
//        GLES30.glUseProgram( 0 );
    }

}

