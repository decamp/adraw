/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

import android.opengl.GLES30;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Philip DeCamp
 */
public class Program implements DrawUnit {

    protected final List<Shader> mShaders = new ArrayList<>( 3 );
    protected       int          mId      = 0;


    public Program() {}


    public int id() {
        return mId;
    }


    public void addShader( Shader shader ) {
        mShaders.add( shader );
    }


    public void init( DrawEnv d ) {
        mId = GLES30.glCreateProgram();
        for( Shader s : mShaders ) {
            s.init( d );
            GLES30.glAttachShader( mId, s.id() );
        }
        GLES30.glLinkProgram( mId );
        d.checkErr();
    }


    public void dispose( DrawEnv d ) {
        if( mId != 0 ) {
            GLES30.glDeleteProgram( mId );
            mId = 0;
        }
    }


    public void bind( DrawEnv d ) {
        if( mId == 0 ) {
            init( d );
        }
        GLES30.glUseProgram( mId );
    }


    public void unbind( DrawEnv d ) {
        GLES30.glUseProgram( 0 );
    }

}
