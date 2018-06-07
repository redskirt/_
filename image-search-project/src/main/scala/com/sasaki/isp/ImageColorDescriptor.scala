package com.sasaki.isp

import org.opencv.imgproc.Imgproc
import org.opencv.core.Mat
import org.opencv.core.CvType
import org.opencv.core.Range
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.core.Rect
import org.opencv.core.Scalar
import com.sasaki.isp.kit.ImageGui
import org.opencv.core.Core
import org.opencv.core.MatOfInt

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Jun 6, 2018 8:20:48 PM
 * @Description 3D HSV直方图
 */
class ImageColorDescriptor {
  
  val self = this
  type Self = self.type
  
  def this($bins: Int) {
    this
    this.bins = $bins
  }
  
  // 3D直方图的“直方”数
  var bins: Int = _
  
  def describe($self: Self, $mat: Mat) = {
    val mat = new Mat()
    var features = Array[Int]()
    Imgproc.cvtColor($mat, mat, Imgproc.COLOR_BGR2HSV)

    // 获取图像大小和中心点
    val size = new Size(mat.width(), mat.height())
    val centre = new Point(size.width * 0.5, size.height * 0.5)
    
    implicit def doubou2int(double: Double) = double toInt
    
    // 将图像划分为 上左、上右、下左、下右 四区域
    val segments = (
      (new Point(0, 0), new Point(centre.x, centre.y)),                   // 上左
      (new Point(centre.x, 0), new Point(size.width, centre.y)),          // 上右
      (new Point(0, centre.y), new Point(centre.x, size.height)),         // 下左
      (new Point(centre.x, centre.y), new Point(size.width, size.height)) // 下右
    )
    
    val ellipseMask = new Mat()
    // 椭圆遮罩大小
    val axes = new Size(mat.width * 0.75 / 2, mat.height * 0.75 / 2)
    
    features
  }
  
  /**
   * 从3D色阶直方图中提取图像遮罩区域
   */
  def histogram($self: Self, mat: Mat, mask: Mat) = {
    import scala.collection.JavaConverters._

//    Imgproc.calcHist(
//        List(mat).asJava, //
//        new MatOfInt(0, 1, 2),//  
//        mask, // 
//        $self.bins, //
//        histSize, // 
//        ranges // 
//        )
  }
}

object ImageColorDescriptor {
  
  val native_library = "/opt/local/share/OpenCV/java/libopencv_java341.dylib"
  System.load(native_library)
  
  val path = "/Users/sasaki/Desktop/refer.jpg"
  
  val WHITE = new Scalar(255)
  val BLACK = new Scalar(0)
  
  implicit def doubou2int(double: Double) = double toInt
  
  def main(args: Array[String]): Unit = {
    
    // 原图
    val mat: Mat = Imgcodecs.imread(path)
    // 中心点
    val centre: Point = new Point(mat.width() / 2, mat.height() / 2)
    // 遮罩层，黑
    val mask: Mat = Mat.zeros(mat.size(), CvType.CV_8UC1)
    // 遮罩区域
    val rect: Rect = new Rect(centre, new Size(50, 50))
    // 用白色填充遮罩区域，白色为目标区域
//    mask.submat(rect).setTo(WHITE)
    // 抽出图像目标区域
    val mat2 = new Mat()
    mat.copyTo(mat2, mask)
    // 抽出图像非目标区域
    val mat3 = new Mat()
    mat.copyTo(mat3)
    mat3.setTo(BLACK, mask)
    
    val size = mat.size()
    
    // 遮罩层
    val mask_ = Mat.zeros(mat.size(), CvType.CV_8UC1)
    // 椭圆区域
    val ellipse = Imgproc.ellipse(
      mask_, //
      centre, // 中心点
      new Size(mat.width() * 0.75 / 2, mat.height() * 0.75 / 2), // 大小
      0, // 旋转角度
      0, // 起始角
      360, // 终止角
      WHITE, //
      -1 // 边框，负数表示填充
    )
    val matWithEllipseMask = new Mat
//    mat.copyTo(matWithEllipseMask, mask_)
    
    val matTopLeft =  Mat.zeros(mat.size(), CvType.CV_8UC1)
//    val pointRect = (new Point(0, 0), new Point(centre.x, centre.y))
    val pointRect = (new Point(0, centre.y), new Point(centre.x, size.height))
    Imgproc.rectangle(matTopLeft, pointRect._2, pointRect._1, WHITE, -1)
    
    val extractMask = new Mat
    Core.subtract(matTopLeft, mask_, extractMask)
    val extractResult = new Mat(mat.size(), CvType.CV_8UC1)
    mat.copyTo(extractResult, extractMask)
    
    
    val gui =  new ImageGui(extractResult, "show")
    gui.imshow()
    ImageGui.waitKey(0)
  }
}

