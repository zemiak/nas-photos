package com.zemiak.nasphotos.files.control;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.inject.Inject;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.zemiak.nasphotos.files.entity.PictureData;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class ImageReader {
    private static final Logger LOG = Logger.getLogger(ImageReader.class.getName());

    @Inject
    @ConfigProperty(name = "photoPath") String photoPath;

    public PictureData getImage(File file) {
        if (null == file) {
            return null;
        }

        PictureData data = new PictureData();
        String name = file.getAbsolutePath();

        data.setId(relativePath(name));
        String title = name.contains("/") ? name.substring(name.lastIndexOf("/") + 1) : name;
        title = title.contains(".") ? title.substring(0, title.indexOf(".")) : title;
        data.setTitle(title);

        setImageInfo(file, data);

        return data;
    }

    private String relativePath(String absolute) {
        if (! absolute.startsWith("/")) {
            return absolute;
        }

        if (! absolute.startsWith(photoPath)) {
            throw new RuntimeException("Photo path " + photoPath + " is not inside of the boundaries.");
        }

        return absolute.substring(photoPath.length());
    }

    private void setImageInfo(File file, PictureData data) {
        long width, height;
        int orientation = 1;

        if (! file.isFile()) {
            LOG.log(Level.SEVERE, "File does not exist: {0}", file.getAbsolutePath());
            return;
        }

        data.setRatioWidth(4);
        data.setRatioHeight(3);

        try {
            Dimension dimension = getDimensions(file);
            if (null == dimension) {
                return;
            }

            width = Math.round(dimension.getWidth());
            height = Math.round(dimension.getHeight());

            Double ar = dimension.getWidth() / dimension.getHeight();
            for (int i = 1; i < 40; ++i) {
                int m = (int)(ar * i + 0.5); // Mathematical rounding
                if (Math.abs(ar - (double)m/i) < 0.01) {
                    data.setRatioWidth(m);
                    data.setRatioHeight(i);
                    break;
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return;
        }

        try {
            orientation = readImageOrientation(file);
        } catch (IOException | MetadataException | ImageProcessingException ex) {
            LOG.log(Level.SEVERE, "{0}: Could not get orientation", file.getAbsolutePath());
        }

        data.setOrientation(orientation);
        data.setWidth(width);
        data.setHeight(height);
    }

    /**
     * 1. 0 degrees – the correct orientation, no adjustment is required.
     * 2. 0 degrees, mirrored – image has been flipped back-to-front.
     * 3. 180 degrees – image is upside down.
     * 4. 180 degrees, mirrored – image is upside down and flipped back-to-front.
     * 5. 90 degrees – image is on its side.
     * 6. 90 degrees, mirrored – image is on its side and flipped back-to-front.
     * 7. 270 degrees – image is on its far side.
     * 8. 270 degrees, mirrored – image is on its far side and flipped back-to-front.
     */
    private int readImageOrientation(File imageFile) throws IOException, MetadataException, ImageProcessingException {
        Metadata metadata = ImageMetadataReader.readMetadata(imageFile);
        Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

        return null == directory ? 1 : directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
    }

    private Dimension getDimensions(File file) throws IOException {
        try (ImageInputStream in = ImageIO.createImageInputStream(file)){
            if (null == in) {
                LOG.log(Level.SEVERE, "Cannot get a reader for file {0}", file.toString());
                return null;
            }

            final Iterator<javax.imageio.ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                javax.imageio.ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    return new Dimension(reader.getWidth(0), reader.getHeight(0));
                } finally {
                    reader.dispose();
                }

            }
        }

        return null;
    }
}
