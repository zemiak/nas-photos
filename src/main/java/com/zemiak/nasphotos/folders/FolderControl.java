package com.zemiak.nasphotos.folders;

import com.zemiak.nasphotos.pictures.ImageReader;
import com.zemiak.nasphotos.pictures.PictureControl;
import com.zemiak.nasphotos.pictures.PictureData;
import java.io.File;
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
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class FolderControl {
    private static final Logger LOG = Logger.getLogger(FolderControl.class.getName());
    @Inject @ConfigProperty(name = "PHOTO_PATH") String photoPath;
    @Inject ImageReader imageReader;

    public JsonObject getList(String pathName) {
        List<PictureData> folders = getFolders(pathName);
        JsonArrayBuilder list = Json.createArrayBuilder();
        folders.forEach(i -> list.add(i.toJson()));
        return Json.createObjectBuilder().add("folders", list).build();
    }

    private List<PictureData> getFolders(String pathName) {
        if ("/".equals(pathName)) {
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
                .map(this::convertFolderToPictureData)
                .collect(Collectors.toList());
    }

    private List<String> getRootFolderPaths() {
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
                .map(this::convertFolderToPictureData)
                .collect(Collectors.toList());
    }

    private PictureData convertFolderToPictureData(String path) {
        String cover = path + "/_cover.jpg";
        File coverFile = new File(cover);
        if (! coverFile.canRead()) {
            cover = "/opt/watermarks/folder.png";
            coverFile = new File(cover);
        }

        return imageReader.getImage(coverFile);
    }
}
