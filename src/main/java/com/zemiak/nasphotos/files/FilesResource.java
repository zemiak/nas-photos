package com.zemiak.nasphotos.files;

import com.zemiak.nasphotos.batch.ScheduledCacheRegeneration;
import com.zemiak.nasphotos.thumbnails.ThumbnailService;
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
    FileService files;

    @Inject
    ThumbnailService thumbnails;

    @Inject
    ScheduledCacheRegeneration cache;

    @Inject
    VersionService versionService;

    @GET
    @Path("list")
    public Response get(@QueryParam("path") @DefaultValue("") String path) {
        JsonArrayBuilder foldersArrayBuilder = Json.createArrayBuilder();
        files.getFolders(path).stream().map(this::pictureDataToJsonObject).forEach(foldersArrayBuilder::add);

        JsonArrayBuilder filesArrayBuilder = Json.createArrayBuilder();
        files.getPictures(path).stream().map(this::pictureDataToJsonObject).forEach(filesArrayBuilder::add);

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
    @Path("thumbnails")
    public Response thumbnail(@QueryParam("path") @DefaultValue("") String path) {
        File file = thumbnails.getThumbnail(path);
        if (null == file) {
            return Response.status(Status.GONE).build();
        }

        String fileName = files.getFileName(path);
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=" + fileName);
        response.header("Content-Type", "image/jpeg");
        return response.build();
    }

    @GET
    @Path("folderThumbnails")
    public Response folderThumbnail(@QueryParam("path") @DefaultValue("") String path) {
        File file = thumbnails.getThumbnail(path);
        if (null == file) {
            return Response.status(302).header("Location", files.getDefaultFolderCover()).build();
        }

        String fileName = files.getFileName(path);
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=" + fileName);
        response.header("Content-Type", "image/jpeg");

        return response.build();
    }

    @GET
    @Path("thumbnails/refresh")
    public void refreshThumbnails() {
        cache.refreshImageCache();
    }

    private JsonObject buildData() {
        JsonObject version = Json.createObjectBuilder()
                .add("version", versionService.getVersion())
                .add("motd", "")
                .build();
        JsonObject cache = Json.createObjectBuilder().build();
        JsonObject data = Json.createObjectBuilder()
                .add("version", version)
                .add("cache", cache)
                .build();

        return data;
    }

    @GET
    @Path("data")
    public Response getData() {
        return Response
                .ok(buildData())
                .build();
    }
}
