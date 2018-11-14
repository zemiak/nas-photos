package com.zemiak.nasphotos.pictures;

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
import javax.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class PictureControl {
    private static final Logger LOG = Logger.getLogger(PictureControl.class.getName());
    private static final Pattern VALID_PICTURE = Pattern.compile("^\\d\\d\\d\\d\\/\\d\\d\\d\\d .+\\/.+(\\.jpg|\\.png)");

    @Inject @ConfigProperty(name = "PHOTO_PATH") String photoPath;
    @Inject ImageReader imageReader;

    public List<PictureData> getPictures(String pathName) {
        if (isRoot(pathName)) {
            return Collections.EMPTY_LIST;
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
            return Collections.EMPTY_LIST;
        }

        Collections.sort(files);
        return files
                .stream()
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
            if (name.startsWith(".") || name.startsWith("_")) {
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
