package com.zemiak.nasphotos.files;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;

@Singleton
public class VersionService {
    @Inject
    String tempPath;

    private String version;

    @PostConstruct
    public void clearVersion() {
        version = null;
    }

    public String getVersion() {
        if (null == version) {
            buildVersion();
        }

        return version;
    }

    private void buildVersion() {
        Long count;

        try {
            count = Files.walk(Paths.get(tempPath), FileVisitOption.FOLLOW_LINKS)
                .filter(path -> !path.toFile().isDirectory())
                .filter(path -> path.toFile().canRead())
                .collect(Collectors.counting());
        } catch (IOException ex) {
            count = -1L;
        }

        version = String.valueOf(count);
    }
}
