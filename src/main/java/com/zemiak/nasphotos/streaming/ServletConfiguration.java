package com.zemiak.nasphotos.streaming;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class ServletConfiguration {
    @Inject String photoPath;

    public String getPhotoPath() {
        return photoPath;
    }
}
