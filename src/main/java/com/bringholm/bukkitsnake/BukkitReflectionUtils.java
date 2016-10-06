package com.bringholm.bukkitsnake;

import org.bukkit.Bukkit;

import java.lang.reflect.*;

public class BukkitReflectionUtils {
    public static Class<?> getNMSClass(String clazz) {
        return getClass(getNMSVersion() + "." + clazz);
    }

    public static Class<?> getCBClass(String clazz) {
        return getClass(getCBVersion() + "." + clazz);
    }

    public static Object invokeDeclaredMethod(Object object, String methodName, Object... parameters) {
        try {
            Method method = object.getClass().getDeclaredMethod(methodName, convertToMethodParameters(parameters));
            method.setAccessible(true);
            return method.invoke(object, parameters);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object invokeMethod(Object object, String methodName, Object... parameters) {
        try {
            Method method = object.getClass().getMethod(methodName, convertToMethodParameters(parameters));
            return method.invoke(object, parameters);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object invokeDeclaredConstructor(Class<?> clazz, Object... parameters) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor(convertToMethodParameters(parameters));
            constructor.setAccessible(true);
            return constructor.newInstance(parameters);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object invokeConstructor(Class<?> clazz, Object... parameters) {
        try {
            Constructor constructor = clazz.getConstructor(convertToMethodParameters(parameters));
            return constructor.newInstance(parameters);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getDeclaredField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getField(fieldName);
            return field.get(object);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setDeclaredField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setField(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getField(fieldName);
            field.set(object, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object getAndInvokeNMSConstructor(String className, Object... parameters) {
        Class<?> clazz = getNMSClass(className);
        return invokeConstructor(clazz, parameters);
    }

    public static Object getAndInvokeDeclaredNMSConstructor(String className, Object... parameters) {
        Class<?> clazz = getNMSClass(className);
        return invokeDeclaredConstructor(clazz, parameters);
    }

    public static Object getAndInvokeCBConstructor(String className, Object... parameters) {
        Class<?> clazz = getCBClass(className);
        return invokeConstructor(clazz, parameters);
    }

    public static Object getAndInvokeDeclaredCBConstructor(String className, Object parameters) {
        Class<?> clazz = getCBClass(className);
        return invokeDeclaredConstructor(clazz, parameters);
    }

    public static void modifyFinalField(Field field, Object target, Object newValue) {
        try {
            field.setAccessible(true);
            Field modifierField = Field.class.getField("modifiers");
            modifierField.setAccessible(true);
            modifierField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(target, newValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Class<?> getClass(String clazz) {
        try {
            return Class.forName(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getNMSVersion() {
        String version = Bukkit.getServer().getClass().getPackage().getName();
        return "net.minecraft.server" + version.substring(version.lastIndexOf("."));
    }

    private static String getCBVersion() {
        return Bukkit.getServer().getClass().getPackage().getName();
    }

    private static Class<?>[] convertToMethodParameters(Object... parameters) {
        Class<?>[] classes = new Class<?>[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Class<?> clazz = parameters[i].getClass();
            if (clazz == Boolean.class) {
                clazz = boolean.class;
            } else if (clazz == Byte.class) {
                clazz = byte.class;
            } else if (clazz == Character.class) {
                clazz = char.class;
            } else if (clazz == Double.class) {
                clazz = double.class;
            } else if (clazz == Float.class) {
                clazz = float.class;
            } else if (clazz == Integer.class) {
                clazz = int.class;
            } else if (clazz == Long.class) {
                clazz = long.class;
            } else if (clazz == Short.class) {
                clazz = short.class;
            } else if (clazz == Void.class) {
                clazz = void.class;
            }
            classes[i] = clazz;
        }
        return classes;
    }
}
