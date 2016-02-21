package com.zemiak.nasphotos.files;

import java.io.File;
import javax.inject.Inject;

public class ImageControl {
    @Inject
    CoverControl covers;

    @Inject
    ImageMetadataControl metaData;

    public PictureData getImage(File file, String relativePath) {
        if (null == file) {
            return null;
        }

        PictureData data = new PictureData();
        data.setFile(file);
        data.setPath(relativePath + "/" + file.getName());

        String name = file.getAbsolutePath();
        name = name.contains("/") ? name.substring(name.lastIndexOf("/") + 1) : name;
        name = name.contains(".") ? name.substring(0, name.indexOf(".")) : name;
        data.setTitle(name);
        data.setInfo(metaData.getImageInfo(file));

        data.setCoverUrl(covers.getPictureCoverUrl(data.getPath()));
        data.setFullSizeUrl(covers.getFullSizeUrl(data.getPath()));

        return data;
    }
}
