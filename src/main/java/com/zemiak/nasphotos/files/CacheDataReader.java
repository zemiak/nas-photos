package com.zemiak.nasphotos.files;

import com.zemiak.nasphotos.Hasher;
import com.zemiak.nasphotos.configuration.ConfigurationProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
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
    private JsonObject cacheEncoded;

    @PostConstruct
    public void clearVersion() {
        version = null;
        cache = null;
        cacheEncoded = null;
    }

    public String getVersion() {
        if (null == version) {
            buildVersion();
        }

        return version;
    }

    private void buildVersion() {
        final MessageDigest digest = Hasher.getDigest();

        try {
            Files.walk(Paths.get(tempPath), FileVisitOption.FOLLOW_LINKS)
                .filter(path -> !path.toFile().isDirectory())
                .filter(path -> path.toFile().canRead())
                .forEach(path -> digest.update(path.toString().getBytes(StandardCharsets.UTF_8)));
            version = Hasher.bytesToHex(digest.digest());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error reading files and/or updating digest", ex);
            version = "err-updater";
        }
    }

    public JsonObject getCache() {
        if (null == cache) {
            buildCache();
        }

        return cache;
    }

    public JsonObject getCacheEncoded() {
        if (null == cacheEncoded) {
            buildCache();
        }

        return cacheEncoded;
    }

    private void buildCache() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        folders.getRootFolderPaths()
                .stream()
                .forEach(folder -> this.processFolder(folder, builder, false));

        cache = builder.build();

        JsonObjectBuilder builder2 = Json.createObjectBuilder();
        folders.getRootFolderPaths()
                .stream()
                .forEach(folder -> this.processFolder(folder, builder2, true));

        cacheEncoded = builder2.build();
    }

    private void processFolder(String folder, JsonObjectBuilder builder, boolean encodePaths) {
        JsonObject list = service.getList(folder, encodePaths);

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

            String path = encodePaths ? FileName.decode(item.getString("path")) : item.getString("path");
            processFolder(path, builder, encodePaths);
        }
    }

    public void reload() {
        buildVersion();
        buildCache();
    }
}
