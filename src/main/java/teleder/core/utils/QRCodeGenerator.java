package teleder.core.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.mock.web.MockMultipartFile;
public class QRCodeGenerator {

    public static MultipartFile generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "png", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        byte[] imageInBytes = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return new MockMultipartFile("qr_code.png", "qr_code.png", "image/" + "png", imageInBytes);
    }

//    public static void saveQRCodeImage(String text, int width, int height, String filePath) throws WriterException, IOException {
//        BufferedImage qrCodeImage = generateQRCodeImage(text, width, height);
//        ImageIO.write(qrCodeImage, "PNG", new File(filePath));
//    }
}
