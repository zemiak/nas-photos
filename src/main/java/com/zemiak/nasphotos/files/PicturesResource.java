package com.zemiak.nasphotos.files;

import com.zemiak.nasphotos.FilenameEncoder;
import java.io.File;
import java.nio.file.Paths;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("pictures")
@Produces(MediaType.APPLICATION_JSON)
public class PicturesResource {
    @Inject @ConfigProperty(name = "PHOTO_PATH", defaultValue = "/Volumes/media/Pictures/") String photoPath;
    @Inject FolderControl folders;

    @GET
    @Path("{id}")
    public Response download(@PathParam("id") String encodedPath) {
        String path = FilenameEncoder.decode(encodedPath);
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
}
