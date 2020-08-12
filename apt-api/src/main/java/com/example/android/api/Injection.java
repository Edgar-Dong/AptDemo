package com.example.android.api;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.android.api.template.Injectable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author:無忌
 * @date:2020/8/5
 * @description:
 */
public class Injection {
    private static final String SUFFIX = "$$ViewBinding";

    public static void inject(@NonNull Activity target) {
        inject(target, target.getWindow().getDecorView());
    }

    public static void inject(@NonNull Activity target, @NonNull View view) {
        String className = target.getClass().getName();
        try {
            Class<?> clazz = target.getClass().getClassLoader().loadClass(className + SUFFIX);
            Constructor<Injectable> constructor = (Constructor<Injectable>) clazz.getConstructor(target.getClass(), View.class);
            constructor.newInstance(target, view);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
