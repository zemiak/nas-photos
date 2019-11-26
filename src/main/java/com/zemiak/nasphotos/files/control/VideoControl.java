package com.zemiak.nasphotos.files.control;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.enterprise.context.Dependent;

@Dependent
public class VideoControl {
    private static final Logger LOG = Logger.getLogger(VideoControl.class.getName());
    public static String SUBFOLDER_THUMBNAILED_VIDEOS = "video-thumbnails";
    private static Set<String> VIDEO_EXTENSIONS = new HashSet<>(Arrays.asList("mov", "mp4", "m4v"));
    private static final Pattern VALID_VIDEO = Pattern.compile("^\\d\\d\\d\\d\\/\\d\\d\\d\\d .+\\/.+(\\.mov|\\.mp4|\\.m4v)");

    public boolean isVideo(String path) {
        String ext = getFileExtension(path);
        if (".".equals(ext) || ext.isEmpty()) {
            return false;
        }

        return VIDEO_EXTENSIONS.contains(ext.substring(1).toLowerCase());
    }

    public File getVideoCover(String path) {
        String thumbnailedFullName = getThumbnailedPathAndFile(path);
        File thumbnailFile = new File(thumbnailedFullName);

        return thumbnailFile.exists() ? thumbnailFile : null;
    }

    private static String getThumbnailedPathAndFile(String fullName) {
        Path fullPath = Paths.get(fullName);
        Path folderPath = fullPath.getParent();
        String fileName = fullPath.getFileName().toString();

        String fileNameWithPictureExt = (fileName.contains(".") ? fileName.substring(0, fileName.indexOf(".")) : fileName) + ".JPG";

        Path thumbnailedPathAndFile = Paths.get(folderPath.toString(), SUBFOLDER_THUMBNAILED_VIDEOS, fileNameWithPictureExt);

        return thumbnailedPathAndFile.toString();
    }

    private String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    public static boolean isVideo(Path path, String rootPath) {
        if (PictureControl.isHidden(path)) {
            LOG.log(Level.FINE, "isVideo: hidden: {0}", path.toString());
            return false;
        }

        String name = path.toAbsolutePath().toString();
        if (name.startsWith(rootPath)) {
            name = name.substring(rootPath.length());
        }
        if (name.startsWith("/")) {
            name = name.substring(1);
        }

        if (VALID_VIDEO.matcher(name.toLowerCase()).matches()) {
            String baseName = path.toAbsolutePath().toString();
            int dot = baseName.lastIndexOf(".");
            baseName = baseName.substring(0, dot);

            return !new File(baseName + ".jpg").isFile() && !new File(baseName + ".JPG").isFile()
                && !new File(baseName + ".png").isFile() && !new File(baseName + ".PNG").isFile()
                && !new File(baseName + ".heif").isFile() && !new File(baseName + ".HEIF").isFile();
        }

        return false;
    }
}
