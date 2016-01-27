package com.zemiak.nasphotos.ui;

import com.zemiak.nasphotos.files.FileService;
import com.zemiak.nasphotos.files.PictureData;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("photoViewForm")
public class PhotoViewForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private FileService service;

    private String path;
    private List<String> folders;
    private List<PictureData> pictures;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String check() {
        if (null == path) {
            System.err.println("PhotoViewForm: path is null, setting to an empty string");
            path = "";
        }

        folders = service.getFolders(path).stream().map(f -> Paths.get(path, f).toString()).collect(Collectors.toList());
        pictures = service.getPictures(path);

        if (folders.isEmpty() && pictures.isEmpty()) {
            JsfMessages.addErrorMessage("Path " + path + " is empty");
            return "index";
        }

        return null;
    }

    public List<String> getFolders() {
        return folders;
    }

    public List<PictureData> getPictures() {
        return pictures;
    }

    public Boolean hasPictures() {
        return !pictures.isEmpty();
    }
}
