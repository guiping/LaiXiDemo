package com.guiping.laixidemo.utils;

import com.google.gson.Gson;

/**
 * Created by guiping on 2020/10/20
 * <p>
 * Describe:
 */
public class GsonUtils {
    private static class GsonIntance {
        static Gson gson = new Gson();
    }

    private GsonUtils() {
    }

    public static Gson getGson() {
        return GsonIntance.gson;
    }
}
