import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

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
import java.util.concurrent.ArrayBlockingQueue;

@SuppressWarnings("all")
public class Main {

    private static File workDir = new File("C:\\Users\\Ban\\Desktop\\work");
    private static File imgSrcDir = new File(workDir, "src");
    private static File binary165 = new File(workDir, "binary165");

    public static void ocr165() throws Exception {

        ITesseract instance = new Tesseract();
        instance.setDatapath(".");
        instance.setLanguage("fontyp");

        int i = 0;
        File[] files = new File("sample165").listFiles();
        Arrays.sort(files);
        for (File file : files) {
            String a = "[" + file.getName()
                    .replace(".png", "")
                    .replace(".jpg", "")
                    + "]";
            a = a.toLowerCase();
            String ret = "[" + instance.doOCR(file)
                    .replace("\n", "")
                    .replace(" ", "").toLowerCase() + "]";
            boolean right = a.toLowerCase().equals(ret.toLowerCase());
            System.out.println(a + ret + right);
            if (right) ++i;
        }
        System.out.println("准确率:[" + i + "/50]");
    }

    public static void main(String[] args) throws Exception {

        System.out.println(Core.NATIVE_LIBRARY_NAME);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

//
        binary(imgSrcDir.getAbsolutePath(), binary165.getAbsolutePath(), 165);

//        binary("C:\\Users\\Ban\\Desktop\\temp1\\jpg\\50.jpg", 165);

//        print("C:\\Users\\Ban\\Desktop\\temp1\\images\\out\\100.jpg.png");

//        ocr(dst);
//        full("C:\\Users\\Ban\\Desktop\\letters\\new");
//        ocr165();

//        getColor("C:\\github\\tesseract-demo\\sample165\\2xct.png");
//        getColor("C:\\github\\tesseract-demo\\sample165\\ayqw.png");

//        splitByLetter("C:\\github\\tesseract-demo\\sample165\\ayqw.png");
//        splitByLetter("C:\\github\\tesseract-demo\\sample165\\ewa1.png");

//        for (File file : new File("C:\\Users\\Ban\\Desktop\\temp1\\binary165")
//                .listFiles()) {
//            splitByLetter(file.getAbsolutePath());
//        }

//        forEachDir("C:\\Users\\Ban\\Desktop\\letters");
    }


    /**
     * 将 src 目录下的所有图片 二值化 到 dst 目录下
     *
     * @param src    源目录
     * @param dst    目标目录
     * @param thresh 阈值
     */
    public static void binary(String src, String dst, double thresh) {

        for (File file : new File(src).listFiles()) {

            System.out.println("二值化 " + file.getName());

            Imgcodecs imageCodecs = new Imgcodecs();
            // Source
            Mat matrix = imageCodecs.imread(file.getAbsolutePath());
            // convert to grayscale
            Mat grayImage = new Mat();
            Imgproc.cvtColor(matrix, grayImage, Imgproc.COLOR_BGR2GRAY);

            Mat dest = new Mat();
            Imgproc.threshold(grayImage, dest,
                    thresh, 255, Imgproc.THRESH_BINARY);

            imageCodecs.imwrite(
                    new File(dst, file.getName()
                            .replace(".jpg", ".png"))
                            .getAbsolutePath(),
                    dest);
        }
    }

    public static void binary(String filePath, double thresh) {

        System.out.println(filePath);

        Imgcodecs imageCodecs = new Imgcodecs();
        // Source
        Mat matrix = imageCodecs.imread(filePath);
        // convert to grayscale
        Mat grayImage = new Mat();
        Imgproc.cvtColor(matrix, grayImage, Imgproc.COLOR_BGR2GRAY);

        Mat dest = new Mat();
        Imgproc.threshold(grayImage, dest,
                thresh, 255, Imgproc.THRESH_BINARY);

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

            for (int y = 0; y < bufImage.getHeight(); ++y) {
                for (int x = 0; x < bufImage.getWidth(); ++x) {
                    int v = bufImage.getRGB(x, y);
                    if (v == -1) {
                        System.out.print("0");
                    } else if (v == -16777216) {
                        System.out.print("*");
                    } else {
                        System.out.print("+");
                    }
                }
                System.out.println("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印
     *
     * @param path
     * @throws IOException
     */
    public static void print(String path) throws IOException {

        BufferedImage bufImage = ImageIO.read(new File(path));

        for (int y = 0; y < bufImage.getHeight(); ++y) {
            for (int x = 0; x < bufImage.getWidth(); ++x) {
                int v = bufImage.getRGB(x, y);
                if (v == -1) {
                    System.out.print("0");
                } else if (v == -16777216) {
                    System.out.print("*");
                } else {
                    System.out.print("+");
                }
            }
            System.out.println("");
        }
    }

    /**
     * ocr 图像识别
     *
     * @param src 目标目录
     */
    public static void ocr(String src) throws TesseractException {

        ITesseract instance = new Tesseract();
        instance.setDatapath(".");
        instance.setLanguage("fontyp");

        for (File file : new File(src).listFiles()) {
            if (file.getName().contains(".png")) continue;
            System.out.println(file.getAbsolutePath());
            String ret = instance.doOCR(file);
            ret = ret.replace("\n", "")
                    .replace(" ", "");
            File dstFile = new File(src, ret + ".png");
            System.out.println(dstFile.getAbsolutePath());
            file.renameTo(dstFile);
        }
    }


    /**
     * 将图片放到白色背景里面
     *
     * @param src
     * @throws IOException
     */
    public static void full(String src) throws IOException {

        String foo = "C:\\Users\\Ban\\Desktop\\temp1\\binary168\\abc.jpg";
        int temp = foo.lastIndexOf(".") + 1;
        System.out.println(foo.substring(temp));

        for (File file : new File(src).listFiles()) {

            System.out.println(file.getAbsolutePath());

            String name = file.getName();

            // 读取底图
            File bottom = new File("bottom.bmp");
            BufferedImage bottomImg = ImageIO.read(bottom);
            Graphics2D g2d = bottomImg.createGraphics();

            // 读取上层图片
            BufferedImage waterImg = ImageIO.read(file);
            int waterImgWidth = waterImg.getWidth();// 获取层图的宽度
            int waterImgHeight = waterImg.getHeight();// 获取层图的高度

            // 在图形和图像中实现混合和透明效果
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0F));
            // 绘制
            g2d.drawImage(waterImg, 10, 10,
                    waterImgWidth, waterImgHeight, null);
            g2d.dispose();// 释放图形上下文使用的系统资源

            ImageIO.write(bottomImg, "png", new File(src, "000" + name));
        }
    }

    /**
     * TODO 待整理
     * 打印图片所有点的颜色值
     *
     * @param imagePath
     */
    public static void getColor(String imagePath) throws IOException {

        BufferedImage bi = removeNoise(imagePath);

        int left = -1;
        List<Integer> list = new ArrayList<>();
        for (int w = 0; w < bi.getWidth(); ++w) {
            boolean clear = true;
            for (int h = 0; h < bi.getHeight(); ++h) {
                if (bi.getRGB(w, h) != -1) {
                    // 表明此列有黑点，不能切割
                    clear = false;
                    break;
                }
            }

            if (clear) {
                if (w - left != 1) {
                    // 第 w 列刚离开有字区域
                    list.add(w);
                }
                // 分割线右移一格
                left = w;
            } else {
                if (w - left == 1) {
                    // 第 w 列刚进入有字区域
                    list.add(left);
                }
            }
        }

        System.out.println("------------------------------------------" +
                "------------------------------------------------");

        System.out.println("");
        System.out.println("分割列: " + list);
        System.out.println("");

        System.out.println("-------------------------------------------" +
                "-----------------------------------------------");


        List<Point> pointListV = new ArrayList<>();
//        for (int h = 0; h < bi.getHeight(); ++h) {
//            for (int i : list) {
//                Point point = new Point(i, h);
//                pointListV.add(point);
//            }
//        }

        for (int j = 0; j < bi.getHeight(); ++j) {
            for (int i = 0; i < bi.getWidth(); ++i) {

                boolean ce = false;
                for (Integer border : list) {
                    if (border.equals(i)) {
                        ce = true;
                        System.out.print("|");
                        break;
                    }
                }
                if (ce) continue;

                int v = bi.getRGB(i, j);
                if (v == -1) {
                    System.out.print("0");
                } else if (v == -16777216) {
                    System.out.print("*");
                } else {
                    System.out.print("+");
                }
            }
            System.out.println("");
        }


        Queue<Integer> borderQueue = new ArrayBlockingQueue<>(list.size(), false, list);
        if (borderQueue.size() % 2 != 0) {
            System.err.println("分割错误了。");
            return;
        }

        List<Point> pointListH = new ArrayList<>();
        int size = borderQueue.size();
        int index = 0;
        while (!borderQueue.isEmpty()) {

            int leftBorder = borderQueue.poll();
            int rightBorder = borderQueue.poll();

            int top = 0;
            // 先从上往下扫，找到上边界
            for (int h = 0; h < bi.getHeight(); ++h) {
                boolean clear = true;
                for (int w = leftBorder; w < rightBorder; ++w) {
                    if (bi.getRGB(w, h) != -1) {
                        // 表明此行有黑点，不能切割
                        top = h - 1;
                        clear = false;
                        break;
                    }
                }
                if (!clear) break;
            }
            top = Math.max(top, 0);

            int bottom = 0;
            // 再从下往上扫，找到上边界
            for (int h = bi.getHeight() - 1; h > 0; --h) {
                boolean clear = true;
                for (int w = leftBorder; w < rightBorder; ++w) {
                    if (bi.getRGB(w, h) != -1) {
                        // 表明此行有黑点，不能切割
                        bottom = h + 1;
                        clear = false;
                        break;
                    }
                }
                if (!clear) break;
            }
            bottom = Math.min(bottom, bi.getHeight());

            String ret = String.format("left : %d, right : %d, top : %d, bottom : %d",
                    leftBorder, rightBorder, top, bottom);


            for (int h = top; h < bottom; ++h) {
                for (int i : list) {
                    Point point = new Point(i, h);
                    pointListV.add(point);
                }
            }


            for (int l = leftBorder + 1; l < rightBorder; ++l) {
                Point pointTop = new Point(l, top);
                pointListH.add(pointTop);
                Point pointBottom = new Point(l, bottom);
                pointListH.add(pointBottom);
            }

            System.out.println(ret);

            BufferedImage dst = bi.getSubimage(leftBorder + 1, top + 1,
                    rightBorder - leftBorder - 1, bottom - top - 1);
            File outputfile = new File("out", ++index + ".jpg");

            for (int j = 0; j < dst.getHeight(); ++j) {
                for (int i = 0; i < dst.getWidth(); ++i) {
                    int v = dst.getRGB(i, j);
                    if (v == -1) {
                        System.out.print("0");
                    } else if (v == -16777216) {
                        System.out.print("*");
                    } else {
                        System.out.print("+");
                    }
                }
                System.out.println("");
            }

            ImageIO.write(dst, "jpg", outputfile);
        }


        System.out.println("");

        for (int j = 0; j < bi.getHeight(); ++j) {
            for (int i = 0; i < bi.getWidth(); ++i) {

                Point point = new Point(i, j);

                if (pointListV.contains(point)) {
                    System.out.print("|");
                    continue;
                }

                if (pointListH.contains(point)) {
                    System.out.print("-");
                    continue;
                }

                int v = bi.getRGB(i, j);
                if (v == -1) {
                    System.out.print("0");
                } else if (v == -16777216) {
                    System.out.print("*");
                } else {
                    System.out.print("+");
                }
            }
            System.out.println("");
        }
    }

    public static BufferedImage removeNoise(BufferedImage bi) throws IOException {

        for (int y = 0; y < bi.getHeight(); ++y) {
            for (int x = 0; x < bi.getWidth(); ++x) {
                int v = bi.getRGB(x, y);
                if (v == -1) {
                    // 白点
//                    System.out.print("0");
                } else if (v == -16777216) {
                    // 黑点
                    // 检测上下左右斜角八个点，如果有黑点，则不是噪点，否则是噪点
                    List<Point> pointList = new ArrayList<>();
                    //0--
                    //-*-
                    //---
                    pointList.add(new Point(x - 1, y - 1));
                    //-0-
                    //-*-
                    //---
                    pointList.add(new Point(x, y - 1));
                    //--0
                    //-*-
                    //---
                    pointList.add(new Point(x + 1, y - 1));
                    //---
                    //0*-
                    //---
                    pointList.add(new Point(x - 1, y));
                    //---
                    //-*0
                    //---
                    pointList.add(new Point(x + 1, y));
                    //---
                    //-*-
                    //0--
                    pointList.add(new Point(x - 1, y + 1));
                    //---
                    //-*-
                    //-0-
                    pointList.add(new Point(x, y + 1));
                    //---
                    //-*-
                    //--0
                    pointList.add(new Point(x + 1, y + 1));

                    boolean isNoisePoint = true;
                    for (Point point : pointList) {
                        if (checkPoint(point, bi.getWidth(), bi.getHeight())) {
                            if (bi.getRGB(point.x, point.y) == -16777216) {
                                // 找到一个邻近黑点，则判断为非噪点
//                                System.out.print("*");
                                isNoisePoint = false;
                                break;
                            }
                        }
                    }

                    if (isNoisePoint) {
                        // 如果是噪点，则漂白它
                        bi.setRGB(x, y, -1);
//                        System.out.print("0");
                    }
                } else {
                    System.err.println("非二值化图片，程序异常。");
                    System.exit(-1);
//                    System.out.print("+");
                }

            }
//            System.out.println("");
        }
        return bi;
    }

    private static BufferedImage removeNoise(String imagePath) throws IOException {
        System.out.println(imagePath);
        BufferedImage bi = ImageIO.read(new File(imagePath));
        return removeNoise(bi);
    }

    /**
     * 检测点是否有效
     *
     * @param leftTopPoint
     * @param width
     * @param height
     * @return
     */
    private static boolean checkPoint(Point point, int width, int height) {
        return point.x >= 0 && point.y >= 0 && point.x < width && point.y < height;
    }


    /**
     * 按字母图片分割
     *
     * @param imagePath 图片路径
     * @throws Exception
     */
    public static void splitByLetter(String imagePath, File dstDir) throws Exception {

        // 1. 去噪点
        BufferedImage bi = removeNoise(imagePath);

        // 2. 找出所有的黑点
        LinkedList<Point> points = getPoints(bi);

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

                List<Point> linkedPoints = getLinkedPoints(points, p);
//                System.out.println("关联点数量:" + linkedPoints.size());

                points.removeAll(linkedPoints);
//                System.out.println("剩余点数:" + points.size());

                queue.addAll(linkedPoints);
//                System.out.println("待检索点数:" + points.size());
            }
            pointList.add(innerList);
        }

//        System.out.println("分割后图片数量为:" + pointList.size());
        int sum = 0;
        for (List<Point> list : pointList) {
            sum = sum + list.size();

            // 如果黑点数小于 6 ，则可能是 i，j 字母中的那个点，删掉不影响识别
            if (list.size() <= 6) continue;

//            System.out.println("图片黑点数量为:" + list.size());
            saveToJpg(list, bi, dstDir);
        }
//        System.out.println("所有图片黑点数量为:" + sum);
    }

    static long index = 1000000;

    private static void saveToJpg(List<Point> list, BufferedImage src, File dstDir) throws IOException {

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

        BufferedImage bi = src.getSubimage(startX, startY, width, height);
        String key = String.format("%d-%d-%d",
                list.size(), width, height);
//        System.out.println(key);
//        String value = KV.getLetter(key);
        String name = String.format("%s-%d.png", key, ++index);
        System.out.println(name);
        ImageIO.write(bi, "png", new File(dstDir, name));
    }

    /**
     * 获取相邻的黑点
     *
     * @param points
     * @param p
     * @return
     */
    public static List<Point> getLinkedPoints(LinkedList<Point> points, Point p) {
        List<Point> pointList = new ArrayList<>();
        //0--
        //-*-
        //---
        pointList.add(new Point(p.x - 1, p.y - 1));
        //-0-
        //-*-
        //---
        pointList.add(new Point(p.x, p.y - 1));
        //--0
        //-*-
        //---
        pointList.add(new Point(p.x + 1, p.y - 1));
        //---
        //0*-
        //---
        pointList.add(new Point(p.x - 1, p.y));
        //---
        //-*0
        //---
        pointList.add(new Point(p.x + 1, p.y));
        //---
        //-*-
        //0--
        pointList.add(new Point(p.x - 1, p.y + 1));
        //---
        //-*-
        //-0-
        pointList.add(new Point(p.x, p.y + 1));
        //---
        //-*-
        //--0
        pointList.add(new Point(p.x + 1, p.y + 1));

        List<Point> retList = new ArrayList<>();
        for (Point point : pointList) {
            if (points.contains(point)) retList.add(point);
        }

        return retList;
    }

    /**
     * 获取所有的黑点
     *
     * @param bi
     * @return
     */
    public static LinkedList<Point> getPoints(BufferedImage bi) {
        LinkedList<Point> points = new LinkedList<>();
        for (int y = 0; y < bi.getHeight(); ++y) {
            for (int x = 0; x < bi.getWidth(); ++x) {
                int v = bi.getRGB(x, y);
                if (v == -16777216) {
                    points.add(new Point(x, y));
                }
            }
        }
        return points;
    }

    private static void forEachDir(String dirPath) {
        for (File file : new File(dirPath).listFiles()) {
            if (file.isDirectory()) continue;
            System.out.println(file.getName()
                    .replace(".png", "")
                    .replace(".jpg", ""));
        }
    }
}