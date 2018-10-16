package com.zemiak.nasphotos.files;

import java.io.File;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

@Stateless
public class FileService {
    @Inject FolderControl folders;
    @Inject PictureControl pictures;
    @Inject MovieControl movies;

    public JsonObject getList(String path, boolean encodePaths) {
        JsonArrayBuilder foldersArrayBuilder = Json.createArrayBuilder();
        folders.getFolders(path).stream().map(d -> pictureDataToJsonObject(d, encodePaths)).forEach(foldersArrayBuilder::add);

        JsonArrayBuilder filesArrayBuilder = Json.createArrayBuilder();
        pictures.getPictures(path).stream().map(d -> pictureDataToJsonObject(d, encodePaths)).forEach(filesArrayBuilder::add);

        JsonArrayBuilder livePhotosArrayBuilder = Json.createArrayBuilder();
        movies.getLivePhotos(path).stream().map(d -> livePhotoDataToJsonObject(d, encodePaths)).forEach(livePhotosArrayBuilder::add);

        JsonArrayBuilder moviesArrayBuilder = Json.createArrayBuilder();
        movies.getMovies(path).stream().map(d -> pictureDataToJsonObject(d, encodePaths)).forEach(moviesArrayBuilder::add);

        JsonObject main = Json.createObjectBuilder()
                .add("folders", foldersArrayBuilder.build())
                .add("files", filesArrayBuilder.build())
                .add("movies", moviesArrayBuilder.build())
                .add("livePhotos", livePhotosArrayBuilder.build())
                .build();

        return main;
    }

    private JsonObject livePhotoDataToJsonObject(LivePhotoData data, boolean encodePaths) {
        String path = encodePaths ? FileName.encode(data.getPath()) : data.getPath();
        String imagePath = encodePaths ? FileName.encode(data.getImageUrl()) : data.getImageUrl();

        return Json.createObjectBuilder()
                .add("path", path)
                .add("imagePath", imagePath)
                .add("title", data.getTitle())
                .add("width", data.getWidth())
                .add("height", data.getHeight())
                .build();
    }

    public JsonObject pictureDataToJsonObject(PictureData data, boolean encodePaths) {
        String path = encodePaths ? FileName.encode(data.getPath()) : data.getPath();
        
        return Json.createObjectBuilder()
                .add("path", path)
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
