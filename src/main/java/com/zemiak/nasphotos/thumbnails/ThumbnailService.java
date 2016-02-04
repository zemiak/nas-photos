package com.zemiak.nasphotos.thumbnails;

import com.zemiak.nasphotos.files.FileService;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ThumbnailService {
    private static final Logger LOG = Logger.getLogger(ThumbnailService.class.getName());

    @Inject
    String photoPath;

    @Inject
    String tempPath;

    @Inject
    ThumbnailCreator creator;

    @Inject
    FileService service;

    @Schedule(hour = "1", minute = "5", persistent = false)
    public void createThumbnails() {
        try {
            Files.createDirectories(Paths.get(tempPath));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot create the thumbnail folder " + tempPath, ex);
            return;
        }

        service.getRootFolderPaths().stream()
                .map(folderPath -> Paths.get(photoPath, folderPath))
                .forEach(this::createThumbnails);
    }

    private void createThumbnails(Path rootPath) {
        System.err.println("Going to generate covers for " + rootPath.toString());

        try {
            Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)
                .filter(path -> !path.toFile().isDirectory())
                .filter(path -> path.toFile().canRead())
                .filter(path -> !FileService.isHidden(path))
                .filter(path -> FileService.isImage(path, photoPath))
                .filter(path -> thumbnailDoesNotExist(path))
                .forEach(this::create);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "createThumbnails IO/Exception" + ex.getMessage(), ex);
        }
    }

    private String getThumbnailFileName(Path path) {
        String fileName = path.toAbsolutePath().toString();

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "SHA-256 not supported!", ex);
            return fileName.replace("/", "_");
        }

        return bytesToHex(digest.digest(fileName.getBytes(StandardCharsets.UTF_8)));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) {
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        }

        return result.toString();
     }

    private Boolean thumbnailDoesNotExist(Path path) {
        File file = Paths.get(tempPath, getThumbnailFileName(path)).toFile();
        return !file.isFile() || !file.canRead();
    }

    private void create(Path path) {
        creator.create(path, tempPath, getThumbnailFileName(path));
        LOG.log(Level.INFO, "Created thumbnail: {0}", path.toString());
    }
}
