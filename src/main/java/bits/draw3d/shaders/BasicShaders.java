/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d.shaders;

import bits.draw3d.*;
import bits.draw3d.model.DrawTri;
import bits.draw3d.model.DrawVert;
import bits.draw3d.util.ValueTypes;
import bits.vec.Vec;
import bits.vec.Vec4;

import java.nio.ByteBuffer;

import static android.opengl.GLES30.*;


/**
 * @author Philip DeCamp
 */
public class BasicShaders {

    @SuppressWarnings( "unchecked" )
    public static BoProgram<DrawVert,DrawTri> createTriProgram(
            ShaderManager shaderMan,
            boolean strip,
            boolean adjacency,
            int texComponents,
            boolean norms,
            boolean colors
    ) {
        int mode = strip ? ( adjacency ? Fake.GL_TRIANGLE_STRIP_ADJACENCY : GL_TRIANGLE_STRIP ) :
                           ( adjacency ? Fake.GL_TRIANGLES_ADJACENCY      : GL_TRIANGLES      );

        BoProgram<DrawVert,DrawTri> ret = (BoProgram)createVertProgram(
                shaderMan,
                mode,
                texComponents,
                norms,
                colors
        );

        ret.mElemWriter = DRAW_TRI_WRITER;
        return ret;
    }


    public static BoProgram<DrawVert,Void> createVertProgram(
            ShaderManager shaderMan,
            int geomMode,
            int texComponents,
            boolean norms,
            boolean colors
    ) {
        BasicShaderConfig conf = new BasicShaderConfig();
        conf.geomMode( geomMode );
        conf.texComponentNum( texComponents );
        conf.normals( norms );
        conf.color( colors );
        BasicShaderConfig selected = new BasicShaderConfig();
        conf.chooseAvailable( selected );
        return createProgram( selected, shaderMan );
    }


    public static BoProgram<DrawVert,Void> createProgram( BasicShaderConfig config, ShaderManager shaderMan ) {
        BoProgram<DrawVert,Void> ret = new BoProgram<>();
        initProgram( config, shaderMan, ret );
        ret.mVertWriter = createVertWriter( config );
        return ret;
    }


    public static BoWriter<DrawVert> createVertWriter( BasicShaderConfig config ) {
        if( config.texComponentNum() == 0 ) {
            if( config.color() || !config.normals() ) {
                return new ColorWriter();
            } else {
                return new NormWriter();
            }
        } else {
            if( !config.normals() ) {
                if( !config.color() ) {
                    return new TexWriter( config.texComponentNum() );
                } else {
                    return new ColorTexWriter( config.texComponentNum() );
                }
            } else {
                if( !config.color() ) {
                    return new NormTexWriter( config.texComponentNum() );
                } else {
                    return new ColorNormTexWriter( config.texComponentNum() );
                }
            }
        }
    }




    private static void initProgram( BasicShaderConfig config, ShaderManager shaderMan, BoProgram<?,?> out ) {
        AutoloadProgram prog = new AutoloadProgram();
        prog.addShader( shaderMan.loadResource( GL_VERTEX_SHADER, config.vertShader() ) );
        if( config.geomShader() != null ) {
            prog.addShader( shaderMan.loadResource( Fake.GL_GEOMETRY_SHADER, config.geomShader() ) );
        }
        prog.addShader( shaderMan.loadResource( GL_FRAGMENT_SHADER, config.fragShader() ) );
        out.mProgram = prog;
    }


    private static abstract class AbstractVertWriter implements BoWriter<DrawVert> {
        public Class<DrawVert> itemClass() {
            return DrawVert.class;
        }

        public int boType() {
            return GL_ARRAY_BUFFER;
        }

        public int elemsPerItem() {
            return 1;
        }

        public int elemNum( DrawVert item ) {
            return 1;
        }

        public int markAdd( DrawVert d, int pos ) {
            if( d.mVboPos >= 0 ) {
                return -1;
            }
            d.mVboPos = pos;
            return 1;
        }

        public int markRemove( DrawVert d ) {
            if( d.mVboPos < 0 ) {
                return -1;
            }
            d.mVboPos = -1;
            return 1;
        }
    }


    private static final class TexWriter extends AbstractVertWriter {
        private final int mTexDim;

        TexWriter( int texNum ) {
            mTexDim = texNum;
        }


        @Override
        public int bytesPerElem() {
            return 12 + 4 * mTexDim;
        }

        @Override
        public void attributes( Vao out ) {
            int stride = bytesPerElem();
            out.addAttribute( 0, 3, GL_FLOAT, false, stride, 0 );
            out.addAttribute( 1, mTexDim, GL_FLOAT, false, stride, 12 );
        }

        @Override
        public void write( DrawVert vert, ByteBuffer bo ) {
            Vec.put( vert.mPos, bo );
            for( int i = 0; i < mTexDim; i++ ) {
                bo.putFloat( vert.mTex[i] );
            }
        }
    }


    private static final class ColorWriter extends AbstractVertWriter {
        @Override
        public int bytesPerElem() {
            return 12 + 4;
        }

        @Override
        public void attributes( Vao out ) {
            int stride = bytesPerElem();
            out.addAttribute( 0, 3, GL_FLOAT, false, stride, 0 );
            out.addAttribute( 1, 4, GL_UNSIGNED_BYTE, true, stride, 12 );
        }

        @Override
        public void write( DrawVert vert, ByteBuffer bo ) {
            Vec.put( vert.mPos, bo );
            Vec4 c = vert.mColor;
            bo.putInt( ValueTypes.toUbytes( c ) );
        }
    }


    private static final class ColorTexWriter extends AbstractVertWriter {
        private final int mTexDim;

        ColorTexWriter( int texDim ) {
            mTexDim = texDim;
        }

        @Override
        public int bytesPerElem() {
            return 12 + 4 * mTexDim + 4;
        }

        @Override
        public void attributes( Vao out ) {
            int stride = bytesPerElem();
            out.addAttribute( 0, 3, GL_FLOAT, false, stride, 0 );
            out.addAttribute( 1, 4, GL_UNSIGNED_BYTE, true, stride, 12 );
            out.addAttribute( 2, mTexDim, GL_FLOAT, false, stride, 12 + 4 );
        }

        @Override
        public void write( DrawVert vert, ByteBuffer bo ) {
            Vec.put( vert.mPos, bo );
            bo.putInt( ValueTypes.toUbytes( vert.mColor ) );
            for( int i = 0; i < mTexDim; i++ ) {
                bo.putFloat( vert.mTex[i] );
            }
        }
    }


    private static final class NormTexWriter extends AbstractVertWriter {
        private final int mTexDim;

        NormTexWriter( int texDim ) {
            mTexDim = texDim;
        }

        @Override
        public int bytesPerElem() {
            return 12 + 12 + 4 * mTexDim;
        }

        @Override
        public void attributes( Vao out ) {
            int stride = bytesPerElem();
            out.addAttribute( 0, 3,       GL_FLOAT, false, stride, 0       );
            out.addAttribute( 1, 3,       GL_FLOAT, false, stride, 12      );
            out.addAttribute( 2, mTexDim, GL_FLOAT, false, stride, 12 + 12 );
        }

        @Override
        public void write( DrawVert vert, ByteBuffer vbo ) {
            Vec.put( vert.mPos, vbo );
            Vec.put( vert.mNorm, vbo );
            for( int i = 0; i < mTexDim; i++ ) {
                vbo.putFloat( vert.mTex[i] );
            }
        }
    }


    private static final class NormWriter extends AbstractVertWriter {

        NormWriter() {}

        @Override
        public int bytesPerElem() {
            return 12 + 12;
        }

        @Override
        public void attributes( Vao out ) {
            int stride = bytesPerElem();
            out.addAttribute( 0, 3, GL_FLOAT, false, stride,  0 );
            out.addAttribute( 1, 3, GL_FLOAT, false, stride, 12 );
        }

        @Override
        public void write( DrawVert vert, ByteBuffer vbo ) {
            Vec.put( vert.mPos, vbo );
            Vec.put( vert.mNorm, vbo );
        }
    }


    private static final class ColorNormTexWriter extends AbstractVertWriter {
        private final int mTexDim;

        ColorNormTexWriter( int texDim ) {
            mTexDim = texDim;
        }

        @Override
        public int bytesPerElem() {
            return 12 + 4 * mTexDim + 12 + 4;
        }

        @Override
        public void attributes( Vao out ) {
            int stride = bytesPerElem();
            out.addAttribute( 0, 3, GL_FLOAT, false, stride, 0 );
            out.addAttribute( 1, 4,       GL_UNSIGNED_BYTE, true,  stride, 12          );
            out.addAttribute( 2, 3,       GL_FLOAT,         false, stride, 12 + 4      );
            out.addAttribute( 3, mTexDim, GL_FLOAT,         false, stride, 12 + 4 + 12 );
         }

        @Override
        public void write( DrawVert vert, ByteBuffer vbo ) {
            Vec.put( vert.mPos, vbo );
            vbo.putInt( ValueTypes.toUbytes( vert.mColor ) );
            Vec.put( vert.mNorm, vbo );
            for( int i = 0; i < mTexDim; i++ ) {
                vbo.putFloat( vert.mTex[i] );
            }
        }
    }


    public static final BoWriter<DrawTri> DRAW_TRI_WRITER = new BoWriter<DrawTri>() {
        @Override
        public Class<DrawTri> itemClass() {
            return DrawTri.class;
        }

        @Override
        public int boType() {
            return GL_ELEMENT_ARRAY_BUFFER;
        }

        @Override
        public int bytesPerElem() {
            return 4;
        }

        @Override
        public int elemsPerItem() {
            return 3;
        }

        @Override
        public int elemNum( DrawTri item ) {
            return 3;
        }

        @Override
        public void attributes( Vao out ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int markAdd( DrawTri item, int pos ) {
            if( item.mIboPos >= 0 ) {
                return -1;
            }
            item.mIboPos = pos;
            return 3;
        }

        @Override
        public void write( DrawTri item, ByteBuffer bo ) {
            DrawVert[] v = item.mVerts;
            bo.putInt( v[0].mVboPos );
            bo.putInt( v[1].mVboPos );
            bo.putInt( v[2].mVboPos );
        }

        @Override
        public int markRemove( DrawTri item ) {
            if( item.mIboPos < 0 ) {
                return -1;
            }
            item.mIboPos = -1;
            return 3;
        }
    };

}
