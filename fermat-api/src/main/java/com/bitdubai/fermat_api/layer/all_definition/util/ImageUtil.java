package com.bitdubai.fermat_api.layer.all_definition.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

/**
 * The Class <code>com.bitdubai.fermat_api.layer.all_definition.util.ImageUtil</code> It provides some
 * basic functionality for working with images
 * <p/>
 *
 * @author Roberto Requena (rart3001@gmail.com) on 24/05/16.
 * @version 1.0
 * @since Java JDK 1.7
 */
public class ImageUtil {

    /**
     * Represent the PNG_FORMAT
     */
    public static final String PNG_FORMAT = "PNG";

    /**
     * Represent the GIF_FORMAT
     */
    public static final String GIF_FORMAT = "GIF";

    /**
     * Represent the JPG_FORMAT
     */
    public static final String JPG_FORMAT = "JPG";

    /**
     * Represent the JPEG_FORMAT
     */
    public static final String JPEG_FORMAT = "JPEG";

    /**
     * Represent the TIFF_FORMAT
     */
    public static final String TIFF_FORMAT = "TIFF";

    /**
     * Represent the HIGH_QUALITY (0.9)
     */
    public static final float HIGH_QUALITY = new Float(0.9f);

    /**
     * Represent the MEDIUM_QUALITY (0.5)
     */
    public static final float MEDIUM_QUALITY = new Float(0.5f);

    /**
     * Represent the LOWER_QUALITY (0.2)
     */
    public static final float LOW_QUALITY = new Float(0.2f);

    /**
     * Convert the bytes size of the image into a Human Readable String
     *
     * @param bytes
     * @return String
     */
    public static String bytesIntoHumanReadable(long bytes) {

        long kilobyte = 1024;
        long megabyte = kilobyte * 1024;
        long gigabyte = megabyte * 1024;
        long terabyte = gigabyte * 1024;

        if ((bytes >= 0) && (bytes < kilobyte)) {
            return bytes + " B";

        } else if ((bytes >= kilobyte) && (bytes < megabyte)) {
            return (bytes / kilobyte) + " KB";

        } else if ((bytes >= megabyte) && (bytes < gigabyte)) {
            return (bytes / megabyte) + " MB";

        } else if ((bytes >= gigabyte) && (bytes < terabyte)) {
            return (bytes / gigabyte) + " GB";

        } else if (bytes >= terabyte) {
            return (bytes / terabyte) + " TB";

        } else {
            return bytes + " Bytes";
        }
    }


    /**
     * This method resize a image to the pass with and height for
     * parameters
     *
     * @param originalImage the image
     * @param newWidth
     * @param newHeight
     * @return BufferedImage
     */
    private static BufferedImage resize(BufferedImage originalImage, int newWidth, int newHeight){

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resizedImage;
    }


    /**
     * Compress a image to the specify quality
     *
     * @param originalImage the image
     * @param format of the image (PNG, JGP ...)
     * @param quality a number between 0.0001 and 1 where 0 is the max compression
     * @throws IOException when io exception occurs
     * @throws IllegalStateException when no compatible format writers found
     */
    public static BufferedImage compress(BufferedImage originalImage, String format, float quality) throws IOException {

        ByteArrayOutputStream bos = null;
        ImageOutputStream ios = null;
        InputStream inputStream = null;

        try{

            if (quality <= 0){
                throw new IllegalArgumentException("The quality must be greater than zero (0)");
            }

            if (quality >= 1){
                throw new IllegalArgumentException("The quality should be less than one (1)");
            }

            /*
             * Get a ImageWriter for the format.
             */
            Iterator<ImageWriter> writers = ImageIO.getImageWritersBySuffix(format);
            if (!writers.hasNext()){
                throw new IllegalStateException("No writers found for the specified format "+format);
            }

            /*
             * Get a image writer
             */
            ImageWriter writer = writers.next();

            /*
             * Create the ImageWriteParam to compress the image.
             */
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);

            /*
             *  The output will be a ByteArrayOutputStream (in memory)
             */
            bos = new ByteArrayOutputStream((int) ImageUtil.getSize(originalImage, format));
            ios = ImageIO.createImageOutputStream(bos);
            writer.setOutput(ios);
            writer.write(null, new IIOImage(originalImage, null, null), param);
            ios.flush();

            /*
             * Convert byte array back to BufferedImage
             */
            inputStream = new ByteArrayInputStream(bos.toByteArray());
            BufferedImage compressImage = ImageIO.read(inputStream);

            return compressImage;

        }finally {

            if (ios != null)
                ios.close();
            if (bos != null)
                bos.close();
            if (inputStream != null)
                inputStream.close();

        }

    }

    /**
     * Get the size of the BufferedImage
     *
     * @param originalImage
     * @param format
     * @return long
     * @throws IOException
     */
    public static long getSize(BufferedImage originalImage, String format) throws IOException {

        ByteArrayOutputStream temporal = new ByteArrayOutputStream();
        ImageIO.write(originalImage, format, temporal);
        temporal.close();
        long size = temporal.size();
        temporal.close();

        return size;
    }


    /**
     * Get the bytes array that represent the image
     *
     * @param originalImage
     * @param format
     * @return byte []
     * @throws IOException
     */
    public static  byte [] getByteArray(BufferedImage originalImage, String format) throws IOException {

        ByteArrayOutputStream temporal = new ByteArrayOutputStream();
        ImageIO.write(originalImage, format, temporal);
        temporal.close();
        byte [] bytes = temporal.toByteArray();
        temporal.close();

        return bytes;
    }

    /**
     * Create a BufferedImage from byte array
     *
     * @param data
     * @return BufferedImage
     * @throws IOException
     */
    public static BufferedImage getBufferedImage(byte [] data) throws IOException {

        InputStream inputStream = new ByteArrayInputStream(data);
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        inputStream.close();

        return bufferedImage;
    }


    public static void main(String [] args){

        try {

            File file = new File("/home/rrequena/Imágenes/Test/original_image.jpg");
            BufferedImage originalImage = ImageIO.read(file);

            System.out.println("Original image Size = " + ImageUtil.bytesIntoHumanReadable(file.length()));

            BufferedImage resizeImage = ImageUtil.resize(originalImage, 100, 100);
            System.out.println("Resize image Size = " + ImageUtil.bytesIntoHumanReadable(getSize(originalImage, JPG_FORMAT)));
            ImageIO.write(resizeImage, JPG_FORMAT, new File("/home/rrequena/Imágenes/Test/resize_image.jpg"));

            BufferedImage compressImageHigh = ImageUtil.compress(originalImage, JPG_FORMAT, HIGH_QUALITY);
            System.out.println("Compress high quality image Size = " + ImageUtil.bytesIntoHumanReadable(getSize(compressImageHigh, JPG_FORMAT)));
            ImageIO.write(compressImageHigh, JPG_FORMAT, new File("/home/rrequena/Imágenes/Test/compress_high_image.jpg"));

            BufferedImage compressMediumImage = ImageUtil.compress(originalImage, JPG_FORMAT, MEDIUM_QUALITY);
            System.out.println("Compress medium quality image Size = " + ImageUtil.bytesIntoHumanReadable(getSize(compressMediumImage, JPG_FORMAT)));
            ImageIO.write(compressMediumImage, JPG_FORMAT, new File("/home/rrequena/Imágenes/Test/compress_medium_image.jpg"));

            BufferedImage compressLowImage = ImageUtil.compress(originalImage, JPG_FORMAT, LOW_QUALITY);
            System.out.println("Compress low quality image Size = " + ImageUtil.bytesIntoHumanReadable(getSize(compressLowImage, JPG_FORMAT)));
            ImageIO.write(compressLowImage, JPG_FORMAT, new File("/home/rrequena/Imágenes/Test/compress_low_image.jpg"));

            BufferedImage compressCustomImage = ImageUtil.compress(originalImage, JPG_FORMAT, new Float(0.001f));
            System.out.println("Compress custom quality image Size = " + ImageUtil.bytesIntoHumanReadable(getSize(compressCustomImage, JPG_FORMAT)));
            ImageIO.write(compressCustomImage, JPG_FORMAT, new File("/home/rrequena/Imágenes/Test/compress_custom_image.jpg"));

            BufferedImage resizeCompressImage = ImageUtil.resize(compressImageHigh, 100, 100);
            System.out.println("Resize and compress image Size = " + ImageUtil.bytesIntoHumanReadable(getSize(resizeCompressImage, JPG_FORMAT)));
            ImageIO.write(resizeCompressImage, JPG_FORMAT, new File("/home/rrequena/Imágenes/Test/resize_compress_image.jpg"));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
