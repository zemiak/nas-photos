package com.zemiak.nasphotos.files.control;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.Dimension;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;

public class MovieMetadataReader {
    private static final Logger LOG = Logger.getLogger(MovieMetadataReader.class.getName());

    public Dimension getDimension(final String fileName) {
        IsoFile isoFile;
        long width = 0, height = 0;

        try {
            isoFile = new IsoFile(fileName);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot open file " + fileName, ex);
            return null;
        }


        MovieBox moov;
        try {
            moov = isoFile.getMovieBox();
        } catch (java.lang.RuntimeException ex) {
            LOG.log(Level.SEVERE, "Cannot read file metadata/1 " + fileName, ex);
            try {
                isoFile.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Cannot close file " + fileName, e);
            }

            return null;
        }

        if (null == moov) {
            LOG.log(Level.SEVERE, "Cannot read file metadata/2 " + fileName);
            try {
                isoFile.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Cannot close file " + fileName, e);
            }

            return null;
        }

        for (Box b: moov.getBoxes()) {
            if ("trak".equalsIgnoreCase(b.getType())) {
                TrackHeaderBox trak = ((TrackBox) b).getTrackHeaderBox();
                long w = Math.round(trak.getWidth());
                long h = Math.round(trak.getHeight());

                if (w != 0 && h != 0) {
                    width = w;
                    height = h;
                }
            }
        }

        try {
            isoFile.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot close file " + fileName, ex);
        }

        if (width != 0 || height != 0) {
            Dimension dimension = new Dimension();
            dimension.setSize(width, height);

            return dimension;
        }

        return null;
    }
}
