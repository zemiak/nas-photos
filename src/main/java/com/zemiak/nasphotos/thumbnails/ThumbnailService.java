package com.zemiak.nasphotos.thumbnails;

import com.zemiak.nasphotos.Hasher;
import com.zemiak.nasphotos.files.*;
import com.zemiak.nasphotos.lookup.ConfigurationProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;

public class ThumbnailService {
    private static final Logger LOG = Logger.getLogger(ThumbnailService.class.getName());

    final private String photoPath = ConfigurationProvider.getPhotoPath();
    final private String tempPath = ConfigurationProvider.getTempPath();
    @Inject ThumbnailCreator creator;
    @Inject FolderControl folders;
    @Inject MetadataReader metaData;
    @Inject CoverControl covers;
    @Inject MovieThumbnailCreator movieThumbnails;
    @Inject MovieControl movies;

    public void createThumbnails() {
        folders.getRootFolderPaths().stream()
                .map(folderPath -> Paths.get(photoPath, folderPath))
                .forEach(this::createThumbnails);
    }

    private void createThumbnails(Path rootPath) {
        try {
            Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)
                .filter(path -> !path.toFile().isDirectory())
                .filter(path -> path.toFile().canRead())
                .filter(path -> !PictureControl.isHidden(path))
                .filter(path -> !isThumbnailCreated(path))
                .forEach(path -> {
                    if (PictureControl.isImage(path, photoPath)) {
                        this.createPictureThumbnail(path);
                    } else if (movies.isMovieFile(path)) {
                        this.createMovieThumbnail(path);
                    }
                });
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "createThumbnails IO/Exception" + ex.getMessage(), ex);
        }
    }

    public static String getThumbnailFileName(Path path) {
        return Hasher.getHash(path.toAbsolutePath().toString());
    }

    private Boolean isThumbnailCreated(Path path) {
        File file = Paths.get(tempPath, getThumbnailFileName(path) + ".jpg").toFile();
        return file.isFile() && file.canRead();
    }

    private void createPictureThumbnail(Path path) {
        String dest = getThumbnailFileName(path);
        creator.create(path, tempPath, dest, metaData.getImageInfo(path.toFile()));
        LOG.log(Level.INFO, "Thumbnailed {0} -> {1}", new Object[]{path.toString(), tempPath + "/" + dest + ".jpg"});
    }

    private void createMovieThumbnail(Path path) {
        String dest = getThumbnailFileName(path);
        movieThumbnails.create(path, tempPath, dest);
        LOG.log(Level.INFO, "Thumbnailed/movie {0} -> {1}", new Object[]{path.toString(), tempPath + "/" + dest + ".jpg"});
    }

    public File getThumbnail(String pathName) {
        Path path = Paths.get(photoPath, pathName);
        File file = path.toFile();
        if (! file.canRead()) {
            return null;
        }

        if (file.isDirectory()) {
            return covers.getFolderCoverFile(pathName);
        }

        return Paths.get(tempPath, getThumbnailFileName(path) + ".jpg").toFile();
    }
}
