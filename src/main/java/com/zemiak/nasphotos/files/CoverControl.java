package com.zemiak.nasphotos.files;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import javax.enterprise.context.Dependent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@Dependent
public class CoverControl implements Serializable {
    private static final String COVER_FILE_NAME = "_cover.jpg";

    @Inject
    String photoPath;

    public File getFolderCoverFile(String path) {
        File file = Paths.get(photoPath, path, COVER_FILE_NAME).toFile();
        return file.isFile() && file.canRead() ? file : null;
    }

    public String getFolderCoverUrl(String path) {
        HttpServletRequest origRequest = (HttpServletRequest)
                FacesContext.getCurrentInstance().getExternalContext().getRequest();

        if (null != getFolderCoverFile(path)) {
            try {
                return origRequest.getContextPath() + "/rest/files/download?path=" + URLEncoder.encode(path, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException("UTF-8 encoding not supported");
            }
        }

        return origRequest.getContextPath() + "/ipad/img/folder.png";
    }

    public String getPictureCoverUrl(String path) {
        HttpServletRequest origRequest = (HttpServletRequest)
                FacesContext.getCurrentInstance().getExternalContext().getRequest();

        try {
            return origRequest.getContextPath() + "/rest/files/download?path=" + URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("UTF-8 encoding not supported");
        }
    }
}
