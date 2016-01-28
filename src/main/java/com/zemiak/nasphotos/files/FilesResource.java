package com.zemiak.nasphotos.files;

import java.io.File;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

@Path("files")
@Produces(MediaType.APPLICATION_JSON)
public class FilesResource {
    @Inject
    FileService fileService;

    @GET
    @Path("list")
    public Response get(@QueryParam("path") @DefaultValue("") String path) {
        JsonArrayBuilder foldersArrayBuilder = Json.createArrayBuilder();
        fileService.getFolders(path).stream().map(this::pictureDataToJsonObject).forEach(foldersArrayBuilder::add);

        JsonArrayBuilder filesArrayBuilder = Json.createArrayBuilder();
        fileService.getPictures(path).stream().map(this::pictureDataToJsonObject).forEach(filesArrayBuilder::add);

        JsonObject main = Json.createObjectBuilder()
                .add("folders", foldersArrayBuilder.build())
                .add("files", filesArrayBuilder.build())
                .build();

        return Response.ok(main).build();
    }

    private JsonObject pictureDataToJsonObject(PictureData data) {
        return Json.createObjectBuilder()
                .add("path", data.getPath())
                .add("title", data.getTitle())
                .add("width", data.getWidth())
                .add("height", data.getHeight())
                .build();
    }

    @GET
    @Path("download")
    public Response download(@QueryParam("path") @DefaultValue("") String path) {
        File file = fileService.getFile(path);
        if (null == file) {
            return Response.status(Status.GONE).build();
        }

        String fileName = fileService.getFileName(path);
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=" + fileName);
        return response.build();
    }
}
