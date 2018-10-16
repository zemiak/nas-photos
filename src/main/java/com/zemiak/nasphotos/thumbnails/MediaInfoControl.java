package com.zemiak.nasphotos.thumbnails;

import com.zemiak.nasphotos.commandline.CommandLine;
import com.zemiak.nasphotos.configuration.ConfigurationProvider;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MediaInfoControl {
    private static final Logger LOG = Logger.getLogger(MediaInfoControl.class.getName());
    private static final String ROTATED = "Rotation";

    final private String mediaInfoPath = ConfigurationProvider.getMediaInfoPath();

    public List<String> getMediaInfo(Path path) {
        List<String> params = Arrays.asList(
            path.toAbsolutePath().toString()
        );

        List<String> results = Collections.EMPTY_LIST;
        try {
            results = CommandLine.execCmd(mediaInfoPath, params);
        } catch (IOException | InterruptedException | IllegalStateException ex) {
            LOG.log(Level.SEVERE, "Cannot run mediainfo for " + path.toString(), ex);
        }

        return results;
    }

    public boolean isRotated(Path path) {
        List<String> results = getMediaInfo(path);
        return results.stream().filter(line -> line.contains(ROTATED)).findAny().isPresent();
    }
}
