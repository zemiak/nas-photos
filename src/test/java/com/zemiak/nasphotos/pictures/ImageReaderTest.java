package com.zemiak.nasphotos.pictures;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import com.zemiak.nasphotos.files.entity.PictureData;

import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ImageReaderTest {
    private static final Logger LOG = Logger.getLogger(ImageReaderTest.class.getName());

    @Test
    public void ratioCalc() throws IOException {
        String photoPath = ConfigProvider.getConfig().getValue("photoPath", String.class);
        if (null == photoPath || photoPath.isEmpty()) {
            photoPath = "/Volumes/media/Pictures";
        }

        File file = new File(photoPath + "/special/folder.png");
        if (! file.canRead()) {
            throw new IOException("File folder.png does not exist");
        }

        long width, height;
        PictureData data = new PictureData();

        Dimension dimension = getDimensions(file);
        if (null == dimension) {
            return;
        }

        width = Math.round(dimension.getWidth());
        height = Math.round(dimension.getHeight());

        Double ar = dimension.getWidth() / dimension.getHeight();
        for (int i = 1; i < 40; ++i) {
            int m = (int)(ar * i + 0.5); // Mathematical rounding
            double rv = Math.abs(ar - (double)m/i);

            if (rv < 0.01) {
                data.setRatioWidth(m);
                data.setRatioHeight(i);
                break;
            }
        }

        data.setWidth(width);
        data.setHeight(height);

        System.err.println("Data: " + data.toString());
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
