package com.zemiak.nasphotos;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Hasher {
    private static final Logger LOG = Logger.getLogger(Hasher.class.getName());

    public static MessageDigest getDigest() {
        MessageDigest digest;

        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "SHA-256 not supported!", ex);
            throw new IllegalStateException("Hash SHA-256 not available");
        }

        return digest;
    }

    public static String getHash(String load) {
        return bytesToHex(getDigest().digest(load.getBytes(StandardCharsets.UTF_8)));
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte byt : bytes) {
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        }

        return result.toString();
    }
}
