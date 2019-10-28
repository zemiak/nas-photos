package com.zemiak.nasphotos;

public class SafeFile {
    public static boolean isSafe(String path) {
        return null != path && !path.startsWith("/") && !path.contains("..");
    }
}
