package pers.lizechao.android_lib;

import android.content.Context;
import android.os.Handler;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Processor;

import io.reactivex.android.schedulers.AndroidSchedulers;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("pers.lizechao.android_lib", appContext.getPackageName());
    }

    public class IMediaSource<I> {

    }

    public abstract class MediaFlowDispatch<T extends IMediaSource<I>,I> {
        abstract public void register(I listener);
    }

    public class VideoMediaFlowDispatch extends MediaFlowDispatch<>
    {

    }



    private Processor processorFirst;

    void text() {

    }
}


