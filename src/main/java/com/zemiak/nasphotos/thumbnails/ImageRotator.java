package com.zemiak.nasphotos.thumbnails;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ImageRotator {
    private static final Logger LOG = Logger.getLogger(ImageRotator.class.getName());

    public void createRotatedVersion(Path original, Path destination, ImageInformation info) {
        BufferedImage img;
        try {
            img = ImageIO.read(original.toFile());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot read image " + original.toString(), ex);
            return;
        }

        BufferedImage scaled;
        try {
            scaled = transformImage(img, info);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error rotating " + original.toString(), ex);
            return;
        }

        try {
            ImageIO.write(scaled, "jpg", destination.toFile());
            LOG.log(Level.INFO, "Rotated and cached image {0} -> {1}", new Object[]{original.toString(), destination.toString()});
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot write rotated image " + destination.toString(), ex);
        }
    }

    public static BufferedImage transformImage(BufferedImage image, ImageInformation info) {
        AffineTransform transform = info.getAffineTransform();
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        BufferedImage destinationImage = op.createCompatibleDestImage(image,  (image.getType() == BufferedImage.TYPE_BYTE_GRAY)? image.getColorModel() : null );
        Graphics2D g = destinationImage.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());
        destinationImage = op.filter(image, destinationImage);
        return destinationImage;
    }
}
