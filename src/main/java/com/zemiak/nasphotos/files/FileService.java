package com.zemiak.nasphotos.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class FileService {
    private static final Logger LOG = Logger.getLogger(FileService.class.getName());

    @Inject
    String photoPath;

    public List<String> getFolders(String pathName) {
        if (isRoot(pathName)) {
            return getRootFolders();
        }

        List<String> files;
        try {
            files = Files.walk(Paths.get(photoPath, pathName), 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(path -> path.toFile().isDirectory())
                    .filter(path -> isNotHidden(path))
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "getFolders IO/Exception" + ex.getMessage(), ex);
            return Collections.EMPTY_LIST;
        }

        Collections.sort(files);
        return files;
    }

    private List<String> getRootFolders() {
        System.err.println("getRootFolders: " + photoPath);

        List<String> files;
        try {
            files = Files.walk(Paths.get(photoPath), 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(path -> path.toFile().isDirectory())
                    .filter(path -> isNotHidden(path))
                    .map(path -> path.getFileName().toString())
                    .filter(fileName -> fileName.length() == 4)
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "getRootFolders IO/Exception" + ex.getMessage(), ex);
            return Collections.EMPTY_LIST;
        }

        Collections.sort(files);
        return files;
    }

    public List<String> getPictures(String pathName) {
        if (isRoot(pathName)) {
            return Collections.EMPTY_LIST;
        }

        List<String> files;
        try {
            files = Files.walk(Paths.get(photoPath, pathName), 1, FileVisitOption.FOLLOW_LINKS)
                    .filter(path -> !path.toFile().isDirectory())
                    .filter(path -> isNotHidden(path))
                    .map(path -> path.getFileName().toString())
                    .filter(path -> path.toLowerCase().endsWith("jpg") || path.toLowerCase().endsWith("png"))
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "getPictures IO/Exception" + ex.getMessage(), ex);
            return Collections.EMPTY_LIST;
        }

        Collections.sort(files);
        return files;
    }

    private boolean isNotHidden(Path path) {
        for (int i = 0; i < path.getNameCount(); i++) {
            String name = path.getName(i).toString();
            if (name.startsWith(".") || name.startsWith("_")) {
                return false;
            }
        }

        return true;
    }

    public String getFileName(String path) {
        return Paths.get(path).getFileName().toString();
    }

    private boolean isRoot(String path) {
        return null == path || "".equals(path) || "/".equals(path);
    }

    public File getFile(String path) {
        return Paths.get(photoPath, path).toFile();
    }
}
