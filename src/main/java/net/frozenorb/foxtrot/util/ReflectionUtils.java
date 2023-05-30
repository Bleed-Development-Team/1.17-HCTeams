package net.frozenorb.foxtrot.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {
    public static Field accessField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Method accessMethod(Class<?> clazz, String methodName, Class<?>... vars) {
        try {
            Method method = clazz.getMethod(methodName, vars);
            method.setAccessible(true);
            return method;
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }
}
