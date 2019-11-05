package com.zemiak.nasphotos.thumbnails;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.zemiak.nasphotos.files.control.PictureControl;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
@Path("backend/thumbnails")
public class ThumbnailScheduler {
    @Inject @ConfigProperty(name = "photoPath") String photoPath;
    @Inject Thumbnailer thumbnailer;

    @Scheduled(cron = "0 15 1 * * ?")
    @POST
    public void rotatePicturesAndGenerateThumbnails() {
        new ImageWalker(this::makeThumbnail, (path) -> PictureControl.isImage(path, photoPath)).walk();
    }

    private Void makeThumbnail(String fullPath) {
        thumbnailer.createOrUpdate(fullPath);
        return null;
    }
}
