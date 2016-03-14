package com.zemiak.nasphotos.files;

import com.zemiak.nasphotos.thumbnails.ImageInformation;
import com.zemiak.nasphotos.thumbnails.ThumbnailService;
import com.zemiak.nasphotos.thumbnails.ThumbnailSize;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;

public class ImageReader {
    @Inject
    CoverControl covers;

    @Inject
    MetadataReader metaData;

    @Inject
    ThumbnailService thumbnails;

    @Inject
    String tempPath;

    @Inject
    String externalUrl;

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
        data.setFullSizeUrl(covers.getPictureFullSizeUrl(data.getPath()));

        File thumbnail = Paths.get(tempPath, thumbnails.getThumbnailFileName(Paths.get(file.getAbsolutePath())) + ".jpg").toFile();
        ImageInformation thumbnailInfo = metaData.getImageInfo(thumbnail);

        if (thumbnailInfo.getWidth() > thumbnailInfo.getHeight()) {
            data.setCoverWidth(Math.min(ThumbnailSize.WIDTH, thumbnailInfo.getWidth()));
            data.setCoverHeight(Math.min(ThumbnailSize.HEIGHT, thumbnailInfo.getHeight()));
        } else {
            data.setCoverWidth(Math.min(ThumbnailSize.HEIGHT, thumbnailInfo.getWidth()));
            data.setCoverHeight(Math.min(ThumbnailSize.WIDTH, thumbnailInfo.getHeight()));
        }


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
