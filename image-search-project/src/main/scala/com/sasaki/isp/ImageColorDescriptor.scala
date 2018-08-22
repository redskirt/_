package com.sasaki.isp

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

import scala.collection.mutable.ArrayBuffer

import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import com.sasaki.isp.util.Util
import scala.io.Source
import org.opencv.utils.Converters 
import java.io.ByteArrayInputStream
import com.sasaki.isp.kit.ImageGui


/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Jun 6, 2018 8:20:48 PM
 * @Description 3D HSV直方图
 */
object ImageColorDescriptor {

  import Sample._

  // 3D直方图的“直方”数
  var bins: Int = _

  def calcHistogramFeatures($mat: Mat): Array[Mat] = {
    val features = ArrayBuffer[Mat]()

    Imgproc.cvtColor($mat, $mat, Imgproc.COLOR_BGR2HSV)
    // 获取图像大小和中心点
    val size = new Size($mat.width(), $mat.height())
    val centre = new Point(size.width * 0.5, size.height * 0.5)

    implicit def doubou2int(double: Double) = double toInt

    // 将图像划分为 上左、上右、下左、下右 四区域
    val segments = Array(
      (new Point(0, 0), new Point(centre.x, centre.y)), // 上左
      (new Point(centre.x, 0), new Point(size.width, centre.y)), // 上右
      (new Point(0, centre.y), new Point(centre.x, size.height)), // 下左
      (new Point(centre.x, centre.y), new Point(size.width, size.height)) // 下右
    )

    // 椭圆遮罩背景层
    val maskEllipseBack = Mat.zeros($mat.size(), CvType.CV_8UC1)
    // 在背景层上绘制椭圆
    Imgproc.ellipse(
      maskEllipseBack, //
      centre, // 中心点
      new Size($mat.width() * 0.75 / 2, $mat.height() * 0.75 / 2), // 大小
      0, // 旋转角度
      0, // 起始角
      360, // 终止角
      WHITE, //
      -1 // 边框，负数表示填充
    )

    // 椭圆遮罩图像
    val matEllipseMask = new Mat
    $mat.copyTo(matEllipseMask, maskEllipseBack)
//    Imgcodecs.imwrite("/Users/sasaki/Desktop/1.jpg", matEllipseMask)

    val histEllipseMask = histogram(matEllipseMask, maskEllipseBack)
    features += histEllipseMask

    var i = 2
    segments.foreach { o =>
      // 矩形遮罩背景层
      val maskRectBack = Mat.zeros($mat.size(), CvType.CV_8UC1)
      // 矩形区域
      val pointRect = (o._1, o._2)
      // 在背景层上绘制矩形区域
      Imgproc.rectangle(maskRectBack, pointRect._2, pointRect._1, WHITE, -1)
      // 矩形+椭圆遮罩层
      val maskRectWithEllipseBack = new Mat
      Core.subtract(maskRectBack, maskEllipseBack, maskRectWithEllipseBack)
      // 矩形+椭圆遮罩图像
      val matRectWithEllipseBack = new Mat($mat.size(), CvType.CV_8UC1)
      $mat.copyTo(matRectWithEllipseBack, maskRectWithEllipseBack)
//      Imgcodecs.imwrite(s"/Users/sasaki/Desktop/$i.jpg", matRectWithEllipseBack)
      i = i + 1
      
      val histRectWithEllipseBack = histogram(matRectWithEllipseBack, maskRectWithEllipseBack)
//      Imgcodecs.imwrite(s"/Users/sasaki/Desktop/${i * 2}.jpg", histRectWithEllipseBack)
      features += histRectWithEllipseBack
    }

    features.toArray
  }

  /**
   * 1. 绘制3D色阶直方图
   * 2. 从直方图中提取图像未遮罩区域
   */
  def histogram(mat: Mat, mask: Mat): Mat = {
    val histogram = new Mat(mat.size(), CvType.CV_8UC1)

    import scala.collection.JavaConverters._
 
    Imgproc.calcHist(
      List(mat).asJava, // 图像
      new MatOfInt(0, 1), // 图像通道，数组表示。灰度图：Array(0)，彩色图Array(0, 1)
      mask, // 遮罩，图像参与计算的部分，不用遮罩可传入Mat()空图像
      histogram, // 目标直方图
      new MatOfInt(16, 16), //  
      new MatOfFloat(0f, 32f, 0, 32f) // 二维数组，每个区间的范围
    )

    val normalHistogram = new Mat(mat.size(), CvType.CV_8UC1)
    Core.normalize(histogram, normalHistogram, 0, histogram.height() / 2, Core.NORM_MINMAX, -1, new Mat())

    normalHistogram
  }

  val FEATURES_FILE_PATH = "/Users/sasaki/Desktop/t.csv"
  def calcMultiSimilarity(destImage: Mat, topN: Int): Seq[Tuple2[String, Double]] = {
    val destRecorder = image2CsvRecorde(destImage)
    val destHistogram = csvRecorde2Vector(destRecorder.split(","))
    
    Util.readTextFile(FEATURES_FILE_PATH)
      .map { o =>
        val array = o.split(",")
        val name = array.head
        val indexHistogram = csvRecorde2Vector(array.tail)
        val similarity = calcSimilarity(indexHistogram, destHistogram)

        (name, similarity)
      }
      .toSeq
      .sortBy(_._2)
      .take(topN)
  }
      
  def image2CsvRecorde(mat: Mat): String = {
    val vectors = calcHistogramFeatures(mat)
    val formatContent = (s: String) => s.replace("[", "").replace("]", "").replace(";", "").replace("\n", ", ")
    vectors.map(t => formatContent(t.dump())).reduce(_ + ", " + _)
  }
    
  def csvRecorde2Vector(recorder: Seq[String]): Mat = {
    import scala.collection.JavaConverters._
    import java.lang.{ Double => JDouble }

    val vector = recorder.map(_.toDouble.asInstanceOf[JDouble]).toList.asJava
    Converters.vector_double_to_Mat(vector)
  }
   
  def calcSimilarity(histogram_1: Mat, histogram_2: Mat): Double = {
    val histogram_1_Depth5 = new Mat(histogram_1.size(), CvType.CV_32F)
    val histogram_2_Depth5 = new Mat(histogram_2.size(), CvType.CV_32F)
    histogram_1.convertTo(histogram_2_Depth5, CvType.CV_32F)
    histogram_2.convertTo(histogram_1_Depth5, CvType.CV_32F)
    
    // 卡方相似度为零的图片表示完全相同。相似度数值越高，表示两幅图像差别越大。
    Imgproc.compareHist(histogram_1_Depth5, histogram_2_Depth5, Imgproc.CV_COMP_CHISQR)
  }
}

object Sample {
  val native_library = "/opt/local/share/OpenCV/java/libopencv_java341.dylib"
  System.load(native_library)
  
  val path = "/Users/sasaki/Desktop/target3.png" // target1.png
  
  val WHITE = new Scalar(255)
  val BLACK = new Scalar(0)
  
  implicit def doubou2int(double: Double) = double toInt
  
  /**
   * 绘制直方图
   */
  def drawHistogram(mat: Mat) = {

    import scala.collection.JavaConverters._

    // 分割成3个单通道图像 ( R, G, B )
    val images = new java.util.ArrayList[Mat]()
    Core.split(mat, images)

    // 设定bin数目
    val histSize = new MatOfInt(10)

    // 设定取值范围(R,G,B)
    val channels = new MatOfInt(0)
    val histRange = new MatOfFloat(0f, 10f)

    // 分别计算直方图
    val hist_b = new Mat()
    val hist_g = new Mat()
    val hist_r = new Mat()

    Imgproc.calcHist(images.subList(0, 1), channels, new Mat(), hist_b, histSize, histRange, false)
    Imgproc.calcHist(images.subList(1, 2), channels, new Mat(), hist_g, histSize, histRange, false)
    Imgproc.calcHist(images.subList(2, 3), channels, new Mat(), hist_r, histSize, histRange, false)

    // 分别创建直方图画布
    val hist_w = 400 // width of the histogram image
    val hist_h = 400 // height of the histogram image
    val bin_w = Math.round(hist_w / histSize.get(0, 0)(0))

    val histImage = new Mat(hist_h, hist_w, CvType.CV_8UC3, new Scalar(0, 0, 0));

    // 将直方图归一化到范围 [ 0, histImage.rows ]
    Core.normalize(hist_b, hist_b, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat())
    Core.normalize(hist_g, hist_g, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat())
    Core.normalize(hist_r, hist_r, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat())

    // 在直方图画布上画出直方图
    for (i <- 1 until histSize.get(0, 0)(0)) {
      // B component or gray image
      Imgproc.line(
          histImage, 
          new Point(bin_w * (i - 1), 
          hist_h - Math.round(hist_b.get(i - 1, 0)(0))),
          new Point(bin_w * (i), 
          hist_h - Math.round(hist_b.get(i, 0)(0))), 
          new Scalar(255, 0, 0), 2, 8, 0)
      // G and R components (if the image is not in gray scale)
      Imgproc.line(
          histImage, 
          new Point(bin_w * (i - 1), 
          hist_h - Math.round(hist_g.get(i - 1, 0)(0))),
          new Point(bin_w * (i), 
          hist_h - Math.round(hist_g.get(i, 0)(0))), 
          new Scalar(0, 255, 0), 2, 8, 0)
      Imgproc.line(
          histImage, 
          new Point(bin_w * (i - 1), 
          hist_h - Math.round(hist_r.get(i - 1, 0)(0))),
          new Point(bin_w * (i), 
          hist_h - Math.round(hist_r.get(i, 0)(0))), 
          new Scalar(0, 0, 255), 2, 8, 0)
    }

    histImage
//    mat2Image(histImage)
  }
  
  def mat2Image(mat: Mat) = {
    // create a temporary buffer
    val buffer = new MatOfByte()
    // encode the frame in the buffer, according to the PNG format
    Imgcodecs.imencode(".png", mat, buffer)
    // build and return an Image created from the image encoded in the  buffer
    import javafx.scene.image.Image
    new Image(new ByteArrayInputStream(buffer.toArray()))
  }

  
  def main(args: Array[String]): Unit = {
    
//    // 原图
    val mat: Mat = Imgcodecs.imread(path)
//    // 中心点
//    val centre: Point = new Point(mat.width() / 2, mat.height() / 2)
//    // 遮罩层，黑
//    val mask: Mat = Mat.zeros(mat.size(), CvType.CV_8UC1)
//    // 遮罩区域
//    val rect: Rect = new Rect(centre, new Size(50, 50))
//    // 用白色填充遮罩区域，白色为目标区域
////    mask.submat(rect).setTo(WHITE)
//    // 抽出图像目标区域
//    val mat2 = new Mat()
//    mat.copyTo(mat2, mask)
//    // 抽出图像非目标区域
//    val mat3 = new Mat()
//    mat.copyTo(mat3)
//    mat3.setTo(BLACK, mask)
//    
//    val size = mat.size()
//    
//    // 遮罩层
//    val mask_ = Mat.zeros(size, CvType.CV_8UC1)
//    // 椭圆区域
//    Imgproc.ellipse(
//      mask_, //
//      centre, // 中心点
//      new Size(mat.width() * 0.75 / 2, mat.height() * 0.75 / 2), // 大小
//      0, // 旋转角度
//      0, // 起始角
//      360, // 终止角
//      WHITE, //
//      -1 // 边框，负数表示填充
//    )
//    val matWithEllipseMask = new Mat
//    mat.copyTo(matWithEllipseMask, mask_)
//    
//    val matTopLeft =  Mat.zeros(mat.size(), CvType.CV_8UC1)
//    val pointRect = (new Point(0, centre.y), new Point(centre.x, size.height))
//    Imgproc.rectangle(matTopLeft, pointRect._2, pointRect._1, WHITE, -1)
//
//    val extractMask = new Mat
//    Core.subtract(matTopLeft, mask_, extractMask)
//    val extractResult = new Mat(mat.size(), CvType.CV_8UC1)
//    mat.copyTo(extractResult, extractMask)
    
       /**
     * 图像直方图，反应图像像素分布
     * 横轴：图像像素种类，可以是：灰度 / 彩色
     * 纵轴：每种颜色值在图像中的像素总数或者占比
     * Imgproc.calcHist(images, channels, mask, hist, histSize, ranges)
     */
//    val histogram = new Mat(mat.size(), CvType.CV_8UC1)
//    Imgproc.calcHist( 
//      List(mat).asJava, // 图像 
//      new MatOfInt(0, 1),// 图像通道，数组表示。灰度图：Array(1)，彩色图Array(0, 1, 2)
//      mask, // 遮罩，图像参与计算的部分，不用遮罩可传入Mat()空图像 
//      histogram, // 目标直方图
//      new MatOfInt(50, 60), // 直方图坐标区间数，会统计落在每个区间的像素点总和
//      new MatOfFloat(0f, 180f, 0f, 256f) // 二维数组，每个区间的范围
//    )
    
    /**
     * 归一化图像直方图，归一化可以使不同大小的图像展现相同的直方图，
     * 以及保证不同尺寸但内容近似的图像也会在比较函数中认为是相似的
     */
//    Core.normalize(src, dst, alpha, beta, norm_type, dtype, mask)
//     Core.normalize(histogram, histogram, 0, 1, Core.NORM_MINMAX, -1, mask)
     
    val histogram = drawHistogram(mat)
    val gui =  new ImageGui(histogram, "show")
    gui.imshow()
    ImageGui.waitKey(0)
    
    /**
     * 源目标的所有图的特征向量，得源特征向量数据集，输出csv
     */
   import ImageColorDescriptor._
   
//   val srcPath = "/Users/sasaki/Desktop/dataset"
//   val destContent = Util.listFiles(srcPath)
////    .take(5)
//    .filter { o => 
//      val name = o.getName
//      name.contains(".jpeg") || name.contains(".jpg") || name.contains(".png")
//    }
//    .map { o =>
//      val name = o.getName
//      val srcMat = Imgcodecs.imread(s"$srcPath/$name")
//      val recorder = image2CsvRecorde(srcMat)
//
//      s"$name, $recorder"
//    } 
////    .foreach(println)
//    .map(_ + "\n")
//    .reduce(_ + _)
//    
//    Util.writeFile(FEATURES_FILE_PATH, destContent)
    
//    val recorde = image2CsvRecorde(mat)
//    val recorde2 = Util.readTextFile(FEATURES_FILE_PATH)
//      .filter(_.contains("123204.png"))
//      .head
//    val vector_1 = csvRecorde2Vector(recorde.split(","))
//    val vector_2 = csvRecorde2Vector(recorde2.split(",").tail)
////    val vector_2 = Imgcodecs.imread("/Users/sasaki/Desktop/target3.png")
//    println(recorde)
//    val recorde2_ = recorde2.substring(14, recorde2.length)
//    println(recorde2_)
//    println(recorde == recorde2_)
//    println(calcSimilarity(vector_1, vector_2))
   
//    calcMultiSimilarity(mat, 200) foreach println
    
//   calcHistogramFeatures(mat)
   
   
  }
}

