package bits.draw3d;

/**
 * @author Philip DeCamp
 */
public interface DrawUnit extends DrawResource {
    void bind( DrawEnv d );
    void unbind( DrawEnv d );
}
