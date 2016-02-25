package com.zemiak.nasphotos.thumbnails;

import java.awt.geom.AffineTransform;

public class ImageInformation {
    private int orientation;
    private long width;
    private long height;

    public ImageInformation() {
        orientation = 1;
        width = -1;
        height = -1;
    }

    public ImageInformation(int orientation, long width, long height) {
        this.orientation = orientation;
        this.width = width;
        this.height = height;
    }

    public boolean isRotated() {
        return 1 != orientation;
    }

    public ImageInformation(ImageInformation info) {
        this();

        if (null == info) {
            return;
        }

        orientation = info.orientation;
        width = info.width;
        height = info.height;
    }

    @Override
    public String toString() {
        return String.format("%dx%d,%d", this.width, this.height, this.orientation);
    }

    public int getOrientation() {
        return orientation;
    }

    public long getWidth() {
        return width;
    }

    public long getHeight() {
        return height;
    }

    public AffineTransform getAffineTransform() {
        AffineTransform t = new AffineTransform();
        switch (orientation) {
            case 1:
                break;
            case 2:
                // Flip X
                t.scale(-1.0, 1.0);
                t.translate(-width, 0);
                break;
            case 3:
                // PI rotation
                t.translate(width, height);
                t.rotate(Math.PI);
                break;
            case 4:
                // Flip Y
                t.scale(1.0, -1.0);
                t.translate(0, -height);
                break;
            case 5:
                // - PI/2 and Flip X
                t.rotate(-Math.PI / 2);
                t.scale(-1.0, 1.0);
                break;
            case 6:
                // -PI/2 and -width
                t.translate(height, 0);
                t.rotate(Math.PI / 2);
                break;
            case 7:
                // PI/2 and Flip
                t.scale(-1.0, 1.0);
                t.translate(-height, 0);
                t.translate(0, width);
                t.rotate(3 * Math.PI / 2);
                break;
            case 8:
                // PI / 2
                t.translate(0, width);
                t.rotate(3 * Math.PI / 2);
                break;
        }
        return t;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public void setHeight(long height) {
        this.height = height;
    }
}
