package com.zemiak.nasphotos.files.control;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.TrackHeaderBox;
import com.zemiak.nasphotos.files.entity.PictureData;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class MovieReader {
    private static final Logger LOG = Logger.getLogger(MovieReader.class.getName());

    @Inject
    @ConfigProperty(name = "photoPath") String photoPath;

    public PictureData getMovie(File file) {
        if (null == file) {
            return null;
        }

        PictureData data = new PictureData();
        data.setType("video");

        String name = file.getAbsolutePath();

        data.setId(relativePath(name));
        String title = name.contains("/") ? name.substring(name.lastIndexOf("/") + 1) : name;
        title = title.contains(".") ? title.substring(0, title.indexOf(".")) : title;
        data.setTitle(title);

        setMovieInfo(file, data);

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

    public void setMovieInfo(File file, PictureData data) {
        IsoFile isoFile;
        String fileName = file.getAbsolutePath();

        try {
            isoFile = new IsoFile(fileName);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot open file " + fileName, ex);
            return;
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

            return;
        }

        if (null == moov) {
            LOG.log(Level.SEVERE, "Cannot read file metadata/2 " + fileName);
            try {
                isoFile.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Cannot close file " + fileName, e);
            }

            return;
        }

        for (Box b: moov.getBoxes()) {
            if ("trak".equalsIgnoreCase(b.getType())) {
                TrackHeaderBox trak = ((TrackBox) b).getTrackHeaderBox();
                long w = Math.round(trak.getWidth());
                long h = Math.round(trak.getHeight());

                if (w != 0 && h != 0) {
                    data.setHeight(h);
                    data.setWidth(w);
                }
            }
        }

        try {
            isoFile.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot close file " + fileName, ex);
        }

        data.setOrientation(1); // matrix box?
    }
}
