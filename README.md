Pregunta 1 de la Pc1-Desarrollo de Software

Para la clase SimpleAgedCache le hice los siguientes cambios: 
package io.collective;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

//Creo una clase que implementa una caché simple con expiración basada en tiempo.

public class SimpleAgedCache {
    private Clock clock;
    private Map<String, CacheEntry> cacheMap;

    //Constructor de la clase SimpleAgedCache.
    public SimpleAgedCache(Clock clock) {
        this.clock = clock;
        this.cacheMap = new HashMap<>();
    }

    // Agrega un valor a la caché con una clave y un tiempo de vida (TTL) en milisegundos.
    public void put(String key, String value, long ttlMillis) {
        Instant expiryTime = clock.instant().plusMillis(ttlMillis);
        cacheMap.put(key, new CacheEntry(value, expiryTime));
    }

    // Verifica si la caché está vacía.
    public boolean isEmpty() {
        return cacheMap.isEmpty();
    }

    // Obtiene el tamaño actual de la caché.
    public int size() {
        return cacheMap.size();
    }

    // Obtiene el valor asociado con la clave especificada.
    public String get(String key) {
        CacheEntry entry = cacheMap.get(key);
        if (entry != null && !entry.isExpired(clock.instant())) {
            return entry.getValue();
        } else {
            // Si el elemento está presente pero ha expirado, eliminarlo de la caché
            if (entry != null) {
                cacheMap.remove(key);
            }
            return null;
        }
    }

    //Clase interna que representa una entrada en la caché.
    private static class CacheEntry {
        private String value;
        private Instant expiryTime;

        // Constructor de la clase CacheEntry.
        public CacheEntry(String value, Instant expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        //Obtiene el valor de la entrada de la caché.
        public String getValue() {
            return value;
        }

        //Verifica si la entrada de la caché ha expirado en comparación con el tiempo actual.
        public boolean isExpired(Instant currentTime) {
            return currentTime.isAfter(expiryTime);
        }
    }
}

Para la clase SimpleAgedTest le hice los siguientes cambios:
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

