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


