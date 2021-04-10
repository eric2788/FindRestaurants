package me.sin.findrestaurants.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class DataTransferService {

    private final Map<String, Object> dataMap = new ConcurrentHashMap<>();

    public void putData(String key, Object object) {
        this.dataMap.putIfAbsent(key, object);
    }

    public <T> T getData(String key) {
        return (T) this.dataMap.remove(key);
    }


}
