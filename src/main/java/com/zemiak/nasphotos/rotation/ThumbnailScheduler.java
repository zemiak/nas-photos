package com.zemiak.nasphotos.rotation;

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
public class ThumbnailScheduler {
    @Inject @ConfigProperty(name = "photoPath") String photoPath;
    @Inject Rotator rotator;
    @Inject Thumbnailer thumbnailer;

    @Scheduled(cron = "0 15 1 * * ?")
    @GET
    public void rotatePicturesAndGenerateThumbnails() {
        new ImageWalker(this::makeThumbnailAndRotate, (path) -> PictureControl.isImage(path, photoPath)).walk();
    }

    private Void makeThumbnailAndRotate(String fullPath) {
        rotator.rotate(fullPath);
        thumbnailer.createOrUpdate(fullPath);
        return null;
    }
}
