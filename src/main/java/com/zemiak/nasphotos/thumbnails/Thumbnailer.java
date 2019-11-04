package com.zemiak.nasphotos.thumbnails;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.imageio.ImageIO;

import com.zemiak.nasphotos.rotation.control.Rotator;

@Dependent
public class Thumbnailer {
    public static final String SUBFOLDER_THUMBNAILED = "thumbnails";
    private static final float MAX_SIZE = 256;
	private static final Logger LOG = Logger.getLogger(Thumbnailer.class.getName());

    public void createOrUpdate(String fullPath) {
        String thumbnailedFullName = getThumbnailedPathAndFile(fullPath);
        if (new File(thumbnailedFullName).exists()) {
            return;
        }

        createFolderIfNeeded(fullPath);
        createThumbnail(Paths.get(Rotator.getRotatedFileName(fullPath)), Paths.get(thumbnailedFullName));

        LOG.log(Level.INFO, "Thumbnailed picture {0}", fullPath);
    }

    public static String getThumbnailedFileName(String fullName) {
        String thumbnailedFullName = getThumbnailedPathAndFile(fullName);
        return new File(thumbnailedFullName).exists() ? thumbnailedFullName : fullName;
	}

	private static String getThumbnailedPathAndFile(String fullName) {
        Path fullPath = Paths.get(fullName);
        Path folderPath = fullPath.getParent();
        Path fileName = fullPath.getFileName();
        Path thumbnailedPathAndFile = Paths.get(folderPath.toString(), SUBFOLDER_THUMBNAILED, fileName.toString());

        return thumbnailedPathAndFile.toString();
    }

    private String getThumbnailedPath(String fullName) {
        Path fullPath = Paths.get(fullName);
        Path folderPath = fullPath.getParent();
        Path thumbnailedPath = Paths.get(folderPath.toString(), SUBFOLDER_THUMBNAILED);

        return thumbnailedPath.toString();
    }

    private void createFolderIfNeeded(String fullName) {
        String thumbnailedFolderName = getThumbnailedPath(fullName);
        File thumbnailedFolder = new File(thumbnailedFolderName);
        if (!thumbnailedFolder.isDirectory()) {
            try {
                Files.createDirectory(Paths.get(thumbnailedFolderName));
                LOG.log(Level.INFO, "Created thumbnailed folder {0}", thumbnailedFolderName);
            } catch (IOException e) {
                throw new RuntimeException("Cannot create folder " + thumbnailedFolderName);
            }
        }
    }

    public void createThumbnail(Path originalFullPath, Path destinationFullPath) {
        BufferedImage img;
        try {
            img = ImageIO.read(originalFullPath.toFile());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot read image " + originalFullPath.toString(), ex);
            return;
        }

        float w = img.getWidth();
        float h = img.getHeight();
        float ratio = (w > h) ? (MAX_SIZE / w) : (MAX_SIZE / h);
        w = w * ratio;
        h = h * ratio;

        BufferedImage scaled = scale(img, Math.round(w), Math.round(h));

        try {
            ImageIO.write(scaled, "jpg", destinationFullPath.toFile());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot write thumbnailed " + destinationFullPath.toString(), ex);
        }
    }

    public static BufferedImage scale(BufferedImage source, int destWidth, int destHeight) {
        BufferedImage bicubic = new BufferedImage(destWidth, destHeight, source.getType());
        Graphics2D bg = bicubic.createGraphics();
        bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        float sx = (float) destWidth / source.getWidth();
        float sy = (float) destHeight / source.getHeight();
        bg.scale(sx, sy);
        bg.drawImage(source, 0, 0, null);
        bg.dispose();
        return bicubic;
    }
}
