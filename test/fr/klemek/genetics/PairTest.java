package fr.klemek.genetics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PairTest {

    @Test
    public void equals() {
        assertEquals(new Pair<>(0, 1), new Pair<>(0, 1));
        assertEquals(new Pair<>(0, 1), new Pair<>(1, 0));
        assertNotEquals(new Pair<>(0, 1), new Pair<>(0, 0));
    }
}