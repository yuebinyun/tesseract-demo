import net.coobird.thumbnailator.Thumbnails;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

@SuppressWarnings("all")
public class Main {

    public static void main(String[] args) throws Exception {

        List<BufferedImage> images = Thumbnails
                .of(new File("sample4").listFiles())
                // 对图片进行裁剪，去掉四周的黑框，然后放大一倍。
//                .sourceRegion(1, 1, 88, 18)
                .size(180, 40)
                .outputFormat("jpg")
                .asBufferedImages();

        ITesseract instance = new Tesseract();
        instance.setDatapath(".");
        instance.setLanguage("eng");
//        instance.setLanguage("fontyp");
//        instance.setLanguage("zwp");

        for (BufferedImage image : images) {
            System.out.println(
                    instance.doOCR(image).replace("\n", ""));
//            String result = instance.doOCR(image).substring(0, 3);
//            int a = Integer.valueOf(String.valueOf(result.charAt(0)));
//            char op = result.charAt(1);
//            int b = Integer.valueOf(String.valueOf(result.charAt(2)));
//            int ret = 9999;
//            if (op == 'X') {
//                ret = a * b;
//            } else if (op == '+') {
//                ret = a + b;
//            } else if (op == '-') {
//                ret = a - b;
//            }
//            System.out.println(result + "=" + ret);
        }
    }
}