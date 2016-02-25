package com.zemiak.nasphotos.thumbnails;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ThumbnailCreator {
    private static final float MAX_SIZE = 256;
    private static final Logger LOG = Logger.getLogger(ThumbnailCreator.class.getName());

    public void create(Path original, String folder, String fileName, ImageInformation info) {
        BufferedImage img;
        try {
            img = ImageIO.read(original.toFile());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot read image " + original.toString(), ex);
            return;
        }

        float w = img.getWidth();
        float h = img.getHeight();
        float ratio = (w > h) ? (MAX_SIZE / w) : (MAX_SIZE / h);
        w = w * ratio;
        h = h * ratio;

        if (info.isRotated()) {
            BufferedImage rotated = ImageRotationControl.transformImage(img, info);

            Path outputPath = Paths.get(folder, fileName + "-r.jpg");
            try {
                ImageIO.write(rotated, "jpg", outputPath.toFile());
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Cannot write rotated " + outputPath.toString(), ex);
            }

            img = rotated;
            float e = w;
            w = h;
            h = e;
        }

        BufferedImage scaled = scale(img, Math.round(w), Math.round(h));

        Path outputPath = Paths.get(folder, fileName + ".jpg");
        try {
            ImageIO.write(scaled, "jpg", outputPath.toFile());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot write thumbnail " + outputPath.toString(), ex);
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
