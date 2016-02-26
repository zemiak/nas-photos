package com.zemiak.nasphotos.files;

import com.zemiak.nasphotos.thumbnails.ThumbnailService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class VersionService {
    private static final Logger LOG = Logger.getLogger(VersionService.class.getName());

    @Inject
    String tempPath;

    private String version;

    @PostConstruct
    public void clearVersion() {
        version = null;
    }

    public String getVersion() {
        if (null == version) {
            buildVersion();
        }

        return version;
    }

    private void buildVersion() {
        final MessageDigest digest = getInitialDigest();
        if (null == digest) {
            version = "err-digest";
            return;
        }

        try {
            Files.walk(Paths.get(tempPath), FileVisitOption.FOLLOW_LINKS)
                .filter(path -> !path.toFile().isDirectory())
                .filter(path -> path.toFile().canRead())
                .forEach(path -> digest.update(path.toString().getBytes(StandardCharsets.UTF_8)));
            version = ThumbnailService.bytesToHex(digest.digest());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error reading files and/or updating digest", ex);
            version = "err-updater";
        }
    }

    private MessageDigest getInitialDigest() {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "SHA-256 not supported!", ex);
            return null;
        }

        return digest;
    }
}
