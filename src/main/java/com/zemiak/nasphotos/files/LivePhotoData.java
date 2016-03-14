package com.zemiak.nasphotos.files;

public class LivePhotoData extends PictureData {
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imagePath) {
        this.imageUrl = imagePath;
    }
}
