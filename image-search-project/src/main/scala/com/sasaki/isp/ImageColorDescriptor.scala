package com.sasaki.isp

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

import scala.collection.mutable.ArrayBuffer

import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfFloat
import org.opencv.core.MatOfInt
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import com.sasaki.isp.util.Util
import scala.io.Source

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Jun 6, 2018 8:20:48 PM
 * @Description 3D HSV直方图
 */
class ImageColorDescriptor {
  
  import ImageColorDescriptor._
  
  val self = this
  
  def this($bins: Int) {
    this
    this.bins = $bins
  }
  
  // 3D直方图的“直方”数
  var bins: Int = _
  
  def calcFeatures(mat: Mat): Array[Mat] = {
    val features = ArrayBuffer[Mat]()

    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2HSV)
    // 获取图像大小和中心点
    val size = new Size(mat.width(), mat.height())
    val centre = new Point(size.width * 0.5, size.height * 0.5)
    
    implicit def doubou2int(double: Double) = double toInt
    
    // 将图像划分为 上左、上右、下左、下右 四区域
    val segments = Array(
      (new Point(0, 0), new Point(centre.x, centre.y)),                   // 上左
      (new Point(centre.x, 0), new Point(size.width, centre.y)),          // 上右
      (new Point(0, centre.y), new Point(centre.x, size.height)),         // 下左
      (new Point(centre.x, centre.y), new Point(size.width, size.height)) // 下右
    )

    // 椭圆遮罩背景层
    val maskEllipseBack = Mat.zeros(mat.size(), CvType.CV_8UC1)
    // 在背景层上绘制椭圆
    Imgproc.ellipse(
      maskEllipseBack, //
      centre, // 中心点
      new Size(mat.width() * 0.75 / 2, mat.height() * 0.75 / 2), // 大小
      0, // 旋转角度
      0, // 起始角
      360, // 终止角
      WHITE, //
      -1 // 边框，负数表示填充
    )

    // 椭圆遮罩图像 
    val matEllipseMask = new Mat
    mat.copyTo(matEllipseMask, maskEllipseBack)

    val histEllipseMask = self.histogram(matEllipseMask, maskEllipseBack)
    features += histEllipseMask

    segments.foreach { o =>
      // 矩形遮罩背景层
      val maskRectBack = Mat.zeros(mat.size(), CvType.CV_8UC1)
      // 矩形区域
      val pointRect = (o._1, o._2)
      // 在背景层上绘制矩形区域
      Imgproc.rectangle(maskRectBack, pointRect._2, pointRect._1, WHITE, -1)
      // 矩形+椭圆遮罩层
      val maskRectWithEllipseBack = new Mat
      Core.subtract(maskRectBack, maskEllipseBack, maskRectWithEllipseBack)
      // 矩形+椭圆遮罩图像
      val matRectWithEllipseBack = new Mat(mat.size(), CvType.CV_8UC1)
      mat.copyTo(matRectWithEllipseBack, maskRectWithEllipseBack)
      
      val histRectWithEllipseBack = histogram(matRectWithEllipseBack, maskRectWithEllipseBack)
      features += histRectWithEllipseBack
    }
    
    features.toArray
  }
    

  /**
   * 1. 绘制3D色阶直方图
   * 2. 从直方图中提取图像未遮罩区域
   */
  def histogram(mat: Mat, mask: Mat) = {
    val histogram = new Mat(mat.size(), CvType.CV_8UC1)
    
    import scala.collection.JavaConverters._

    Imgproc.calcHist(
      List(mat).asJava, // 图像
      new MatOfInt(0, 1), // 图像通道，数组表示。灰度图：Array(1)，彩色图Array(0, 1, 2)
      mask, // 遮罩，图像参与计算的部分，不用遮罩可传入Mat()空图像
      histogram, // 目标直方图
      new MatOfInt(50, 60), // 直方图坐标区间数，会统计落在每个区间的像素点总和
      new MatOfFloat(0f, 180f, 0f, 256f) // 二维数组，每个区间的范围
    )
    
    val normalHistogram = new Mat(mat.size(), CvType.CV_8UC1)
    Core.normalize(histogram, normalHistogram, 0, histogram.rows(), Core.NORM_MINMAX, -1, new Mat())
    
    normalHistogram
  }
}

object ImageColorDescriptor {
  
  val native_library = "/opt/local/share/OpenCV/java/libopencv_java341.dylib"
  System.load(native_library)
  
  val path = "/Users/sasaki/Desktop/refer2.jpg"
  
  val WHITE = new Scalar(255)
  val BLACK = new Scalar(0)
  
  implicit def doubou2int(double: Double) = double toInt
  
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
     
//    val gui =  new ImageGui(histogram, "show")
//    gui.imshow()
//    ImageGui.waitKey(0)
    
//     val descriptor = new ImageColorDescriptor()
//     val srcPath = "/Users/sasaki/vsh/SZU"
     val destination = "/Users/sasaki/Desktop/t.csv"
//
//   val destContent = Util.listFiles(srcPath)
//    .take(10)
//    .map { o =>
//      val name = o.getName
//      val srcMat = Imgcodecs.imread(s"$srcPath/$name")
//      val vectors = descriptor.describe(srcMat)
//      val formatContent = (s: String) => s.replace("[", "").replace("]", "").replace(";", "").replace("\n", ", ")
//      val recorder = vectors.map(t => formatContent(t.dump())).reduce(_ + ", " + _)
//
//      s"$name, $recorder"
//    }
//    .map(_ + "\n")
//    .reduce(_ + _)
//    
//    Util.writeFile(destination, destContent)
    
    /**
     * 
     */
//    Imgproc.compareHist(H1, H2, method)
  import org.opencv.utils.Converters 
  
  val result = Source.fromFile(destination)
  .getLines()
  .take(5)
  .map { o =>
    import scala.collection.JavaConverters._

    val array = o.split(",")
    val name = array.head
    val vector = array.tail.map(_.toDouble.asInstanceOf[java.lang.Double]).toList.asJava
    val mat = Converters.vector_double_to_Mat(vector)
    // 原图转换深度为5，否则计算相似度时异常
    val matDepth5 = new Mat(mat.size(), CvType.CV_32F)
    mat.convertTo(matDepth5, CvType.CV_32F)
    (name, matDepth5)
  }
//  .foreach(println)
//  println(c)
  val result2 = result.take(2).toArray
  val h1 = result2(0)._2
  val h2 = result2(1)._2

  // 卡方相似度为零的图片表示完全相同。相似度数值越高，表示两幅图像差别越大。
  val value = Imgproc.compareHist(h1, h2, Imgproc.CV_COMP_CHISQR/*卡方相似度*/)

  println(value)
  
  }
}

