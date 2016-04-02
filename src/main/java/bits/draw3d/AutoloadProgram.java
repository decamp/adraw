/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

import android.opengl.GLES30;

import java.util.*;


/**
 * @author Philip DeCamp
 */
public class AutoloadProgram extends Program {

    private boolean mCreateUniformLoadersOnInit = true;
    private boolean mConfigBlockBindingsOnInit  = true;

    protected List<ProgramResource> mAttribs;
    protected List<Uniform>         mUniforms;
    protected List<UniformBlock>    mBlocks;


    private List<DrawTask> mOnBind = null;


    public AutoloadProgram() {}


    public boolean createUniformLoadersOnInit() {
        return mCreateUniformLoadersOnInit;
    }

    /**
     * @param enable If true, this program will automatically configure itself during initialization
     *               to automatically load common uniforms.
     * @see Uniforms#addAvailableLoaders
     */
    public void createUniformLoadersOnInit( boolean enable ) {
        mCreateUniformLoadersOnInit = enable;
    }


    public boolean configBlockBindingsOnInit() {
        return mConfigBlockBindingsOnInit;
    }

    /**
     * @param enable If true, this program will automatically configure itself during initialization
     *               to automatically load common uniforms.
     * @see Uniforms#addAvailableLoaders
     */
    public void configBlockBindingsOnInit( boolean enable ) {
        mConfigBlockBindingsOnInit = enable;
    }



    /**
     * Not available until initialized.
     * @return direct reference to list of all attributes.
     */
    public List<ProgramResource> attribsRef() {
        return mAttribs;
    }

    /**
     * Not available until initialized.
     * @return direct reference to list of all uniforms.
     */
    public List<Uniform> uniformsRef() {
        return mUniforms;
    }

    /**
     * Not available until initialized.
     * @return direct reference to list of all uniform blocks.
     */
    public List<UniformBlock> uniformBlocksRef() {
        return mBlocks;
    }

    /**
     * Not available until initialized. Returns uniform with specified name.
     * @return uniform with given name, or {@code null} if not exists.
     */
    public Uniform uniform( String name ) {
        for( Uniform u: mUniforms ) {
            if( name.equals( u.mName ) ) {
                return u;
            }
        }
        return null;
    }




    public void addBindTask( DrawTask task ) {
        if( mOnBind == null ) {
            mOnBind = new ArrayList<>( 6 );
        }
        mOnBind.add( task );
    }


    @Override
    public void init( DrawEnv d ) {
        super.init( d );
        mAttribs  = Shaders.listAttributes( mId );
        mUniforms = Shaders.listUniforms( mId );
        mBlocks   = Shaders.listUniformBlocks( mId, mUniforms );

        // Remove uniforms in blocks.
        Iterator<Uniform> iter = mUniforms.iterator();
        while( iter.hasNext() ) {
            if( iter.next().mBlockIndex >= 0 ) {
                iter.remove();
            }
        }

        d.checkErr();

        if( mCreateUniformLoadersOnInit ) {
            Uniforms.addAvailableLoaders( this );
            bind( d );
        }

        Uniforms.setDefaultBlockBindings( d, mId, mBlocks );
        Uniforms.setDefaultTexUnits( d, mUniforms );
        unbind( d );
        d.checkErr();
    }

    @Override
    public void bind( DrawEnv d ) {
        super.bind( d );
        List<DrawTask> list = mOnBind;
        if( list == null ) {
            return;
        }
        final int len = list.size();
        for( int i = 0; i < len; i++ ) {
            list.get( i ).run( d );
        }
    }


    public void unbind( DrawEnv d ) {
        GLES30.glUseProgram( 0 );
    }

}
