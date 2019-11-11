package com.zemiak.nasphotos.rotation.control;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.nasphotos.files.control.ImageReader;
import com.zemiak.nasphotos.files.entity.PictureData;
import com.zemiak.nasphotos.rotation.entity.ImageInformation;

@Dependent
public class Rotator {
    public static final String SUBFOLDER_ROTATED = "rotated";
    private static final Logger LOG = Logger.getLogger(Rotator.class.getName());

    @Inject
    ImageReader imageReader;

    @Inject
    MetadataReader metadataReader;

    @Inject
    ImageManipulation manipulator;

    public void rotate(String fullPath) {
        File picFile = new File(fullPath);
        PictureData picData = imageReader.getImage(picFile);
        int orientation = picData.getOrientation();

        switch (orientation) {
        case 3:
        case 4:
            // rotate 180 degrees (upside down)
            rotate(fullPath, 180);
            break;

        case 5:
        case 6:
            // rotate -90 degrees (to the left)
            rotate(fullPath, -90);
            break;

        case 7:
        case 8:
            // rotate +90 degrees (to the right)
            rotate(fullPath, 90);
            break;

        default:
            // do nothing
            break;
        }
    }

    public static String getRotatedFileName(String fullName) {
        String rotatedFullName = getRotatedPathAndFile(fullName);
        return new File(rotatedFullName).exists() ? rotatedFullName : fullName;
    }

    private void rotate(String fullName, int degrees) {
        ImageInformation info = metadataReader.getImageInfo(new File(fullName));
        if (! info.isRotated()) {
            return;
        }

        String rotatedFullName = getRotatedPathAndFile(fullName);
        if (new File(rotatedFullName).exists()) {
            return;
        }

        createFolderIfNeeded(fullName);

        info.setOrientation(ImageInformation.ROTATED_CLOCKWISE);
        manipulator.rotate(Paths.get(fullName), Paths.get(rotatedFullName), info);

        LOG.log(Level.INFO, "Rotated picture {0}", fullName);
    }

    private static String getRotatedPathAndFile(String fullName) {
        Path fullPath = Paths.get(fullName);
        Path folderPath = fullPath.getParent();
        Path fileName = fullPath.getFileName();
        Path rotatedPathAndFile = Paths.get(folderPath.toString(), SUBFOLDER_ROTATED, fileName.toString());

        return rotatedPathAndFile.toString();
    }

    private String getRotatedPath(String fullName) {
        Path fullPath = Paths.get(fullName);
        Path folderPath = fullPath.getParent();
        Path rotatedPath = Paths.get(folderPath.toString(), SUBFOLDER_ROTATED);

        return rotatedPath.toString();
    }

    private void createFolderIfNeeded(String fullName) {
        String rotatedFolderName = getRotatedPath(fullName);
        File rotatedFolder = new File(rotatedFolderName);
        if (!rotatedFolder.isDirectory()) {
            try {
                Files.createDirectory(Paths.get(rotatedFolderName));
                LOG.log(Level.INFO, "Created rotated folder {0}", rotatedFolderName);
            } catch (IOException e) {
                throw new RuntimeException("Cannot create folder " + rotatedFolderName);
            }
        }
    }
}
