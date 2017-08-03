package wang.relish.colorpicker.sample;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void f() {
        String a = "4", b = "6", c = "9", d = "2";
        System.out.println(String.format(Locale.ENGLISH, "#%02d%02d%02d%02d", a, b, c, d));
    }
}