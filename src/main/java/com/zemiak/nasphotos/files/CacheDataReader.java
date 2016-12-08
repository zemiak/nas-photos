package com.zemiak.nasphotos.files;

import com.zemiak.nasphotos.lookup.ConfigurationProvider;
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
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

@Singleton
public class CacheDataReader {
    private static final Logger LOG = Logger.getLogger(CacheDataReader.class.getName());

    final private String tempPath = ConfigurationProvider.getTempPath();

    @Inject FolderControl folders;
    @Inject FileService service;

    private String version;
    private JsonObject cache;

    @PostConstruct
    public void clearVersion() {
        version = null;
        cache = null;
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

    public JsonObject getCache() {
        if (null == cache) {
            buildCache();
        }

        return cache;
    }

    private void buildCache() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        folders.getRootFolderPaths()
                .stream()
                .forEach(folder -> this.processFolder(folder, builder));

        cache = builder.build();
    }

    private void processFolder(String folder, JsonObjectBuilder builder) {
        JsonObject list = service.getList(folder);

        if (list.getJsonArray("folders").isEmpty() && list.getJsonArray("files").isEmpty() &&
                list.getJsonArray("movies").isEmpty() && list.getJsonArray("livePhotos").isEmpty()) {
            return;
        }

        builder.add(folder, list);

        JsonArray folderArray = list.getJsonArray("folders");
        for (int i = 0; i < list.size(); i++) {
            JsonObject item;
            try {
                item = folderArray.getJsonObject(i);
            } catch (IndexOutOfBoundsException ex) {
                continue;
            }

            processFolder(item.getString("path"), builder);
        }
    }

    public void reload() {
        buildVersion();
        buildCache();
    }
}
