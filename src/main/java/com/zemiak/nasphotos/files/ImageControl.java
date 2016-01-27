package com.zemiak.nasphotos.files;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageControl {
    public PictureData getImage(File file, String relativePath) {
        if (null == file) {
            System.err.println(relativePath + ": file is null");
            return null;
        } else {
            System.err.println("ImageControl: " + file.getAbsolutePath());
        }

        PictureData data = new PictureData();
        data.setFile(file);
        data.setPath(relativePath + "/" + file.getName());

        String name = relativePath.contains("/") ? relativePath.substring(relativePath.lastIndexOf("/") + 1) : relativePath;
        name = name.contains(".") ? name.substring(0, name.indexOf(".")) : name;
        data.setTitle(name);

        Dimension dimension;
        try {
            dimension = getDimensions(file);
        } catch (IOException ex) {
            dimension = null;
        }

        if (null != dimension) {
            data.setWidth(Math.round(dimension.getWidth()));
            data.setHeight(Math.round(dimension.getHeight()));
        }

        return data;
    }

    private Dimension getDimensions(File file) throws IOException {
        try(ImageInputStream in = ImageIO.createImageInputStream(file)){
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    return new Dimension(reader.getWidth(0), reader.getHeight(0));
                } finally {
                    reader.dispose();
                }
            }
        }

        return null;
    }
}
