package fr.klemek.genetics;

import org.junit.Test;

import static org.junit.Assert.fail;

public class UtilsTest {

    @Test
    public void geoDistance() {
        float realDist = 793.8f;

        float distApp = Utils.geoDistance(new float[]{43.42f, 7.16f}, new float[]{49.26f, 1.05f}, true);

        assertGreater(realDist * 0.9f, distApp);
        assertLesser(realDist * 1.1f, distApp);

        float dist = Utils.geoDistance(new float[]{43.42f, 7.16f}, new float[]{49.26f, 1.05f}, false);

        assertGreater(realDist * 0.99f, dist);
        assertLesser(realDist * 1.01f, dist);
    }

    public void assertGreater(float ref, float value) {
        if (value <= ref)
            fail(String.format("%f is lesser than %f", value, ref));
    }

    public void assertLesser(float ref, float value) {
        if (value >= ref)
            fail(String.format("%f is greater than %f", value, ref));
    }
}