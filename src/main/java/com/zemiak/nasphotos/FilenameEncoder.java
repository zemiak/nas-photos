package com.zemiak.nasphotos;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public final class FilenameEncoder {
    private static final Decoder DECODER = Base64.getDecoder();
    private static final Encoder ENCODER = Base64.getEncoder();

    private FilenameEncoder() {
    }

    public static String decode(String base64EncodedFileName) {
        return new String(DECODER.decode(base64EncodedFileName.replace('_', '=')));
    }

    public static String encode(String fileName) {
        return ENCODER.encodeToString(fileName.getBytes()).replace('=', '_');
    }
}
