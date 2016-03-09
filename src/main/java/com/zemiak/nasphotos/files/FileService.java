package com.zemiak.nasphotos.files;

import java.io.File;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

@Stateless
public class FileService {
    private static final Logger LOG = Logger.getLogger(FileService.class.getName());

    @Inject
    FolderControl folders;

    @Inject
    PictureControl pictures;

    @Inject
    MovieControl movies;

    public JsonObject getList(String path) {
        JsonArrayBuilder foldersArrayBuilder = Json.createArrayBuilder();
        folders.getFolders(path).stream().map(this::pictureDataToJsonObject).forEach(foldersArrayBuilder::add);

        JsonArrayBuilder filesArrayBuilder = Json.createArrayBuilder();
        pictures.getPictures(path).stream().map(this::pictureDataToJsonObject).forEach(filesArrayBuilder::add);

        JsonArrayBuilder livePhotosArrayBuilder = Json.createArrayBuilder();
        movies.getLivePhotos(path).stream().map(this::livePhotoDataToJsonObject).forEach(livePhotosArrayBuilder::add);

        JsonArrayBuilder moviesArrayBuilder = Json.createArrayBuilder();
        movies.getMovies(path).stream().map(this::pictureDataToJsonObject).forEach(moviesArrayBuilder::add);

        JsonObject main = Json.createObjectBuilder()
                .add("folders", foldersArrayBuilder.build())
                .add("files", filesArrayBuilder.build())
                .add("movies", moviesArrayBuilder.build())
                .add("livePhotos", livePhotosArrayBuilder.build())
                .build();

        return main;
    }

    private JsonObject livePhotoDataToJsonObject(LivePhotoData data) {
        return Json.createObjectBuilder()
                .add("path", data.getPath())
                .add("imagePath", data.getImagePath())
                .add("title", data.getTitle())
                .add("width", data.getWidth())
                .add("height", data.getHeight())
                .build();
    }

    public JsonObject pictureDataToJsonObject(PictureData data) {
        return Json.createObjectBuilder()
                .add("path", data.getPath())
                .add("title", data.getTitle())
                .add("width", data.getWidth())
                .add("height", data.getHeight())
                .build();
    }

    public static boolean isRoot(String path) {
        return null == path || "".equals(path) || "/".equals(path);
    }

    public File getFile(String path) {
        return pictures.getFile(path);
    }

    public String getFileName(String path) {
        return pictures.getFileName(path);
    }
}
