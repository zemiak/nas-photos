package com.zemiak.nasphotos.files;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

public final class FileName {
    private static final Decoder DECODER = Base64.getDecoder();
    private static final Encoder ENCODER = Base64.getEncoder();

    private FileName() {
    }

    static String decode(String base64EncodedFileName) {
        return new String(DECODER.decode(base64EncodedFileName.replace('_', '=')));
    }

    static String encode(String fileName) {
        return ENCODER.encodeToString(fileName.getBytes()).replace('=', '_');
    }
}
