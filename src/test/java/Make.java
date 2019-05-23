import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Queue;
import java.util.*;

@SuppressWarnings("all")
public class Make {

    private static final int factory = 165;
    private static final int BLACK = -16777216;
    private static final int WHITE = -1;

    private static File workDir = new File("C:\\Users\\Ban\\Desktop\\work");
    private static File imgSrcDir = new File(workDir, "src");
    private static File binaryDir = new File(workDir, "binary" + factory);
    private static File letterDir = new File(workDir, "letter" + factory);
    private static File learnDir = new File(workDir, "learn" + factory);

    private static File testImage = new File(imgSrcDir, "0024.jpg");

    @Before
    public void mkDir() {
        if (!workDir.exists()
                && !workDir.mkdir()
                && !imgSrcDir.exists()
                && !imgSrcDir.mkdir()
                && !binaryDir.exists()
                && !binaryDir.mkdir()
                && !letterDir.exists()
                && !letterDir.mkdir()
                && !learnDir.exists()
                && !learnDir.mkdir()
        ) {
            System.err.println("创建文件夹失败。");
        }
        System.out.println(Core.NATIVE_LIBRARY_NAME);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    public void rename() {
        for (File file : imgSrcDir.listFiles()) {
            String indexStr = file.getName()
                    .replace(".jpg", "")
                    .replace(".png", "");
            if (indexStr.length() >= 4) {
                continue;
            }
            int index = Integer.valueOf(indexStr);
//            System.out.printf("%04d\n", index);
            String name = String.format("%04d.jpg", index);
            File newFile = new File(imgSrcDir, name);
            System.out.printf(" %s rename ret: %b \n", file.getName(), file.renameTo(newFile));
        }
    }

    /**
     * 二值化验证码图片
     *
     * @param testImage
     */
//    @Test
    public void binary(File testImage) throws IOException {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        if (!binaryDir.exists() && !binaryDir.mkdir()) {
            System.out.println("目录不存在，且无法创建。");
            return;
        }

        FileUtils.cleanDirectory(binaryDir);

        Main.binary(imgSrcDir.getAbsolutePath(), binaryDir.getAbsolutePath(), 165);

    }

    /**
     * 分割图片
     */
    @Test
    public void splitLetter() throws Exception {
        for (File file : binaryDir.listFiles()) {
            Main.splitByLetter(file.getAbsolutePath(), letterDir);
        }
    }

    @Test
    public void deletUselessPic() {
        File dir = new File("C:\\Users\\Ban\\.cmcc\\dct-165-unknown");
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) continue;
            if (file.getName().contains("-01-") ||
                    file.getName().contains("-02-") ||
                    file.getName().contains("-03-")) {
                System.out.println("删除图片:" + file.getAbsolutePath() + " -> " + file.delete());
            }
        }
    }


    /**
     * 删除重复的 letter
     */
    @Test
    public void delRepeat() throws Exception {

        letterDir = new File("C:\\Users\\Ban\\.cmcc\\dct-165-unknown");

        Map<String, List<String>> map = new HashMap<>();
        for (File file : letterDir.listFiles()) {
            if (file.isDirectory()) continue;
            String key = file.getName().substring(0, file.getName().lastIndexOf("-"));
            List<String> files = map.get(key);
            if (files == null) {
                files = new ArrayList<>();
                map.put(key, files);
            }
            files.add(file.getAbsolutePath());
        }

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            ArrayList<BufferedImage> biList = new ArrayList<>();
            List<String> files = entry.getValue();
            for (String path : files) {
                boolean find = false;
                File img = new File(path);
                BufferedImage bufferedImage = ImageIO.read(img);
                for (BufferedImage biUni : biList) {
                    if (bufferedImagesEqual(biUni, bufferedImage)) {
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    biList.add(bufferedImage);
                } else {
                    System.out.println("删除图片:" + path + " -> " + img.delete());
                }
            }
        }
    }


    // 同宽高的比较相似度，大于 80% 则可以判断为同一字符

    /**
     * 给图片正确的值
     */
    @Test
    public void name() {

        String name = "b";
        File dir = new File("C:\\Users\\Ban\\Desktop\\temp");
        for (File f : dir.listFiles()) {
            f.renameTo(new File(dir, name + "-" + f.getName()));
        }

    }

    private boolean bufferedImagesEqual(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    if (img1.getRGB(x, y) != img2.getRGB(x, y))
                        return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 学习记录
     */
    @Test
    public void learn() throws Exception {
        for (File file : letterDir.listFiles()) {
            String letter = file.getName().substring(0, 1);
            System.out.println(letter);
            BufferedImage bi = ImageIO.read(file);
            int w = bi.getWidth();
            int h = bi.getHeight();
            int blackNum = 0;
            for (int x = 0; x < bi.getWidth(); ++x) {
                for (int y = 0; y < bi.getHeight(); ++y) {
                    if (BLACK == bi.getRGB(x, y)) {
                        ++blackNum;
                    } else {
                        if (WHITE != bi.getRGB(x, y)) {
                            System.err.println("非二值化图片");
                            System.exit(88);
                        }
                    }
                }
            }
            String name = String.format("%s-%d-%d-%d.png", letter, blackNum, w, h);
            file.renameTo(new File(learnDir, name));
        }
    }

    /**
     * 学习
     * 解析 letter，重命名为 [字母]-[黑点数量]-[图片宽度]-[图片高度]-[序列号].png
     */
    @Test
    public void learn2() throws IOException {
        // 先分组
        // 1.
        Map<String, List<String>> map = new HashMap<>();
        for (File file : letterDir.listFiles()) {
            String letter = file.getName().substring(0, 1);
            List<String> list = map.get(letter);
            if (list == null) {
                list = new ArrayList<>();
                map.put(letter, list);
            }
            list.add(file.getAbsolutePath());
        }

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String letter = entry.getKey();
            List<String> files = entry.getValue();
            for (int i = 0; i < files.size(); ++i) {
                File image = new File(files.get(i));
                BufferedImage bi = ImageIO.read(image);
                int w = bi.getWidth();
                int h = bi.getHeight();
                int blackNum = 0;
                for (int x = 0; x < bi.getWidth(); ++x) {
                    for (int y = 0; y < bi.getHeight(); ++y) {
                        if (BLACK == bi.getRGB(x, y)) {
                            ++blackNum;
                        } else {
                            if (WHITE != bi.getRGB(x, y)) {
                                System.err.println("非二值化图片");
                                System.exit(88);
                            }
                        }
                    }
                }
                String name = String.format("%s-%03d-%02d-%02d-%03d.png", letter, blackNum, w, h, i);
                File file = new File(learnDir, name);
                System.out.println(
                        String.format("重命名 %s -> %b", file.getAbsolutePath(), image.renameTo(file))
                );
            }
        }
    }

    //        @Test
    public Map<String, List<String>> group() throws Exception {

        Map<String, List<String>> map = new HashMap<>();

        for (File file : learnDir.listFiles()) {
            String name = file.getName();
            String bwh = name.substring(name.indexOf("-") + 1, name.lastIndexOf("-"));
            List<String> list = map.get(bwh);
            if (list == null) {
                list = new ArrayList<>();
                map.put(bwh, list);
            }
            list.add(file.getAbsolutePath());
        }

//        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
//            System.out.print(entry.getKey() + " -> ");
//            System.out.println(entry.getValue().size());
//        }

        return map;
    }

    @Test
    public void ocr() throws Exception {
        File[] images = new File("C:\\github\\tesseract-demo\\sample165").listFiles();
        int i = 0;
        for (File image : images) {
            if (ocrWithFile(image)) ++i;
        }
        System.out.println(String.format("%d/%d", i, images.length));
    }

    private static String yzm = "";

    private boolean ocrWithFile(File file) throws Exception {
        System.out.print("测试图片 -> " + file.getName());
        Map<String, List<String>> map = group();

        // 1. 拿到一张图片，灰度化，二值化，分割
        List<BufferedImage> letters = getLetters(file);

        System.out.print(" 分割得到字符数 " + letters.size());
        StringBuffer buffer = new StringBuffer();
        for (BufferedImage bi : letters) {
            List<Point> blackPoints = Main.getPoints(bi);
            int blackNum = blackPoints.size();
            int w = bi.getWidth();
            int h = bi.getHeight();
            String key = String.format("%03d-%02d-%02d", blackNum, w, h);
            List<String> paths = map.get(key);
            if (paths == null) {
//                System.err.println("不存在图规格:" + key);
                buffer.append("-");
                continue;
            }

            boolean find = false;
            for (String path : paths) {
                if (bufferedImagesEqual(ImageIO.read(new File(path)), bi)) {
                    buffer.append(path.substring(path.indexOf("-") - 1, path.indexOf("-")));
                    find = true;
                    break;
                }
            }
            if (!find) buffer.append("-");
        }
        String ret = buffer.toString();
        yzm = ret;
        boolean right = ret.equals(file.getName().substring(0, 4));
        System.out.println(" 识别结果 " + ret + " -> " + right);
        return right;
    }

    private List<BufferedImage> getLetters(File testImage) throws Exception {

        BufferedImage binaryImg = getBinary(testImage);
        if (binaryImg == null) {
            System.exit(999);
        }

        return splitImage(binaryImg);
    }

    private List<BufferedImage> splitImage(BufferedImage binaryImg) throws Exception {

        // 1. 去噪点
        BufferedImage bi = Main.removeNoise(binaryImg);

        // 2. 找出所有的黑点
        LinkedList<Point> points = Main.getPoints(bi);

        List<List<Point>> pointList = new ArrayList<>();

//        System.out.println("所有黑点总数: " + points.size());
        while (!points.isEmpty()) {

            Queue<Point> queue = new LinkedList();
            Point point = points.pop();
            queue.add(point);

            List<Point> innerList = new ArrayList();
            while (!queue.isEmpty()) {

                Point p = ((LinkedList<Point>) queue).pop();
//                System.out.println("起点[" + p.x + ", " + p.y + "]");
                innerList.add(p);

                List<Point> linkedPoints = Main.getLinkedPoints(points, p);
//                System.out.println("关联点数量:" + linkedPoints.size());

                points.removeAll(linkedPoints);
//                System.out.println("剩余点数:" + points.size());

                queue.addAll(linkedPoints);
//                System.out.println("待检索点数:" + points.size());
            }
            pointList.add(innerList);
        }

        pointList.sort(new Comparator<List<Point>>() {
            @Override
            public int compare(List<Point> p1, List<Point> p2) {

                int p1x = Integer.MAX_VALUE;
                for (Point point : p1) {
                    p1x = Math.min(p1x, point.x);
                }

                int p2x = Integer.MAX_VALUE;
                for (Point point : p2) {
                    p2x = Math.min(p2x, point.x);
                }

                return p1x - p2x;
            }
        });

        List<BufferedImage> ret = new ArrayList<>(pointList.size());

//        System.out.println("分割后图片数量为:" + pointList.size());
        for (List<Point> list : pointList) {
            // 如果黑点数小于 6 ，则可能是 i，j 字母中的那个点，删掉不影响识别
            if (list.size() <= 6) continue;
//            System.out.println("图片黑点数量为:" + list.size());

            // 1. 确定宽高
            int startX = Integer.MAX_VALUE;
            int startY = Integer.MAX_VALUE;
            int endX = Integer.MIN_VALUE;
            int endY = Integer.MIN_VALUE;
            for (Point point : list) {
                startX = Math.min(startX, point.x);
                startY = Math.min(startY, point.y);
                endX = Math.max(endX, point.x);
                endY = Math.max(endY, point.y);
            }
            int width = endX - startX + 1;
            int height = endY - startY + 1;

            BufferedImage subBi = bi.getSubimage(startX, startY, width, height);
            ret.add(subBi);
        }
//        System.out.println("所有图片黑点数量为:" + sum);

        return ret;
    }

    public BufferedImage getBinary(File image) {

        Imgcodecs imageCodecs = new Imgcodecs();

        // Source
        Mat matrix = imageCodecs.imread(image.getAbsolutePath());
        // convert to grayscale
        Mat grayImage = new Mat();
        Imgproc.cvtColor(matrix, grayImage, Imgproc.COLOR_BGR2GRAY);

        Mat dest = new Mat();
        Imgproc.threshold(grayImage, dest,
                factory, 255, Imgproc.THRESH_BINARY);

        // convert the matrix into a matrix of bytes appropriate for
        // this file extension
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode(".png", dest, mob);
        // convert the "matrix of bytes" into a byte array
        byte[] byteArray = mob.toArray();

        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            bufImage = null;
        }

        return bufImage;
    }

    private static final String API_URL = "http://sxapp.zj.chinamobile.com";

    @Test
    public void testOnline() throws Exception {

        OkHttpClient client = new OkHttpClient
                .Builder()
                .addInterceptor(new SaveCookies())
                .addInterceptor(new AddCookies())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ChinaMobile chinaMobile = retrofit.create(ChinaMobile.class);

        Call<ResponseBody> call = chinaMobile.verificationCode(
                String.valueOf(System.currentTimeMillis()));

        // 获取图片验证码。
        Response<ResponseBody> response;
        try {
            response = call.execute();
            ResponseBody body = response.body();
            if (body == null) {
                System.out.println("网络请求验证码失败，重新请求。");
            }
            body.byteStream();
            BufferedImage bi = ImageIO.read(body.byteStream());
            File image = new File(System.currentTimeMillis() + ".jpg");
            ImageIO.write(bi, "jpg", image);
            Thread.sleep(3);
            ocrWithFile(image);

            if (yzm.contains("-") || yzm.contains("_") || yzm.length() < 4) {
                System.out.println("识别验证码错误");
                return;
            }

            // 获取图片验证码。
            call = chinaMobile.query("13575586999", "15857581084", yzm);
            try {
                response = call.execute();
                body = response.body();
                if (body == null) {
                    System.out.println("网络请求验证码失败，重新请求。");
                }
                System.out.println(body.string());
            } catch (Exception e) {
                System.out.println("网络请求验证码失败，重新请求。");
            }

        } catch (Exception e) {
            System.out.println("网络请求验证码失败，重新请求。");
        }
    }

    private HashSet<String> cookies = new HashSet<>();

    private class SaveCookies implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                cookies = new HashSet<>();
                cookies.addAll(originalResponse.headers("Set-Cookie"));
            }
            return originalResponse;
        }
    }

    private class AddCookies implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            String SESSION = "SESSION_COOKIE=sx-sqjl.8c7ffd0d-56b8-11e9-9363-02421894b58a;";
            builder.addHeader("Cookie", SESSION);
            for (String cookie : cookies) {
                builder.addHeader("Cookie", cookie);
            }
            return chain.proceed(builder.build());
        }
    }

    // {"msg":"验证码有误","size":0,"success":false}
}
