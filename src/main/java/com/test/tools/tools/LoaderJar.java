package com.test.tools.tools;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class LoaderJar {
    public static Class loadJarMain(String path) throws MalformedURLException, ClassNotFoundException {
        File file = new File(path);
        if (!file.exists()) return null;

        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
        Class<?> aClass = urlClassLoader.loadClass("com.plugin.Exp");
        return aClass;
    }
}
