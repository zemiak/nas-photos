package com.zemiak.nasphotos.control;

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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import com.rometools.utils.Strings;
import com.zemiak.nasphotos.entity.PictureData;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class FolderControl {
    private static final Logger LOG = Logger.getLogger(FolderControl.class.getName());
    @Inject @ConfigProperty(name = "photoPath") String photoPath;
    @Inject ImageReader imageReader;

    public JsonObject getList(String pathName) {
        List<PictureData> folders = getFolders(pathName);
        JsonArrayBuilder list = Json.createArrayBuilder();
        folders.forEach(i -> list.add(i.toJson()));
        return Json.createObjectBuilder().add("items", list).build();
    }

    private List<PictureData> getFolders(String pathName) {
        if (Strings.isEmpty(pathName)) {
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
            return Collections.emptyList();
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
            return Collections.emptyList();
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

    public File getFolderCover(String path) {
        String absolutePath = path.startsWith("/") ? path : Paths.get(photoPath, path).toString();
        String cover = Paths.get(absolutePath, "_cover.jpg").toString();
        File coverFile = new File(cover);
        if (! coverFile.canRead()) {
            coverFile = new File(Paths.get(photoPath, "special", "folder.png").toString());
        }

        return coverFile;
    }

    private PictureData convertFolderToPictureData(String path) {
        File coverFile = getFolderCover(path);

        PictureData image = imageReader.getImage(coverFile);
        image.setId(path);

        String title = path.contains("/") ? path.substring(path.lastIndexOf("/") + 1) : path;
        title = title.contains(".") ? title.substring(0, title.indexOf(".")) : title;
        image.setTitle(title);
        image.setType("folder");

        return image;
    }

    public boolean isLeafFolder(String realPath) {
        List<PictureData> folders = getFolders(realPath);
        return folders.isEmpty();
    }
}
