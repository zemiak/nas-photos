package com.zemiak.nasphotos.files;

import com.zemiak.nasphotos.thumbnails.ImageInformation;
import com.zemiak.nasphotos.thumbnails.ImageMetadataControl;
import com.zemiak.nasphotos.thumbnails.ThumbnailService;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;

public class ImageControl {
    @Inject
    CoverControl covers;

    @Inject
    ImageMetadataControl metaData;

    @Inject
    ThumbnailService thumbnails;

    @Inject
    String tempPath;

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

        File thumbnail = Paths.get(tempPath, thumbnails.getThumbnailFileName(Paths.get(file.getAbsolutePath())) + ".jpg").toFile();
        ImageInformation thumbnailInfo = metaData.getImageInfo(thumbnail);
        data.setCoverWidth(thumbnailInfo.getWidth());
        data.setCoverHeight(thumbnailInfo.getHeight());

        return data;
    }

    public Path getRotatedFilePath(File file) {
        String fileName = thumbnails.getThumbnailFileName(Paths.get(file.getAbsolutePath()));
        fileName = fileName + "-r.jpg";

        return Paths.get(tempPath, fileName);

    }

    public boolean isRotated(File file) {
        File rotatedFile = getRotatedFilePath(file).toFile();
        return rotatedFile.isFile() && rotatedFile.canRead();
    }
}
