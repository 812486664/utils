package cn.shining.art.sys.tools;

import org.w3c.dom.Element;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;


public class ImageUtils {

    private static final Integer HORIZONTAL_WIDTH = 1280;
    private static final Integer HORIZONTAL_HEIGHT = 960;

    private static final Integer VERTICAL_WIDTH = 960;
    private static final Integer VERTICAL_HEIGHT = 1280;

    private static final Integer DEFAULT_WIDTH = 300;
    private static final Integer DEFAULT_HEIGHT = 300;


    /**
     * 预览图
     * 压缩图片
     * 竖版960 横版1280
     *
     * @param srcImagePath 读取图片路径
     * @param toImagePath  写入图片路径
     * @throws IOException
     */
    public static void reduceImage(String srcImagePath, String toImagePath) throws IOException {

        BufferedImage tag = null;
        try {
            BufferedImage src = ImageIO.read(new FileInputStream(srcImagePath));
            if(src.getHeight() > src.getWidth()){
                tag = reduce(src, src, 1);
            }else{
                tag = reduce(src, src, 0);
            }
            String suffix = srcImagePath.substring(srcImagePath.lastIndexOf("."));
            saveAsJPEG(100, tag, 1f, new FileOutputStream(toImagePath), suffix);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 以JPEG编码保存图片
     * @param dpi  分辨率
     * @param image_to_save  要处理的图像图片
     * @param JPEGcompression  压缩比
     * @param fos 文件输出流
     * @throws IOException
     */
    private static void saveAsJPEG(Integer dpi ,BufferedImage image_to_save, Float JPEGcompression, FileOutputStream fos, String suffix) throws IOException {

        ImageOutputStream ios  =  ImageIO.createImageOutputStream(fos);

        ImageWriter imageWriter = null;
        IIOMetadata imageMetaData = null;

        if (suffix.toLowerCase().contains("jpeg")  || suffix.toLowerCase().contains("jpg")){
            imageWriter  =  ImageIO.getImageWritersBySuffix("jpeg").next();
            imageWriter.setOutput(ios);
            //and metadata
            imageMetaData  =  imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image_to_save), null);

            if(dpi !=  null && !dpi.equals("")){

                Element tree  =  (Element) imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");
                Element jfif  =  (Element) tree.getElementsByTagName("app0JFIF").item(0);
                jfif.setAttribute("Xdensity", Integer.toString(dpi) );
                jfif.setAttribute("Ydensity", Integer.toString(dpi));
                jfif.setAttribute("resUnits", "1");
                imageMetaData.setFromTree("javax_imageio_jpeg_image_1.0", tree);
            }

            if(JPEGcompression >= 0 && JPEGcompression <= 1f){
                JPEGImageWriteParam jpegParams  =  (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
                jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
                jpegParams.setCompressionQuality(JPEGcompression);
            }

        }else{
            //png 压缩问题
            imageWriter  =  ImageIO.getImageWritersBySuffix("png").next();
            imageWriter.setOutput(ios);
            imageMetaData  =  imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image_to_save), null);
        }

        imageWriter.write(imageMetaData, new IIOImage(image_to_save, null, null), null);
        ios.close();
        imageWriter.dispose();


        fos.close();
    }


    /**
     * 递归压缩
     *
     * @param src 读取图片路径
     * @param tag  写入图片路径
     * @param code 1 竖版 0 宽版
     * @throws IOException
     */
    private static BufferedImage reduce(BufferedImage src, BufferedImage tag, int code) {
        int value_width, value_height;
        BigDecimal ratio = null;

        int width = src.getWidth();
        int height = src.getHeight();

        if (code == 1) {
            value_width = VERTICAL_WIDTH;
            value_height = VERTICAL_HEIGHT;
        } else {
            value_width = HORIZONTAL_WIDTH;
            value_height = HORIZONTAL_HEIGHT;
        }

        if (width > value_width || height > value_height) {

            if (width > value_width) {
                ratio = new BigDecimal(width).divide(new BigDecimal(value_width), 10, BigDecimal.ROUND_HALF_EVEN);
            } else {
                ratio = new BigDecimal(height).divide(new BigDecimal(value_height), 10,BigDecimal.ROUND_HALF_EVEN);
            }

            int widthRatio = new BigDecimal(width).divide(ratio, BigDecimal.ROUND_HALF_EVEN).intValue();
            int heightRatio = new BigDecimal(height).divide(ratio, BigDecimal.ROUND_HALF_EVEN).intValue();

            tag = new BufferedImage(widthRatio, heightRatio, BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage(src, 0, 0, widthRatio, heightRatio, null);

            tag = reduce(tag,tag,code);
        }
        return tag;
    }


    /**
     * 缩略图
     * 压缩图片
     * 默认处理结果300*300
     *
     * @param srcImagePath 读取图片路径
     * @param toImagePath  写入图片路径
     * @throws IOException
     */
    public static void reduceImageDefault(String srcImagePath, String toImagePath) throws IOException {
        reduceImage(srcImagePath, toImagePath, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 压缩图片
     *
     * @param srcImagePath 读取图片路径
     * @param toImagePath  写入图片路径
     * @param widthRatio   压缩后宽度
     * @param heightRatio  压缩后高度
     * @throws IOException
     */
    public static void reduceImage(String srcImagePath, String toImagePath, int widthRatio, int heightRatio) throws IOException {
        try {

            BufferedImage src = ImageIO.read(new FileInputStream(srcImagePath));
            BufferedImage tag = new BufferedImage(widthRatio, heightRatio, BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage(src, 0, 0, widthRatio, heightRatio, null);
            String suffix = srcImagePath.substring(srcImagePath.lastIndexOf("."));
            saveAsJPEG(100, tag, 1f, new FileOutputStream(toImagePath),suffix);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


/*
    public static void main(String[] agrs) {

        try {
            reduceImage("e://png.png", "e://png_1.png");
            reduceImageDefault("e://png.png", "e://png_x.png");
            reduceImage("e://jpg.jpg", "e://jpg_1.jpg");
            reduceImageDefault("e://jpg.jpg", "e://jpg_x.jpg");
            //reduceImage("e://2FE0B6C0-ED98-4A4A-AF64-DA4621CFF702.JPG", "e://xxx_1.JPG");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

}
