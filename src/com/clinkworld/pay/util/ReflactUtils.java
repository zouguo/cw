package com.clinkworld.pay.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class ReflactUtils {

    public static Object reflactFiled(String className, String filedName) {
        Object result = null;
        try {
            result = Class.forName(className).getField(filedName).get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Method reflactMethod(String className, String methodName, Class[] paramClasses) throws ClassNotFoundException, NoSuchMethodException {
        return Class.forName(className).getMethod(methodName, paramClasses);
    }

    public static Method reflactMethodNoException(String className, String methodName, Class[] paramClasses) {
        Method method = null;
        try {
            method = reflactMethod(className, methodName, paramClasses);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }

    public static Object reflactInvoke(Object obj, Method method, Object[] params) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(obj, params);
    }

    public static Object reflactInvoke(Object obj, String methodName, Class[] paramClasses, Object[] params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method method = obj.getClass().getMethod(methodName, paramClasses);
        return method.invoke(obj, params);
    }

    public static Object reflactInvokeNoException(Object obj, String methodName, Class[] paramClasses, Object[] params) {
        Object result = null;
        try {
            result = reflactInvoke(obj, methodName, paramClasses, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
