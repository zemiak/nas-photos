package com.zemiak.nasphotos.files;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.zemiak.nasphotos.thumbnails.ImageInformation;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class MetadataReader {
    private static final Logger LOG = Logger.getLogger(MetadataReader.class.getName());

    public ImageInformation getImageInfo(File file) {
        long width, height;
        int orientation = 1;

        if (! file.isFile()) {
            LOG.log(Level.SEVERE, "File does not exist: {0}", file.getAbsolutePath());
            return null;
        }

        try {
            Dimension dimension = getDimensions(file);
            if (null == dimension) {
                return null;
            }

            width = Math.round(dimension.getWidth());
            height = Math.round(dimension.getHeight());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }

        try {
            orientation = readImageOrientation(file);
        } catch (IOException | MetadataException | ImageProcessingException ex) {
            LOG.severe(file.getAbsolutePath() + ": Could not get orientation");
        }

        return new ImageInformation(orientation, width, height);
    }

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

            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
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
