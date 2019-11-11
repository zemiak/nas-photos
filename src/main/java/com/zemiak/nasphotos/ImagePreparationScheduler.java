package com.zemiak.nasphotos;

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

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Path("schedules/prepare")
public class ImagePreparationScheduler {
    private static final Logger LOG = Logger.getLogger(ImagePreparationScheduler.class.getName());

    @Inject @ConfigProperty(name = "photoPath") String photoPath;
    @Inject Rotator rotator;
    @Inject Thumbnailer thumbnailer;

    @Scheduled(cron = "0 15 1 * * ?")
    @GET
    public void rotatePicturesAndGenerateThumbnails() {
        new ImageWalker(this::makeThumbnailAndRotate, (path) -> PictureControl.isImage(path, photoPath)).walk();
        LOG.info("Done.");
    }

    private Void makeThumbnailAndRotate(String fullPath) {
        rotator.rotate(fullPath);
        thumbnailer.createOrUpdate(fullPath);
        return null;
    }
}
