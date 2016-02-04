package com.zemiak.nasphotos.files;

import java.nio.file.Paths;
import java.util.Arrays;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class FileServiceTest {
    FileService service;

    @Before
    public void setUp() {
        service = new FileService();
        service.photoPath = "/Volumes/media/Pictures/";
    }

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
            assertFalse(picFileName, FileService.isImage(Paths.get(picFileName), service.photoPath));
        });
    }

    @Test
    public void isImage() throws Exception {
        Arrays.asList(new String[]{
            "/Volumes/media/Pictures/2014/1201 Deti/photo.Png",
            "/Volumes/media/Pictures/2089/1202 Vianoce/IMG_2302.JPG",
            "/Volumes/media/Pictures/2012/1203 Maruska/IMG_0126.JPG"
        }).stream().forEach(picFileName -> {
            assertTrue(picFileName, FileService.isImage(Paths.get(picFileName), service.photoPath));
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
            assertFalse(picFileName, FileService.isHidden(Paths.get(picFileName)));
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
            assertTrue(picFileName, FileService.isHidden(Paths.get(picFileName)));
        });
    }
}
