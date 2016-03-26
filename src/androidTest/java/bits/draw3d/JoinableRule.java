package bits.draw3d;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;


/**
 * @author Philip DeCamp
 */
public class JoinableRule<T extends Activity> extends ActivityTestRule<T> {

    JoinableRule( Class<T> clazz, boolean touchMode, boolean launch ) {
        super( clazz, touchMode, launch );
        Activity act = getActivity();
    }


    public boolean join() throws InterruptedException {
        return join( -1 );
    }


    public synchronized boolean join( long timeout ) throws InterruptedException {
        Activity act = getActivity();
        long start = System.currentTimeMillis();

        while( !act.isFinishing() ) {
            if( timeout >= 0 && System.currentTimeMillis() >= start + timeout ) {
                return false;
            }
            wait( 50L );
        }

        return true;
    }

}
