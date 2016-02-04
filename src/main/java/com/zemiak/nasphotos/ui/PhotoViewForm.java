package com.zemiak.nasphotos.ui;

import com.zemiak.nasphotos.files.FileService;
import com.zemiak.nasphotos.files.PictureData;
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
    private FileService service;

    private String path;
    private List<PictureData> folders;
    private List<PictureData> pictures;

    public String check() {
        if (null == path || "/".equals(path)) {
            path = "";
        }

        folders = service.getFolders(path);
        pictures = service.getPictures(path);

        if (folders.isEmpty() && pictures.isEmpty()) {
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
