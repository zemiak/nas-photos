package com.zemiak.nasphotos.files;

import java.util.Objects;
import javax.json.Json;
import javax.json.JsonObject;

public class PictureData {
    private String id;
    private String title;
    private long width;
    private long height;
    private int orientation;
    private int ratioWidth = 4;
    private int ratioHeight = 3;
    private String type = "picture";

    public PictureData() {
    }

    public PictureData(PictureData old) {
        this.id = old.getId();
        this.title = old.getTitle();
        this.width = old.getWidth();
        this.height = old.getHeight();
        this.orientation = old.getOrientation();
        this.type = old.getType();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PictureData other = (PictureData) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public int getRatioWidth() {
        return ratioWidth;
    }

    public void setRatioWidth(int ratioWidth) {
        this.ratioWidth = ratioWidth;
    }

    public int getRatioHeight() {
        return ratioHeight;
    }

    public void setRatioHeight(int ratioHeight) {
        this.ratioHeight = ratioHeight;
    }

    public JsonObject toJson() {
        return Json.createObjectBuilder()
                .add("id", this.getId())
                .add("title", this.getTitle())
                .add("width", this.getWidth())
                .add("height", this.getHeight())
                .add("orientation", this.getOrientation())
                .add("ratioWidth", this.getRatioWidth())
                .add("ratioHeight", this.getRatioHeight())
                .add("type", this.getType())
                .build();
    }
}
