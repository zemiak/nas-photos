package com.zemiak.nasphotos.thumbnails.control;

import javax.enterprise.context.Dependent;

@Dependent
public class Thumbnailer {
    public static final String SUBFOLDER_THUMBNAILED = "rotated";

    public void createOrUpdate(String fullPath) {
        /**
          * protected static BufferedImage load(String name) {
		BufferedImage i = null;

		try {
			i = ImageIO.read(AbstractScalrTest.class.getResource(name));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return i;
	}
          */
    }
}
