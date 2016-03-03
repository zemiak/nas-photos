package com.zemiak.nasphotos.files;

import com.zemiak.nasphotos.thumbnails.ThumbnailService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

@Singleton
public class CacheDataReader {
    private static final Logger LOG = Logger.getLogger(CacheDataReader.class.getName());

    @Inject
    String tempPath;

    @Inject
    String photoPath;

    @Inject
    FileService service;

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

    public JsonObject pictureDataToJsonObject(PictureData data) {
        return Json.createObjectBuilder()
                .add("path", data.getPath())
                .add("title", data.getTitle())
                .add("width", data.getWidth())
                .add("height", data.getHeight())
                .build();
    }

    public JsonObject getCache() {
        if (null == cache) {
            buildCache();
        }

        return cache;
    }

    private void buildCache() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        service.getRootFolderPaths().stream().forEach(folder -> this.processFolder(folder, builder));

        cache = builder.build();
    }

    private void processFolder(String folder, JsonObjectBuilder builder) {
        Path path = Paths.get(folder);
        List<PictureData> folders = service.getFolders(path.toString());
        List<PictureData> pictures = service.getPictures(path.toString());

        if (folders.isEmpty() && pictures.isEmpty()) {
            return;
        }

        JsonArrayBuilder filesArrayBuilder = Json.createArrayBuilder();
        pictures.stream().map(this::pictureDataToJsonObject).forEach(filesArrayBuilder::add);

        JsonArrayBuilder foldersArrayBuilder = Json.createArrayBuilder();
        folders.stream().map(this::pictureDataToJsonObject).forEach(foldersArrayBuilder::add);

        builder.add("folders", foldersArrayBuilder);
        builder.add("files", filesArrayBuilder);

        folders.stream().forEach(folderData -> processFolder(folderData.getPath(), builder));
    }

    public void reload() {
        buildVersion();
        buildCache();
    }
}
