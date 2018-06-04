package com.sasaki.isp

import java.awt.image.BufferedImage
import java.io.File

import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

import javax.imageio.ImageIO

/**
 * @Author Sasaki
 * @Mail redskirt@outlook.com
 * @Timestamp May 28, 2018 10:32:40 PM
 * @Description
 */
object DigitalExtractSample extends App {

  // 本地opencv库
  val native_library = "/opt/local/share/OpenCV/java/libopencv_java341.dylib"
  System.load(native_library)
  
  val filePath = "/Users/sasaki/git/_/image-search-project/src/main/resources/bankcard/1.jpg"

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
  
  // 图像灰度化
  val image3: Mat = new Mat(image.size(), CvType.CV_8UC1)
  Imgproc.cvtColor(image, image3, Imgproc.COLOR_RGB2GRAY)
  
  // 图像二值化处理，即仅保留纯黑、纯白两种像素
  val image4: Mat = new Mat(image.size(), CvType.CV_8UC1)
  Imgproc.threshold(image3, image4, 0, 255, Imgproc.THRESH_BINARY)
  
  // 图像腐蚀，将图像的像素点做放大(3 * 3)处理，使得图像“颗粒化”，需要提取的数据会加粗，便于后续处理
  val factor: Mat = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3))
  val image5: Mat = new Mat(image.size(), CvType.CV_8UC1)
  Imgproc.erode(image4, image5, factor)
  
  // 过滤、切割图像
  for {
	  x <- 0 until image5.width()
    y <- 0 until image5.height()
  } {
    val elementPoint: Array[Int] = image5.get(x, y).map(o => 255)
    image5.put(x, y, elementPoint)
  }
  Imgcodecs.imwrite("/Users/sasaki/Desktop/a.jpg", image5)  
  
}