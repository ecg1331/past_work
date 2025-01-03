package edu.uchicago.gerber._08final.mvc.controller;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;

/*
Place all .png image assets in this directory src/main/resources/imgs or its subdirectories.
All raster images loaded in static context prior to runtime.
 */
public class ImageLoader {

    private static Map<String, BufferedImage> IMAGE_MAP = null;
    static {
            Path rootDirectory = Paths.get("src/main/resources/imgs");
            Map<String, BufferedImage> localMap = null;
            try {
                localMap = loadPngImages(rootDirectory);
            } catch (IOException e) {
                e.fillInStackTrace();
            }
            IMAGE_MAP = localMap;
    }

    /*
     Walks the directory and sub-directories at root src/main/resources/imgs and returns a Map<String, BufferedImage>
     of images in that file hierarcy.
     */
    private static Map<String, BufferedImage> loadPngImages(Path rootDirectory) throws IOException {
        Map<String, BufferedImage> pngImages = new HashMap<>();
        Files.walkFileTree(rootDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.toString().toLowerCase().endsWith(".png")
                        && !file.toString().toLowerCase().contains("do_not_load.png")) {
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file.toFile());
                        if (bufferedImage != null) {
                            pngImages.put(file.toString().toLowerCase().replace("\\", "/").substring(18), bufferedImage);
                        }
                    } catch (IOException e) {
                        e.fillInStackTrace();
                    }
                }
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                // Handle the error here if necessary
                return FileVisitResult.CONTINUE;
            }
        });
        return pngImages;
    }

    // resizes the large kitty png
    public static BufferedImage resize(BufferedImage image) {
        int newWidth = 350;
        int newHeight = 350;

        BufferedImage resizedKitty = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedKitty.createGraphics();
        g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        return resizedKitty;
    }

    //fetch the image from existing static map
    public static BufferedImage getImage(String imagePath) {
            return IMAGE_MAP.get(imagePath.toLowerCase());
    }
}
