package com.zemiak.files.control;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import com.zemiak.nasphotos.files.control.MovieReader;
import com.zemiak.nasphotos.files.entity.PictureData;

import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * MovieReaderTest
 */
public class MovieReaderTest {

    // @Test
    public void testVideoProperties() throws IOException, SAXException, TikaException {
        File file = new File("/Users/vasko/Pictures/2010/1003 Matus/P1120694.MOV");
        PictureData data = new PictureData();

        MovieReader reader = new MovieReader();
        reader.setMovieInfo(file, data);

        assertTrue(data.getWidth() > 0, "Movie width > 0");
        assertTrue(data.getHeight() > 0, "Movie height > 0");

        System.out.println(file.getAbsolutePath() + ": width " + data.getWidth() + ", height " + data.getHeight());
    }
}
