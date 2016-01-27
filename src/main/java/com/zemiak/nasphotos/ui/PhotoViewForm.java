package com.zemiak.nasphotos.ui;

import com.zemiak.nasphotos.files.FileService;
import java.io.Serializable;
import java.util.List;
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
    private List<String> pictures;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String check() {
        if (null != path) {
            folders = service.getFolders(path);
            pictures = service.getPictures(path);
        }

        if (null == path || (folders.isEmpty() && pictures.isEmpty())) {
            JsfMessages.addErrorMessage("Path " + path + " is empty");
            return "index";
        }

        return null;
    }

    public List<String> getFolders() {
        return folders;
    }

    public List<String> getPictures() {
        return pictures;
    }
}
