package com.zemiak.nasphotos.thumbnails;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zemiak.nasphotos.files.control.FolderControl;
import com.zemiak.nasphotos.files.control.PictureControl;

import org.eclipse.microprofile.config.ConfigProvider;

public class ImageWalker {
    private static final Logger LOG = Logger.getLogger(FolderControl.class.getName());
    String photoPath;
    Function<String, Void> operator;
    Predicate<Path> fileFilter;

    public ImageWalker(Function<String, Void> operator, Predicate<Path> fileFilter) {
        this.photoPath = ConfigProvider.getConfig().getValue("photoPath", String.class);
        this.operator = operator;
        this.fileFilter = fileFilter;
    }

    public void walk() {
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
                    .filter(path -> path.toString().length() == 4)
                    .map(path -> {return Paths.get(photoPath, path.toString());})
                    .forEach(rootPath ->{
                        try {
                            walkSecondLevel(rootPath, operator);
                        } catch (IOException e) {
                            LOG.log(Level.SEVERE, "Error walking and processing files", e);
                        }
                    });
    }

    private void walkSecondLevel(Path startingPath, Function<String, Void> operator) throws IOException {
        Files.walk(startingPath, 1, FileVisitOption.FOLLOW_LINKS)
            .skip(1)
            .filter(path -> path.toFile().isDirectory())
            .filter(path -> path.toFile().canRead())
            .filter(path -> !PictureControl.isHidden(path))
            .forEach(path -> {
                try {
                    walkFiles(path, operator);
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Error walking and processing files", e);
                }
            });
    }

    private void walkFiles(Path startingPath, Function<String, Void> operator) throws IOException {
        Files.walk(startingPath, 1, FileVisitOption.FOLLOW_LINKS)
            .skip(1)
            .filter(path -> !path.toFile().isDirectory())
            .filter(path -> path.toFile().canRead())
            .filter(this.fileFilter)
            .forEach(fileName -> {
                visitFile(fileName, operator);
            });
    }

    private void visitFile(Path picturePath, Function<String, Void> operator) {
        operator.apply(picturePath.toString());
    }
}
