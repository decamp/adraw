/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

import android.content.res.AssetManager;
import android.content.res.Resources;
import bits.draw3d.util.Streams;

import java.io.*;


/**
 * @author Philip DeCamp
 */
public class ShaderManager {

    private Resources mOptRes;
    private String mOptVersionOverride;


    public ShaderManager( Resources optResources, String optVersionOverride ) {
        mOptRes = optResources;
        mOptVersionOverride = optVersionOverride;
    }


    public Resources defaultResources() {
        return mOptRes;
    }


    public String defaultVersionOverride() {
        return mOptVersionOverride;
    }


    public Shader loadSource( int type, String source ) {
        return loadSource( type, source, mOptVersionOverride );
    }


    public Shader loadSource( int type, String source, String optVersionOverride ) {
        if( optVersionOverride != null ) {
            source = source.replaceFirst( "^#version\\s.*", "#version " + optVersionOverride );
        }
        return new Shader( type, source );
    }


    public Shader loadResource( int type, String resourcePath ) {
        return loadResource( mOptRes, type, resourcePath, mOptVersionOverride );
    }


    public Shader loadResource( Resources res, int type, String resourcePath, String optVersionOverride ) {
        String source = null;

        if( res != null ) {
            try( InputStream open = res.getAssets().open( resourcePath, AssetManager.ACCESS_STREAMING ) ) {
                source = Streams.readString( open );
            } catch( IOException ignore ) {}
        }

        if( source == null ) {
            throw new LinkageError( "Could not find shader: " + resourcePath );
        }

        return loadSource( type, source, optVersionOverride );
    }


    public Shader loadFile( int type, File file ) {
        try {
            String s = Streams.readString( file );
            return loadSource( type, s );
        } catch( IOException e ) {
            throw new LinkageError( "Could not run shader: " + file.getPath() );
        }
    }

}
