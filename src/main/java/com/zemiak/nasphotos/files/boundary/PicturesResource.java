package com.zemiak.nasphotos.files.boundary;

import java.io.File;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.zemiak.nasphotos.SafeFile;
import com.zemiak.nasphotos.files.control.FolderControl;
import com.zemiak.nasphotos.files.control.VideoControl;
import com.zemiak.nasphotos.thumbnails.Thumbnailer;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Produces(MediaType.APPLICATION_JSON)
@Path("backend")
public class PicturesResource {
    @Inject @ConfigProperty(name = "photoPath") String photoPath;
    @Inject FolderControl folders;
    @Inject VideoControl videos;

    @GET
    @Path("download")
    public Response download(@QueryParam("path") String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        if (! SafeFile.isSafe(path)) {
            return Response.status(Status.FORBIDDEN).entity("Path " + path + " is unsafe").build();
        }

        if (! path.startsWith("/")) {
            path = Paths.get(photoPath, path).toString();
        }

        File file = new File(path);
        if (file.isDirectory()) {
            file = folders.getFolderCover(path);
        }

        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=" + file.getName());

        if (path.toLowerCase().endsWith(".jpg") || path.toLowerCase().endsWith(".jpeg")) {
            response.header("Content-Type", "image/jpeg");
        } else if (path.toLowerCase().endsWith(".png")) {
            response.header("Content-Type", "image/png");
        } else if (path.toLowerCase().endsWith(".heic")) {
            response.header("Content-Type", "image/heic");
        }

        return response.build();
    }

    @GET
    @Path("thumbnail")
    public Response downloadThumbnail(@QueryParam("path") String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        if (! SafeFile.isSafe(path)) {
            return Response.status(Status.FORBIDDEN).entity("Path " + path + " is unsafe").build();
        }

        if (! path.startsWith("/")) {
            path = Paths.get(photoPath, path).toString();
        }

        File file = new File(path);
        if (file.isDirectory()) {
            file = folders.getFolderCover(path);
        } else {
            if (videos.isVideo(path)) {
                file = videos.getVideoCover(path);
            } else {
                file = new File(Thumbnailer.getThumbnailedFileName(path));
            }
        }

        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=" + file.getName());

        if (path.toLowerCase().endsWith(".jpg") || path.toLowerCase().endsWith(".jpeg")) {
            response.header("Content-Type", "image/jpeg");
        } else if (path.toLowerCase().endsWith(".png")) {
            response.header("Content-Type", "image/png");
        } else if (path.toLowerCase().endsWith(".heic")) {
            response.header("Content-Type", "image/heic");
        }

        return response.build();
    }
}
