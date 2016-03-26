/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

import android.opengl.GLES30;
import android.opengl.GLException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES30.*;


/**
 * @author Philip DeCamp
 */
public class Shaders {

    /**
     * Convenience method that creates a shader, attaches source, compiles, and checks for errors.
     *
     * @param shaderType  GL_VERTEX_SHADER, GL_FRAGMENT_SHADER, or GL_GEOMETRY_SHADER
     * @param source      Source GLSL code.
     * @return id of newly created shader.
     * @throws GLException if shader creation fails.
     */
    public static int compile( int shaderType, String source ) throws GLException {
        int id = GLES30.glCreateShader( shaderType );
        GLES30.glShaderSource( id, source );
        GLES30.glCompileShader( id );

        int[] arr = new int[1];
        GLES30.glGetShaderiv( id, GL_COMPILE_STATUS, arr, 0 );
        if( arr[0] == 0 ) {
            String msg = GLES30.glGetShaderInfoLog( id );
            msg = "Shader Compilation Failed : " + msg;
            GLES30.glDeleteShader( id );
            throw new GLException( arr[0], msg );
        }
        return id;
    }


    public static List<ProgramResource> listAttributes( int program ) {
        int[] vals = { 0, 0, 0 };
        GLES30.glGetProgramiv( program, GL_ACTIVE_ATTRIBUTES,           vals, 0 );
        // GL_ACTIVE_ATTRIBUTE_MAX_LENGTH actually represents the max NAME length.
        GLES30.glGetProgramiv( program, GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, vals, 1 );

        final int num = vals[0];
        byte[] nameBytes = new byte[ vals[1] + 1 ];
        List<ProgramResource> ret = new ArrayList<>( num );

        for( int index = 0; index < num; index++ ) {
            GLES30.glGetActiveAttrib( program, index, nameBytes.length, vals, 0, vals, 1, vals, 2, nameBytes, 0 );
            String name = "";
            if( vals[0] > 0 ) {
                try {
                    name = new String( nameBytes, 0, vals[0], "UTF-8" );
                } catch( UnsupportedEncodingException e ) {
                    throw new RuntimeException( e );
                }
            }

            ret.add( new ProgramResource( Fake.GL_PROGRAM_INPUT, vals[2], vals[1], index, index, name ) );
        }

        return ret;
    }


    public static List<Uniform> listUniforms( int program ) {
        final int[] val = { 0 };
        GLES30.glGetProgramiv( program, GL_ACTIVE_UNIFORMS, val, 0 );
        final int num = val[0];
        int[] inds = new int[ num ];
        for( int i = 0; i < num; i++ ) {
            inds[i] = i;
        }

        List<Uniform> ret = initUniforms( program, inds, num );
        return ret;
    }


    public static List<UniformBlock> listUniformBlocks( int program, List<Uniform> progUniforms ) {
        int[] val = { 0 };
        GLES30.glGetProgramiv( program, GL_ACTIVE_UNIFORM_BLOCKS, val, 0 );
        final int numBlocks = val[0];
        GLES30.glGetProgramiv( program, GL_ACTIVE_UNIFORM_BLOCK_MAX_NAME_LENGTH, val, 0 );
        // GL_ACTIVE_UNIFORM_MAX_NAME_LENGTH may be buggy, so limit size of buffer.
        byte[] nameBytes = new byte[ Math.max( 256, Math.min( 2048, val[0] ) ) ];

        List<UniformBlock> ret = new ArrayList<>( numBlocks );

        for( int index = 0; index < numBlocks; index++ ) {
            GLES30.glGetActiveUniformBlockiv( program, index, GL_UNIFORM_BLOCK_BINDING, val, 0 );
            final int loc      = val[0];
            GLES30.glGetActiveUniformBlockiv( program, index, GL_UNIFORM_BLOCK_DATA_SIZE, val, 0 );
            final int dataSize = val[0];
            GLES30.glGetActiveUniformBlockiv( program, index, GL_UNIFORM_BLOCK_NAME_LENGTH, val, 0 );
            final int nameLen  = val[0];
            GLES30.glGetActiveUniformBlockiv( program, index, GL_UNIFORM_BLOCK_ACTIVE_UNIFORMS, val, 0 );
            final int childNum = val[0];

            int[] inds = new int[childNum];
            GLES30.glGetActiveUniformBlockiv( program, index, GL_UNIFORM_BLOCK_ACTIVE_UNIFORM_INDICES, inds, 0 );
            List<Uniform> unis = new ArrayList<>( childNum );
            for( int j = 0; j < childNum; j++ ) {
                unis.add( progUniforms.get( inds[j] ) );
            }

            String name = "";
            if( nameLen > 0 ) {
                try {
                    GLES30.glGetActiveUniformBlockName( program, index, nameBytes.length, val, 0, nameBytes, 0 );
                    name = new String( nameBytes, 0, val[0], "UTF-8" );
                } catch( UnsupportedEncodingException e ) {
                    throw new RuntimeException( e );
                }
            }

            ret.add( new UniformBlock( index, loc, name, dataSize, unis ) );
        }

        return ret;
    }


    private static List<Uniform> initUniforms( int prog, int[] inds, int num ) {
        final List<Uniform> ret = new ArrayList<>( num );
        if( num == 0 ) {
            return ret;
        }

        int[] vals = { 0, 0, 0 };
        GLES30.glGetProgramiv( prog, GL_ACTIVE_UNIFORM_MAX_LENGTH, vals, 0 );
        final byte[] nameBytes = new byte[ Math.max( 128, Math.min( 2048, vals[0] ) ) ];

        int[] arrayStrides  = new int[ num ];
        int[] matrixStrides = new int[ num ];
        int[] blockIndices  = new int[ num ];
        int[] blockOffsets  = new int[ num ];
        GLES30.glGetActiveUniformsiv( prog, num, inds, 0, GL_UNIFORM_ARRAY_STRIDE,  arrayStrides,  0 );
        GLES30.glGetActiveUniformsiv( prog, num, inds, 0, GL_UNIFORM_MATRIX_STRIDE, matrixStrides, 0 );
        GLES30.glGetActiveUniformsiv( prog, num, inds, 0, GL_UNIFORM_BLOCK_INDEX,   blockIndices,  0 );
        GLES30.glGetActiveUniformsiv( prog, num, inds, 0, GL_UNIFORM_OFFSET,        blockOffsets,  0 );

        for( int i = 0; i < num; i++ ) {
            GLES30.glGetActiveUniform( prog, inds[i], nameBytes.length, vals, 0, vals, 1, vals, 2, nameBytes, 0 );
            String name = "";
            if( vals[0] > 0 ) {
                try {
                    name = new String( nameBytes, 0, vals[0], "UTF-8" );
                } catch( UnsupportedEncodingException e ) {
                    throw new RuntimeException( e );
                }
            }

            int loc = GLES30.glGetUniformLocation( prog, name );
            ret.add( new Uniform( vals[2],
                                  vals[1],
                                  inds[i],
                                  loc,
                                  name,
                                  arrayStrides[i],
                                  matrixStrides[i],
                                  blockIndices[i],
                                  blockOffsets[i] ) );
        }

        return ret;
    }


    /**
     * Not necessary because Android's glGetShaderInfoLog auto converts to String.
     */
    @Deprecated
    public static String readShaderInfoLog( int shader ) {
        return GLES30.glGetShaderInfoLog( shader );
    }

}
