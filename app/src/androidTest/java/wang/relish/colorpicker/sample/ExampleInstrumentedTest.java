package wang.relish.colorpicker.sample;

import android.content.Context;
import android.graphics.Color;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("wang.relish.colorpicker.sample", appContext.getPackageName());
    }

    @Test
    public void color() {
        float[] hsv = new float[]{0, 0, 0};
        Color.colorToHSV(0xffffff, hsv);
        System.out.println(Arrays.toString(hsv));
    }
}
