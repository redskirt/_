package com.sasaki.isp

import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.io.File
import org.opencv.core.CvType
import org.opencv.imgproc.Imgproc

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 28, 2018 10:32:40 PM
 * @Description
 */
object DigitalExtractSample {

  // 本地opencv库
  val native_library = "/opt/local/share/OpenCV/java/libopencv_java341.dylib"
  System.load(native_library)
  
  val filePath = "bankcard/1.jpg"

  // Mat类是OpenCV最基本的一个数据类型，它可以表示一个多维的多通道的数组。
  // Mat常用来存储图像，包括单通道二维数组——灰度图，多通道二维数组——彩色图。
  val image: Mat = Imgcodecs.imread(filePath)

  // Mat -> BufferedImage
  val bytes = new Array[Byte](image.rows() * image.cols() * image.elemSize().toInt)
  //		image.get(0, 0, bytes)
  val bufferedImage = new BufferedImage(image.cols(), image.rows(), BufferedImage.TYPE_BYTE_GRAY)
  //    bufferedImage.getRaster().setDataElements(0, 0, image.cols(), image.rows(), bytes)

  // BufferedImage -> Mat
  val bufferedImage2 = ImageIO.read(new File(filePath))
  val image2: Mat = new Mat(bufferedImage2.getHeight(), bufferedImage2.getWidth(), CvType.CV_8UC3)
  
  // 图片灰度化
  val image3: Mat = new Mat(image.size(), CvType.CV_8UC1)
  Imgproc.cvtColor(image, image3, Imgproc.COLOR_RGB2GRAY)
  
  
}