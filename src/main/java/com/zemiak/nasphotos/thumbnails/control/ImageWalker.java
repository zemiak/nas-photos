package com.zemiak.nasphotos.thumbnails.control;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.nasphotos.files.control.FolderControl;
import com.zemiak.nasphotos.files.control.PictureControl;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Dependent
public class ImageWalker {
    private static final Logger LOG = Logger.getLogger(FolderControl.class.getName());

    @Inject
    @ConfigProperty(name = "photoPath")
    String photoPath;

    public void walkImages(Function<String, Void> operator) {
        System.out.println("walkImages()");

        try {
            walkFromRoot(operator);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error walking and processing files", e);
        }
    }

    private void walkFromRoot(Function<String, Void> operator) throws IOException {
        Files.walk(Paths.get(photoPath), 1, FileVisitOption.FOLLOW_LINKS)
                    .skip(1)
                    .filter(path -> path.toFile().isDirectory())
                    .filter(path -> path.toFile().canRead())
                    .filter(path -> !PictureControl.isHidden(path))
                    .map(path -> path.getFileName().toString())
                    .filter(fileName -> fileName.length() == 4)
                    .forEach(rootFolder ->{
                        System.out.println("Visiting root level folder " + rootFolder);

                        try {
                            walkSecondLevel(rootFolder, operator);
                        } catch (IOException e) {
                            LOG.log(Level.SEVERE, "Error walking and processing files", e);
                        }
                    });
    }

    private void walkSecondLevel(String pathName, Function<String, Void> operator) throws IOException {
        Files.walk(Paths.get(photoPath, pathName), 1, FileVisitOption.FOLLOW_LINKS)
            .skip(1)
            .filter(path -> path.toFile().isDirectory())
            .filter(path -> path.toFile().canRead())
            .filter(path -> !PictureControl.isHidden(path))
            .map(path -> Paths.get(pathName, path.getFileName().toString()).toString())
            .forEach(secondLevelFolder -> {
                System.out.println("Visiting second level folder " + secondLevelFolder);

                try {
                    walkFiles(secondLevelFolder, operator);
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Error walking and processing files", e);
                }
            });
    }

    private void walkFiles(String pathName, Function<String, Void> operator) throws IOException {
        Files.walk(Paths.get(photoPath, pathName), 1, FileVisitOption.FOLLOW_LINKS)
            .skip(1)
            .filter(path -> !path.toFile().isDirectory())
            .filter(path -> path.toFile().canRead())
            .filter(path -> PictureControl.isImage(path, photoPath))
            .map(path -> path.getFileName().toString())
            .forEach(fileName -> {
                visitFile(fileName, operator);
            });
    }

    private void visitFile(String pathName, Function<String, Void> operator) {
        System.out.println("Visiting file " + pathName);
    }
}
