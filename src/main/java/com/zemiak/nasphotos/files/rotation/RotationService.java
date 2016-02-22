package com.zemiak.nasphotos.files.rotation;

import com.zemiak.nasphotos.files.FileService;
import com.zemiak.nasphotos.files.ImageControl;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

public class RotationService {
    private static final Logger LOG = Logger.getLogger(RotationService.class.getName());

    @Inject
    String photoPath;

    @Inject
    String tempPath;

    @Inject
    FileService service;

    @Inject
    ImageControl images;

    @Inject
    ImageMetadataControl metaData;

    @Inject
    ImageRotationControl rotation;

    public void rotatePictures() {
        service.getRootFolderPaths().stream()
                .map(folderPath -> Paths.get(photoPath, folderPath))
                .forEach(this::createRotatedVersion);
    }

    private void createRotatedVersion(Path rootPath) {
        try {
            Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)
                .filter(path -> !path.toFile().isDirectory())
                .filter(path -> path.toFile().canRead())
                .filter(path -> !FileService.isHidden(path))
                .filter(path -> FileService.isImage(path, photoPath))
                .filter(this::isRotated)
                .filter(this::rotatedVersionDoesNotExist)
                .forEach(this::create);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "createRotatedVersion IO/Exception" + ex.getMessage(), ex);
        }
    }

    private boolean isRotated(Path path) {
        ImageInformation info = metaData.getImageInfo(path.toFile());
        return 1 != info.getOrientation();
    }

    private boolean rotatedVersionDoesNotExist(Path path) {
        return !images.isRotated(path.toFile()); // this finds out, if the pre-rotated file exists
    }

    private void create(Path original) {
        Path rotated = images.getRotatedFilePath(original.toFile());
        rotation.create(original, rotated, metaData.getImageInfo(original.toFile()));
    }
}
