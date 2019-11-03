package com.zemiak.nasphotos.files.control;

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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import com.zemiak.nasphotos.files.entity.PictureData;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class PictureControl {
    private static final Logger LOG = Logger.getLogger(PictureControl.class.getName());
    private static final Pattern VALID_PICTURE = Pattern.compile("^\\d\\d\\d\\d\\/\\d\\d\\d\\d .+\\/.+(\\.jpg|\\.png|\\.heic)");

    @Inject @ConfigProperty(name = "photoPath") String photoPath;
    @Inject ImageReader imageReader;

    public JsonObject getPictures(String pathName) {
        List<PictureData> pictures = getPicturesRaw(pathName);
        JsonArrayBuilder list = Json.createArrayBuilder();
        pictures.forEach(i -> list.add(i.toJson()));
        return Json.createObjectBuilder().add("items", list).build();
    }

    private List<PictureData> getPicturesRaw(String pathName) {
        if (isRoot(pathName)) {
            return Collections.emptyList();
        }

        List<String> files;
        try {
            files = Files.walk(Paths.get(photoPath, pathName), 1, FileVisitOption.FOLLOW_LINKS)
                    .skip(1)
                    .filter(path -> !path.toFile().isDirectory())
                    .filter(path -> path.toFile().canRead())
                    .filter(path -> isImage(path, photoPath))
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "getPictures IO/Exception" + ex.getMessage(), ex);
            return Collections.emptyList();
        }

        Collections.sort(files);
        String[] yearAndFolder = pathName.split("/");
        return files
                .stream()
                .map(pictureFileName -> Paths.get(photoPath, yearAndFolder[0], yearAndFolder[1], pictureFileName).toString())
                .map(fileName -> new File(fileName))
                .map(n -> imageReader.getImage(n))
                .collect(Collectors.toList());
    }

    public static boolean isImage(Path path, String rootPath) {
        if (isHidden(path)) {
            LOG.log(Level.FINE, "isImage: hidden: {0}", path.toString());
            return false;
        }

        String name = path.toAbsolutePath().toString();
        if (name.startsWith(rootPath)) {
            name = name.substring(rootPath.length());
        }
        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        boolean matches = VALID_PICTURE.matcher(name.toLowerCase()).matches();
        return matches;
    }

    public static boolean isHidden(Path path) {
        String name;

        for (int i = 0; i < path.getNameCount(); i++) {
            name = path.getName(i).toString();
            if (name.startsWith(".") || name.startsWith("_") || name.equalsIgnoreCase("Originals")) {
                return true;
            }
        }

        return false;
    }

    public String getFileName(String path) {
        return Paths.get(path).getFileName().toString();
    }

    public File getFile(String path) {
        File file = Paths.get(photoPath, path).toFile();
        if (! file.canRead()) {
            return null;
        }

        return file;
    }

    private static boolean isRoot(String path) {
        return null == path || "".equals(path) || "/".equals(path);
    }
}
