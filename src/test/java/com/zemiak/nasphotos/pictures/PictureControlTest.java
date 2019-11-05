package com.zemiak.nasphotos.pictures;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.Arrays;

import com.zemiak.nasphotos.files.control.PictureControl;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class PictureControlTest {
    private final static String PHOTO_PATH = "/Volumes/media/Pictures/";

    @Test
    public void isNotImage() throws Exception {
        Arrays.asList(new String[]{
            PHOTO_PATH + "YYMM Svadba/website/Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "2016-inbox/01/Miro iPhone/IMG_2302.JPG",
            PHOTO_PATH + "Maruska - Janka Foto/2012-04/P1030310.JPG",
            PHOTO_PATH + "_YYMM Svadba/website/Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "YYMM Svadba/_website/Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "YYMM Svadba/website/_Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "YYMM Svadba/website/Fotografie_files/_P4010496.jpg",
            PHOTO_PATH + ".YYMM Svadba/website/Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "YYMM Svadba/.website/Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "YYMM Svadba/website/.Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "YYMM Svadba/website/Fotografie_files/.P4010496.jpg",
        }).stream().forEach(picFileName -> {
            assertFalse(PictureControl.isImage(Paths.get(picFileName), PHOTO_PATH), picFileName);
        });
    }

//    @Test
    public void isImage() throws Exception {
        Arrays.asList(new String[]{
            PHOTO_PATH + "2014/1201 Deti/photo.Png",
            PHOTO_PATH + "2089/1202 Vianoce/IMG_2302.JPG",
            PHOTO_PATH + "2012/1203 Maruska/IMG_0126.JPG"
        }).stream().forEach(picFileName -> {
            assertTrue(PictureControl.isImage(Paths.get(picFileName), PHOTO_PATH), picFileName);
        });
    }

    @Test
    public void isNotHidden() throws Exception {
        Arrays.asList(new String[]{
            PHOTO_PATH + "YYMM Svadba/website/Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "2016-inbox/01/Miro iPhone/IMG_2302.JPG",
            PHOTO_PATH + "Maruska - Janka Foto/2012-04/P1030310.JPG",
            PHOTO_PATH + "2014/01 Deti/photo.Png",
            PHOTO_PATH + "2089/12 Vianoce/IMG_2302.JPG"
        }).stream().forEach(picFileName -> {
            assertFalse(PictureControl.isHidden(Paths.get(picFileName)), picFileName);
        });
    }

    @Test
    public void isHidden() throws Exception {
        Arrays.asList(new String[]{
            PHOTO_PATH + "_YYMM Svadba/website/Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "YYMM Svadba/_website/Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "YYMM Svadba/website/_Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "YYMM Svadba/website/Fotografie_files/_P4010496.jpg",
            PHOTO_PATH + ".YYMM Svadba/website/Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "YYMM Svadba/.website/Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "YYMM Svadba/website/.Fotografie_files/P4010496.jpg",
            PHOTO_PATH + "YYMM Svadba/website/Fotografie_files/.P4010496.jpg"
        }).stream().forEach(picFileName -> {
            assertTrue(PictureControl.isHidden(Paths.get(picFileName)), picFileName);
        });
    }
}
