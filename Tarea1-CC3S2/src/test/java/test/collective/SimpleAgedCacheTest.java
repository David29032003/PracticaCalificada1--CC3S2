package test.collective;

import io.collective.SimpleAgedCache;
import io.collective.TestClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import static org.junit.jupiter.api.Assertions.*;

public class SimpleAgedCacheTest {
    TestClock clock = new TestClock();
    SimpleAgedCache empty = new SimpleAgedCache(clock);
    SimpleAgedCache nonempty = new SimpleAgedCache(clock);

    @BeforeEach
    public void before() {
        nonempty.put("aKey", "aValue", 2000);
        nonempty.put("anotherKey", "anotherValue", 4000);
    }

    @Test
    public void isEmpty() {
        assertTrue(empty.isEmpty());
        assertFalse(nonempty.isEmpty());
    }

    @Test
    public void size() {
        assertEquals(0, empty.size());
        assertEquals(2, nonempty.size());
    }

    @Test
    public void get() {
        assertNull(empty.get("aKey"));
        assertEquals("aValue", nonempty.get("aKey"));
        assertEquals("anotherValue", nonempty.get("anotherKey"));
    }

    @Test
    public void getExpired() {
        // Arrange
        TestClock clock = new TestClock();
        SimpleAgedCache expired = new SimpleAgedCache(clock);

        clock.offset(Duration.ofMillis(3000));

        // Act
        expired.put("aKey", "aValue", 2000);
        expired.put("anotherKey", "anotherValue", 4000);

        // Avanza el tiempo simulado
        clock.offset(Duration.ofMillis(3000));

        //Asert
        assertNull(expired.get("aKey")); // El elemento "aKey" debe haber expirado
        // Comprueba si los valores expirados son manejados correctamente
        assertEquals(1, expired.size());
        assertEquals("anotherValue", expired.get("anotherKey"));
    }

    static class TestClock extends Clock {
        Duration offset = Duration.ZERO;

        @Override
        public ZoneId getZone() {
            return Clock.systemDefaultZone().getZone();
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return Clock.offset(Clock.system(zone), offset);
        }

        @Override
        public Instant instant() {
            return Clock.offset(Clock.systemDefaultZone(), offset).instant();
        }

        public void offset(Duration offset) {
            this.offset = offset;
        }
    }
}
