package com.zemiak.nasphotos.files;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class FolderControl {
    private static final Logger LOG = Logger.getLogger(FolderControl.class.getName());

    @Inject
    String photoPath;

    @Inject
    FolderConverter folderConverter;

    @Inject
    CoverControl covers;

    public List<PictureData> getFolders(String pathName) {
        if (FileService.isRoot(pathName)) {
            return getRootFolders();
        }

        List<String> files;
        try {
            files = Files.walk(Paths.get(photoPath, pathName), 1, FileVisitOption.FOLLOW_LINKS)
                    .skip(1)
                    .filter(path -> path.toFile().isDirectory())
                    .filter(path -> path.toFile().canRead())
                    .filter(path -> !PictureControl.isHidden(path))
                    .map(path -> Paths.get(pathName, path.getFileName().toString()).toString())
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "getFolders IO/Exception" + ex.getMessage(), ex);
            return Collections.EMPTY_LIST;
        }

        Collections.sort(files);
        return files
                .stream()
                .map(n -> folderConverter.convertFolderToPictureData(n))
                .collect(Collectors.toList());
    }

    public List<String> getRootFolderPaths() {
        List<String> files;
        try {
            files = Files.walk(Paths.get(photoPath), 1, FileVisitOption.FOLLOW_LINKS)
                    .skip(1)
                    .filter(path -> path.toFile().isDirectory())
                    .filter(path -> path.toFile().canRead())
                    .filter(path -> !PictureControl.isHidden(path))
                    .map(path -> path.getFileName().toString())
                    .filter(fileName -> fileName.length() == 4)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "getRootFolders IO/Exception" + ex.getMessage(), ex);
            return Collections.EMPTY_LIST;
        }

        return files;
    }

    private List<PictureData> getRootFolders() {
        List<String> files = getRootFolderPaths();

        Collections.sort(files, Collections.reverseOrder());
        return files
                .stream()
                .map(n -> folderConverter.convertFolderToPictureData(n))
                .collect(Collectors.toList());
    }
}
