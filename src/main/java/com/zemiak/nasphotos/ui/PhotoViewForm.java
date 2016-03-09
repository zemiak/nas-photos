package com.zemiak.nasphotos.ui;

import com.zemiak.nasphotos.files.*;
import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@RequestScoped
@Named("photoViewForm")
public class PhotoViewForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    FileService service;

    @Inject
    FolderControl folderControl;

    @Inject
    PictureControl pictureControl;

    @Inject
    MovieControl movieControl;

    private String path;
    private List<PictureData> folders;
    private List<PictureData> pictures;
    private List<PictureData> movies;
    private List<LivePhotoData> livePhotos;

    public String check() {
        if (null == path || "/".equals(path)) {
            path = "";
        }

        folders = folderControl.getFolders(path);
        pictures = pictureControl.getPictures(path);
        movies = movieControl.getMovies(path);
        livePhotos = movieControl.getLivePhotos(path);

        if (folders.isEmpty() && pictures.isEmpty() && livePhotos.isEmpty() && movies.isEmpty()) {
            JsfMessages.addErrorMessage("Path " + path + " is empty");
            return "index";
        }

        return null;
    }

    public List<PictureData> getFolders() {
        return folders;
    }

    public List<PictureData> getPictures() {
        return pictures;
    }

    public List<PictureData> getMovies() {
        return movies;
    }

    public List<LivePhotoData> getLivePhotos() {
        return livePhotos;
    }

    public Boolean getPicturesExist() {
        return !pictures.isEmpty();
    }

    public String getPath() {
        return path;
    }

    public Boolean getBackButton() {
        return !"".equals(path);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        if (! getBackButton()) {
            return "Photos";
        }

        int pos = path.lastIndexOf("/");
        return pos > -1 ? path.substring(pos + 1) : path;
    }

    public String getBackPath() {
        int pos = path.lastIndexOf("/");
        if (-1 == pos) {
            return "/";
        }

        return path.substring(0, path.lastIndexOf("/"));
    }
}
