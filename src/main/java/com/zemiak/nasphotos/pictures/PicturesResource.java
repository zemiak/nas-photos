package com.zemiak.nasphotos.pictures;

import com.zemiak.nasphotos.FilenameEncoder;
import java.io.File;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("pictures")
@Produces(MediaType.APPLICATION_JSON)
public class PicturesResource {
    @GET
    @Path("{id}")
    public Response download(@PathParam("id") String encodedPath) {
        String path = FilenameEncoder.decode(encodedPath);
        File file = new File(path);
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
