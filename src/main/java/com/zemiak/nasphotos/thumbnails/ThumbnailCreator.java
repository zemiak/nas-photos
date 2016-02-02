package com.zemiak.nasphotos.thumbnails;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ThumbnailCreator {
    private static int MAX_SIZE = 256;
    private static final Logger LOG = Logger.getLogger(ThumbnailCreator.class.getName());

    public void create(Path original, String folder, String fileName) {
        BufferedImage img;
        try {
            img = ImageIO.read(original.toFile());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot read image " + original.toString(), ex);
            return;
        }

        int w = (int) img.getWidth();
        int h = (int) img.getHeight();
        double ratio = (w > h) ? (MAX_SIZE / w) : (MAX_SIZE / h);

        BufferedImage scaled = scale(img, ratio);
        Path outputPath = Paths.get(folder, fileName + ".jpg");
        try {
            ImageIO.write(scaled, "jpg", outputPath.toFile());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot write thumbnail " + outputPath.toString(), ex);
        }
    }

    private BufferedImage scale(BufferedImage source, double ratio) {
        int w = (int) (source.getWidth() * ratio);
        int h = (int) (source.getHeight() * ratio);
        BufferedImage bi = getCompatibleImage(w, h);
        Graphics2D g2d = bi.createGraphics();
        double xScale = (double) w / source.getWidth();
        double yScale = (double) h / source.getHeight();
        AffineTransform at = AffineTransform.getScaleInstance(xScale, yScale);
        g2d.drawRenderedImage(source, at);
        g2d.dispose();
        return bi;
    }

    private BufferedImage getCompatibleImage(int w, int h) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage(w, h);
        return image;
    }
}
