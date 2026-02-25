import dev.rotator.pvplabs.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PairTest {

    @Test
    public void testEquality() {
        Pair<String, Integer> p1 = new Pair<>("Hello", 5);
        Pair<String, Integer> p2 = new Pair<>("Hello", 5);

        assertEquals(p1, p2, "Pairs with same values should be equal");
    }

    @Test
    public void testInequality_FirstDifferent() {
        Pair<String, Integer> p1 = new Pair<>("A", 1);
        Pair<String, Integer> p2 = new Pair<>("B", 1);

        assertNotEquals(p1, p2, "Pairs different in first element should not be equal");
    }

    @Test
    public void testInequality_SecondDifferent() {
        Pair<String, Integer> p1 = new Pair<>("A", 1);
        Pair<String, Integer> p2 = new Pair<>("A", 2);

        assertNotEquals(p1, p2, "Pairs different in second element should not be equal");
    }

    @Test
    public void testHashCode() {
        Pair<String, Integer> p1 = new Pair<>("X", 42);
        Pair<String, Integer> p2 = new Pair<>("X", 42);

        assertEquals(
                p1.hashCode(),
                p2.hashCode(),
                "Equal pairs must produce equal hash codes"
        );
    }

    @Test
    public void testMapKeyFunctionality() {
        Map<Pair<String, String>, String> map = new HashMap<>();

        Pair<String, String> key = new Pair<>("A", "B");
        map.put(key, "StoredValue");

        Pair<String, String> lookup = new Pair<>("A", "B");

        assertTrue(map.containsKey(lookup), "Map should recognize logically equal Pair key");
        assertEquals("StoredValue", map.get(lookup), "Map lookup using equal Pair should return stored value");
    }
}
