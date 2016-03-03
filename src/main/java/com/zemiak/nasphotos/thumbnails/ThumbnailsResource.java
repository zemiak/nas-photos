package com.zemiak.nasphotos.thumbnails;

import com.zemiak.nasphotos.batch.ScheduledCacheRegeneration;
import com.zemiak.nasphotos.files.FileService;
import com.zemiak.nasphotos.thumbnails.ThumbnailService;
import java.io.File;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("thumbnails")
@Produces(MediaType.APPLICATION_JSON)
public class ThumbnailsResource {
    @Inject
    ThumbnailService thumbnails;

    @Inject
    ScheduledCacheRegeneration cache;

    @Inject
    FileService files;

    @GET
    public Response thumbnail(@QueryParam("path") @DefaultValue("") String path) {
        File file = thumbnails.getThumbnail(path);
        if (null == file) {
            return Response.status(Response.Status.GONE).build();
        }

        String fileName = files.getFileName(path);
        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=" + fileName);
        response.header("Content-Type", "image/jpeg");
        return response.build();
    }

    @GET
    @Path("folders")
    public Response folderThumbnail(@QueryParam("path") @DefaultValue("") String path) {
        File file = thumbnails.getThumbnail(path);
        if (null == file) {
            return Response.status(302).header("Location", files.getDefaultFolderCover()).build();
        }

        String fileName = files.getFileName(path);
        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=" + fileName);

        if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            response.header("Content-Type", "image/jpeg");
        } else if (fileName.toLowerCase().endsWith(".png")) {
            response.header("Content-Type", "image/png");
        }

        return response.build();
    }

    @GET
    @Path("refresh")
    public void refreshThumbnails() {
        cache.refreshImageCache();
    }
}
