package bits.draw3d;

import android.app.Activity;
import android.content.res.Resources;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import bits.math3d.Vec4;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;

import static android.opengl.GLES30.*;

/**
 * @author Philip DeCamp
 */
@RunWith( AndroidJUnit4.class )
@LargeTest
public class DrawTest {

    private static final String TAG = DrawTest.class.getSimpleName();
    private static final String GLVER = "300 es";

    public Resources TEST_RES;

    @Rule
    public JoinableRule<Activity> mRule = new JoinableRule<>( Activity.class, true, true );

    @Before
    public void setUp() throws Exception {
        TEST_RES = InstrumentationRegistry.getTargetContext().getResources();
    }

    @Test
    public void createSurface() throws Throwable {
        mRule.getActivity().runOnUiThread( new Runnable() {
            public void run() {
                Activity act = mRule.getActivity();
                GLSurfaceView surface = new GLSurfaceView( act );
                new DrawTestRenderer( surface );
                act.setContentView( surface );
            }
        } );
        mRule.join( 3600000L );
    }


    class DrawTestRenderer implements GLSurfaceView.Renderer {

        final GLSurfaceView mSurface;
        Random mRand = new Random();
        AutoloadProgram mProg;
        Vao mVao;
        final DrawEnv d;

        DrawTestRenderer( GLSurfaceView surface ) {
            mSurface = surface;
            mSurface.setEGLContextClientVersion( 3 );
            mSurface.setRenderer( this );
            mSurface.setRenderMode( GLSurfaceView.RENDERMODE_CONTINUOUSLY );
            d = new DrawEnv( new ShaderManager( surface.getResources(), GLVER ) );
        }

        @Override
        public void onSurfaceCreated( GL10 ignore, EGLConfig config ) {
            d.init( mSurface, null );

            mProg = new AutoloadProgram();
            mProg.addShader(
                    d.mShaderMan.loadResource(
                            TEST_RES,
                            GL_VERTEX_SHADER,
                            "glsl/bits/draw3d/TestRenderTri.vert",
                            GLVER
                    )
            );
            mProg.addShader(
                    d.mShaderMan.loadResource(
                            TEST_RES,
                            GL_FRAGMENT_SHADER,
                            "glsl/bits/draw3d/TestRenderTri.frag",
                            GLVER
                    )
            );
            mProg.init( d );
            mProg.bind( d );

            for( Uniform uni: mProg.uniformsRef() ) {
                System.out.println(
                        uni.mName + "\t" +
                        MemberType.fromGl( uni.mMemberType ) +
                        " x " + uni.mArrayLength
                );
            }

            List<Uniform> uniforms = Shaders.listUniforms( mProg.id() );
            List<UniformBlock> blocks = Shaders.listUniformBlocks( mProg.id(), uniforms );

            for( UniformBlock res: blocks ) {
                System.out.println( res.mName + "\t" + res.mLocation + "\t" + res.mDataSize );
                for( Uniform uni: res.mUniforms ) {
                    System.out.format(
                            "    %s\t%s x %d",
                            uni.mName,
                            MemberType.fromGl( uni.mMemberType ),
                            uni.mArrayLength
                    );
                }
            }

            System.out.println( "###" );

            d.checkErr();
            int loc = GLES30.glGetUniformLocation( mProg.id(), "Fog.color" );
            System.out.println( loc );

            mVao = new Vao( Bo.createArrayBuffer( GL_STATIC_DRAW ), null );
            mVao.addAttribute( 0, 3, GL_FLOAT, false, 12 + 16,  0 );
            mVao.addAttribute( 1, 4, GL_FLOAT, false, 12 + 16, 12 );
            ByteBuffer bb = DrawUtil.alloc( 3 * 12 + 3 * 16 );
            float[] data = {
                    0.5f,    0, 0, 1, 0, 0, 1,
                       0, 0.5f, 0, 0, 1, 0, 1,
                       0,    0, 0, 0, 0, 1, 1
            };
            bb.asFloatBuffer().put( data );
            mVao.vbo().buffer( bb );
        }

        @Override
        public void onSurfaceChanged( GL10 gl, int width, int height ) {}

        @Override
        public void onDrawFrame( GL10 ignore ) {
            d.init( mSurface, null );

            GLES30.glClearColor( 0.2f, 0.18f, 0.18f, 0f );
            GLES30.glClear( GL_COLOR_BUFFER_BIT );

            d.mView.identity();
            d.mView.translate( 0f, 0f, -1f );
            d.mProj.setOrtho( -1f, 1f, -1f, 1f, 0f, 2f );

            Vec4 fog = new Vec4( mRand.nextFloat(), mRand.nextFloat(), mRand.nextFloat(), 1f );
            d.mFog.apply( fog, 0.6f, 0.9f );
            d.checkErr();

            mProg.bind( d );
            mVao.bind( d );
            GLES30.glDrawArrays( GL_TRIANGLES, 0, 3 );
        }
    }

}