package com.zemiak.nasphotos.files.boundary;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.zemiak.nasphotos.files.control.PictureControl;
import com.zemiak.nasphotos.rotation.control.ImageWalker;
import com.zemiak.nasphotos.rotation.control.Rotator;
import com.zemiak.nasphotos.thumbnails.Thumbnailer;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Path("backend/batch/thumbnails")
public class ThumbnailerJob {
    private static final Logger LOG = Logger.getLogger(ThumbnailerJob.class.getName());

    @Inject @ConfigProperty(name = "photoPath") String photoPath;
    @Inject Rotator rotator;
    @Inject Thumbnailer thumbnailer;

    @GET
    public void rotatePicturesAndGenerateThumbnails() {
        LOG.log(Level.INFO, "Start: {0}.", photoPath);
        new ImageWalker(this::makeThumbnailAndRotate, (path) -> PictureControl.isImage(path, photoPath)).walk();
        LOG.log(Level.INFO, "Done.");
    }

    private Void makeThumbnailAndRotate(String fullPath) {
        rotator.rotate(fullPath);
        thumbnailer.createOrUpdate(fullPath);
        return null;
    }
}
