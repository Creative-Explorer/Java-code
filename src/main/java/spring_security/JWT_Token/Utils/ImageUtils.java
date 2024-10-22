package spring_security.JWT_Token.Utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static byte[] compressImage(byte[] imageData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        BufferedImage bufferedImage = ImageIO.read(bais);
        if (bufferedImage == null) {
            throw new IOException("Failed to decode image data");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();

        // Set the compression quality (0.0 to 1.0)
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(0.75f); // You can adjust this value

        imageWriter.setOutput(ImageIO.createImageOutputStream(baos));
        imageWriter.write(null, new javax.imageio.IIOImage(bufferedImage, null, null), imageWriteParam);
        imageWriter.dispose();

        return baos.toByteArray();
    }
}
