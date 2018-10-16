package com.zemiak.nasphotos.files;

import com.zemiak.nasphotos.thumbnails.ThumbnailService;
import java.io.File;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

@Path("files")
@Produces(MediaType.APPLICATION_JSON)
public class FilesResource {
    @Inject FileService files;
    @Inject ThumbnailService thumbnails;

    @GET
    @Path("{id}/thumbnail")
    public Response thumbnail(@PathParam("id") @DefaultValue("") String encodedPath) {
        String path = FileName.decode(encodedPath);
        File file = thumbnails.getThumbnail(path);
        if (null == file) {
            return Response.status(Response.Status.GONE).build();
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
    @Path("{id}/binary")
    public Response download(@PathParam("id") @DefaultValue("") String encodedPath) {
        String path = FileName.decode(encodedPath);
        File file = files.getFile(path);
        if (null == file) {
            return Response.status(Status.GONE).build();
        }

        String fileName = files.getFileName(path);
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=" + fileName);

        if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            response.header("Content-Type", "image/jpeg");
        } else if (fileName.toLowerCase().endsWith(".png")) {
            response.header("Content-Type", "image/png");
        }

        return response.build();
    }

    @GET
    @Path("{id}/list")
    public Response get(@PathParam("id") @DefaultValue("") String encodedPath) {
        String path = FileName.decode(encodedPath);
        return Response.ok(files.getList(path, true)).build();
    }
}
