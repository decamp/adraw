/*
 * Copyright (c) 2014. Massachusetts Institute of Technology
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.draw3d;

/**
 * Interface for objects that act as textures, FBOs, or RBOs.
 * <p>
 * The Texture interface provides calls for using the
 * object directly as a texture, FBO, or RBO. Additionally,
 * it extends DrawNode and may be used directly within a
 * scene graph if desired. When used as a DrawNode, the
 * primary pushDraw() behavior binds the Texture to the
 * current Framebuffer, and the primary popDraw() behavior
 * reverts the bindLocation to the previous state.
 * 
 * @author decamp
 */
public interface Texture extends DrawUnit, ReshapeListener {

    /**
     * @return Examples: GL_TEXTURE_2D, GL_TEXTURE_3D, GL_RENDERBUFFER, etc.
     */
    int target();
    
    /**
     * @return the current ID of this object, or 0 if not initialized.
     */
    int id();
    
    /**
     * Internal format value used for glTexImage* commands.  
     * 
     * @return Examples: GL_RGBA, GL_DEPTH_COMPONENT16, etc.
     */
    int internalFormat();
    
    /**
     * @return format of samples in main memory.
     */
    int format();
    
    /**
     * @return datatype of components in main memory.
     */
    int dataType();
    
    /**
     * Sets the size of the Texture buffer.  By default,
     * the size is -1,-1, meaning that the Texture does
     * not attempt to allocate storage for itself.  Changing
     * this value causes this Texture to allocate storage on
     * the next pushDraw(), bind(), or init() event.
     *  
     * @param w  Width of buffer, in pixels.
     * @param h  Height of buffer, in pixels.
     */
    void size( int w, int h );
    
    /**
     * @return currently defined width of underlying buffer, or -1 if undefined.
     */
    int width();
    
    /**
     * @return currently defined height of underlying buffer, or -1 if undefined. 
     */
    int height();
    
    /**
     * @return true if this Texture has a defined size; {@code width() >= 0 && height() >= 0}.
     */
    boolean hasSize();
    
    /**
     * Specifies whether the Texture should automatically allocate storage
     * to match the size of its context.  If enabled, setSize(w,h) will
     * be called on each call to reshape(gld, x, y, w, h).
     * <p>  
     * Default is {@code false}.
     */
    void resizeOnReshape( boolean resizeOnReshape );

    /**
     * @return true iff resizeOnReshape is enabled.
     * @see #resizeOnReshape
     */
    boolean resizeOnReshape();
    
    /**
     * (Optional method).  Sets the depth (number of image layers) for this Texture.
     * Only matters for 3D textures.  Calling this method may cause texture to 
     * reallocate storage.
     */
    void depth( int depth );

    /**
     * @return depth of this Texture.  Unless TEXTURE_3D, probably 1.
     */
    int depth();
    
    /**
     * @param key  Key of texture param 
     * @return currently specified value of that texture param, or {@code null} if not defined.
     */
    Integer param( int key );
    
    /**
     * Sets parameter textures, ala glTexParameter.
     */
    void param( int key, int value );

    /**
     * Sets the format of the Texture.  Calling this method may cause the
     * texture to reallocate storage if it has a defined size.  
     * 
     * @param internalFormat  Same as "internalFormat" param used in glTexImage* commands.
     * @param format          Same as "format" param used in glTexImage* commands.
     * @param dataType        Same as "dataType" param used in glTexImage* commands.
     */
    void format( int internalFormat, int format, int dataType );

    /**
     * Initializes the Texture. SHOULD be called automatically as
     * necessary by {@link #bind(bits.draw3d.DrawEnv)}.{@code init()} has the following behavior:<ul>
     * <li>If this Texture has no id, generates an id.</li>
     * <li>If this Texture has a defined size, allocates a buffer using the current format.</li>
     * </ul>
     */
    void init( DrawEnv g );

    /**
     * Disposes this Texture's resources.
     */
    void dispose( DrawEnv g );

    /**
     * Binds this Texture to it's designated target.
     */
    void bind( DrawEnv g );

    /**
     * Binds this Texture's designated target to 0.
     */
    void unbind( DrawEnv g );

    /**
     * Makes specified texture unit active and binds this texture to that unit.
     *
     * @param unit Number of texture unit. 0 is TEX_UNIT0.
     */
    void bind( DrawEnv d, int unit );

    /**
     * Makes specified texture unit active and unbinds this texture from that unit.
     *
     * @param unit Number of texture unit. 0 is TEX_UNIT0.
     */
    void unbind( DrawEnv g, int unit );

    /**
     * If autoResizeOnReshape(), produces a call to <tt>size(w, h)</tt>.
     */
    void reshape( DrawEnv g );

}
