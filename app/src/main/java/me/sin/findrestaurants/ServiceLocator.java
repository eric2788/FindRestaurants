package me.sin.findrestaurants;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public final class ServiceLocator {

    private final static Map<Class<?>, Object> instanceMap = new ConcurrentHashMap<>();

    public static <T> T getService(Class<T> cls) {
        if (instanceMap.containsKey(cls)) return (T) instanceMap.get(cls);
        T ins = createInstance(cls);
        instanceMap.put(cls, ins);
        return ins;
    }


    private static <T> T createInstance(Class<T> cls) {
        if (cls.getConstructors().length != 1)
            throw new IllegalStateException("you should only have only one constructor in pre-create instance: "+cls);
        Class<?>[] constructors = cls.getConstructors()[0].getParameterTypes();
        Object[] constructorParameters = new Object[constructors.length];
        for (int i = 0; i < constructors.length; i++) {
            Object ins = getService(constructors[i]);
            constructorParameters[i] = ins;
        }
        try {
            return (T) cls.getConstructors()[0].newInstance(constructorParameters);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalStateException("Cannot initialize instance ".concat(cls.getName()), e);
        }
    }

    public static <T> void registerInstance(Class<T> cls, T object){
        instanceMap.put(cls, object);
    }
}
