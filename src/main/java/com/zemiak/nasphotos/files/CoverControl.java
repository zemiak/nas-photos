package com.zemiak.nasphotos.files;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class CoverControl implements Serializable {
    private static final String COVER_FILE_NAME = "_cover.jpg";
    private static final String DEFAULT_COVER_FILE_URL = "ipad/img/folder.png";

    @Inject
    String photoPath;

    @Inject
    String externalUrl;

    public File getFolderCoverFile(String path) {
        File file = Paths.get(photoPath, path, COVER_FILE_NAME).toFile();
        return file.isFile() && file.canRead() ? file : null;
    }

    public String getFolderCoverUrl(String path) {
        if (null != getFolderCoverFile(path)) {
            try {
                return externalUrl + "rest/thumbnails/folders?path=" + URLEncoder.encode(path, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException("UTF-8 encoding not supported");
            }
        }

        return getDefaultFolderCover();
    }

    public String getDefaultFolderCover() {
        return externalUrl + DEFAULT_COVER_FILE_URL;
    }

    public String getPictureCoverUrl(String path) {
        try {
            return externalUrl + "rest/thumbnails?path=" + URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("UTF-8 encoding not supported");
        }
    }

    public String getMovieCoverUrl(String path) {
        try {
            return externalUrl + "rest/thumbnails/movies?path=" + URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("UTF-8 encoding not supported");
        }
    }

    public String getPictureFullSizeUrl(String path) {
        try {
            return externalUrl + "rest/files?path=" + URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("UTF-8 encoding not supported");
        }
    }
}
