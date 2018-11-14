package com.zemiak.nasphotos.folders;

import com.zemiak.nasphotos.FilenameEncoder;
import com.zemiak.nasphotos.pictures.ImageReader;
import com.zemiak.nasphotos.pictures.PictureData;
import java.io.File;
import java.io.Serializable;
import javax.inject.Inject;

public class FolderConverter implements Serializable {
    @Inject
    ImageReader imageReader;

    public PictureData convertFolderToPictureData(String path) {
        String cover = path + "/_cover.jpg";
        if (! new File(cover).exists()) {
            cover = "/opt/watermarks/folder.png";
        }

        PictureData image = imageReader.getImage(new File(cover));

        PictureData data = new PictureData(image);
        data.setTitle(new File(path).getName());
        data.setId(FilenameEncoder.encode(path));

        return data;
    }
}
