package com.zemiak.nasphotos.thumbnails;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.zemiak.nasphotos.thumbnails.control.ImageWalker;
import com.zemiak.nasphotos.thumbnails.control.Rotator;
import com.zemiak.nasphotos.thumbnails.control.Thumbnailer;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Path("schedules/prepare")
public class ThumbnailScheduler {
    @Inject @ConfigProperty(name = "photoPath") String photoPath;
    @Inject Rotator rotator;
    @Inject Thumbnailer thumbnailer;
    @Inject ImageWalker walker;

    @Scheduled(cron = "0 15 1 * * ?")
    @GET
    public void rotatePicturesAndGenerateThumbnails() {
        // 1:15am every day
        walker.walkImages(this::makeThumbnailAndRotate);
    }

    private Void makeThumbnailAndRotate(String fullPath) {
        rotator.rotate(fullPath);
        thumbnailer.createOrUpdate(fullPath);
        return null;
    }
}
