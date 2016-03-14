package com.zemiak.nasphotos.thumbnails;

import com.zemiak.nasphotos.commandline.CommandLine;
import com.zemiak.nasphotos.files.MovieControl;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class MovieThumbnailCreator {
    private static final Logger LOG = Logger.getLogger(MovieThumbnailCreator.class.getName());

    @Inject private String ffmpegPath;
    @Inject private String tempPath;
    @Inject Boolean developmentSystem;
    @Inject MovieControl movies;
    @Inject ImageManipulation manipulator;

    public void create(Path original, String folder, String fileName) {
        Path outputPath = Paths.get(folder, fileName + ".jpg");
        String movieFileName = original.toAbsolutePath().toString();
        String imageFileName = outputPath.toAbsolutePath().toString();
        String ext = getExtension(imageFileName).toLowerCase();

        if (ext.equals("mp4") || ext.equals("m4v") || ext.equals("mov")) {
            createFfmpegThumbnail(movieFileName, imageFileName);

            try {
                if (movies.isLivePhotoMovieFile(original)) {
                    watermark(outputPath, "**>>");
                } else {
                    watermark(outputPath, "||>>");
                }
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Cannot watermark thumbnail for {0}", original.toString());
            }
        }
    }

    private String getExtension(String file) {
        int i = file.lastIndexOf(".");
        if (i == -1) {
            return "";
        }

        return file.substring(i + 1);
    }

    private void createFfmpegThumbnail(String movieFileName, String imageFileName) {
        final List<String> params = Arrays.asList(
            "-s", "180", "-i", movieFileName, "-o", imageFileName
        );

        try {
            CommandLine.execCmd(ffmpegPath, params);

            LOG.log(Level.INFO, "Generated thumbnail {0} ...", imageFileName);
        } catch (IllegalStateException | InterruptedException | IOException ex) {
            LOG.log(Level.SEVERE, "DID NOT generate thumbnail {0}: {1} ...",
                    new Object[]{imageFileName, ex});
        }
    }

    private void watermark(Path outputPath, String text) throws IOException {
        File source = outputPath.toFile();
        File target = File.createTempFile("w", ".jpg", new File(tempPath));

        manipulator.watermark(text, source, target);

        if (! source.delete()) {
            throw new IOException("Cannot delete " + source.toString());
        }

        Files.move(Paths.get(target.getAbsolutePath()), outputPath, StandardCopyOption.ATOMIC_MOVE);
    }
}
