package com.sasaki.isp

import org.opencv.imgproc.Imgproc
import org.opencv.core.Mat
import org.opencv.core.CvType
import org.opencv.core.Range
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp Jun 6, 2018 8:20:48 PM
 * @Description 3D HSV直方图
 */
class ImageColorDescriptor {
  
  def this($bins: Int) {
    this
    this.bins = $bins
  }
  
  // 3D直方图的“直方”数
  var bins: Int = _
 
  def describe(descriptor: ImageColorDescriptor, $mat: Mat) = {
    val mat = new Mat()
    var features = Array[Int]()
    Imgproc.cvtColor($mat, mat, Imgproc.COLOR_BGR2HSV)

    // 获取图像大小和中心点
    val size = new Size(mat.width(), mat.height())
    val centre = new Point(size.width * 0.5, size.height * 0.5)
    
    implicit def doubou2int(double: Double) = double toInt
    
    // 将图像划分为 上左、上右、下左、下右 四区域
    val segments = (
      (new Range(0, centre.x), new Range(0, centre.y)),                   // 上左
      (new Range(centre.x, size.width), new Range(0, centre.y)),          // 上右
      (new Range(0, centre.x), new Range(centre.y, size.height)),         // 下左
      (new Range(centre.x, size.width), new Range(centre.y, size.height)) // 下右
    )
    

    val ellipseMask = new Mat()
    // 椭圆遮罩大小
    val axes = new Size(mat.width * 0.75 / 2, mat.height * 0.75 / 2)
    
    features
  }
  
  
}