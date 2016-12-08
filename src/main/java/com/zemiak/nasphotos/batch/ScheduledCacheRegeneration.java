package com.zemiak.nasphotos.batch;

import com.zemiak.nasphotos.files.CacheDataReader;
import com.zemiak.nasphotos.lookup.ConfigurationProvider;
import com.zemiak.nasphotos.thumbnails.ThumbnailService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ScheduledCacheRegeneration {
    private static final Logger LOG = Logger.getLogger(ScheduledCacheRegeneration.class.getName());

    final private String tempPath = ConfigurationProvider.getTempPath();

    @Inject CacheDataReader cache;
    @Inject ThumbnailService thumbnails;

    @Schedule(hour = "1", minute = "5", persistent = false)
    public void refreshImageCache() {
        try {
            Files.createDirectories(Paths.get(tempPath));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot create the thumbnail folder " + tempPath, ex);
            return;
        }

        thumbnails.createThumbnails();

        cache.clearVersion();
        cache.reload();
    }
}
