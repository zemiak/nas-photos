package com.zemiak.nasphotos.files;

import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PictureControlTest {
    private final static String PHOTO_PATH = "/Volumes/media/Pictures/";

    @Test
    public void isNotImage() throws Exception {
        Arrays.asList(new String[]{
            "/Volumes/media/Pictures/YYMM Svadba/website/Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/2016-inbox/01/Miro iPhone/IMG_2302.JPG",
            "/Volumes/media/Pictures/Maruska - Janka Foto/2012-04/P1030310.JPG",
            "/Volumes/media/Pictures/_YYMM Svadba/website/Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/YYMM Svadba/_website/Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/YYMM Svadba/website/_Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/YYMM Svadba/website/Fotografie_files/_P4010496.jpg",
            "/Volumes/media/Pictures/.YYMM Svadba/website/Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/YYMM Svadba/.website/Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/YYMM Svadba/website/.Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/YYMM Svadba/website/Fotografie_files/.P4010496.jpg",
        }).stream().forEach(picFileName -> {
            assertFalse(picFileName, PictureControl.isImage(Paths.get(picFileName), PHOTO_PATH));
        });
    }

//    @Test
    public void isImage() throws Exception {
        Arrays.asList(new String[]{
            "/Volumes/media/Pictures/2014/1201 Deti/photo.Png",
            "/Volumes/media/Pictures/2089/1202 Vianoce/IMG_2302.JPG",
            "/Volumes/media/Pictures/2012/1203 Maruska/IMG_0126.JPG"
        }).stream().forEach(picFileName -> {
            assertTrue(picFileName, PictureControl.isImage(Paths.get(picFileName), PHOTO_PATH));
        });
    }

    @Test
    public void isNotHidden() throws Exception {
        Arrays.asList(new String[]{
            "/Volumes/media/Pictures/YYMM Svadba/website/Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/2016-inbox/01/Miro iPhone/IMG_2302.JPG",
            "/Volumes/media/Pictures/Maruska - Janka Foto/2012-04/P1030310.JPG",
            "/Volumes/media/Pictures/2014/01 Deti/photo.Png",
            "/Volumes/media/Pictures/2089/12 Vianoce/IMG_2302.JPG"
        }).stream().forEach(picFileName -> {
            assertFalse(picFileName, PictureControl.isHidden(Paths.get(picFileName)));
        });
    }

    @Test
    public void isHidden() throws Exception {
        Arrays.asList(new String[]{
            "/Volumes/media/Pictures/_YYMM Svadba/website/Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/YYMM Svadba/_website/Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/YYMM Svadba/website/_Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/YYMM Svadba/website/Fotografie_files/_P4010496.jpg",
            "/Volumes/media/Pictures/.YYMM Svadba/website/Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/YYMM Svadba/.website/Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/YYMM Svadba/website/.Fotografie_files/P4010496.jpg",
            "/Volumes/media/Pictures/YYMM Svadba/website/Fotografie_files/.P4010496.jpg"
        }).stream().forEach(picFileName -> {
            assertTrue(picFileName, PictureControl.isHidden(Paths.get(picFileName)));
        });
    }
}
