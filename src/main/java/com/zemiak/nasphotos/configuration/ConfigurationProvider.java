package com.zemiak.nasphotos.configuration;

import java.nio.file.Paths;

/**
 * Needed ENV keys are listed below.
 *
 * BIN_PATH
 * PHOTO_PATH
 * TEMP_PATH
 * WATERMARK_PATH
 * EXTERNAL_URL
 */
public final class ConfigurationProvider {
    private static String get(String key) {
        String value = System.getenv(key);
        if (null == value || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing configuration " + key);
        }

        return value;
    }

    private static String getBinPath() {
        return get("BIN_PATH");
    }

    public static String getPhotoPath() {
        return Paths.get(get("PHOTO_PATH")).toString();
    }

    public static String getTempPath() {
        return Paths.get(get("TEMP_PATH")).toString();
    }

    public static String getFfmpegPath() {
        return Paths.get(getBinPath(), "ffmpegthumbnailer").toString();
    }

    public static String getCompositePath() {
        return Paths.get(getBinPath(), "composite").toString();
    }

    public static String getMediaInfoPath() {
        return Paths.get(getBinPath(), "mediainfo").toString();
    }

    public static String getWatermarkPath() {
        return Paths.get(get("WATERMARK_PATH")).toString();
    }

    public static String getExternalUrl() {
        return get("EXTERNAL_URL");
    }
}
