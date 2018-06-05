package com.sasaki.isp

import java.awt.image.BufferedImage
import java.io.File

import scala.util.control.Breaks

import org.opencv.core._
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

import javax.imageio.ImageIO
import org.opencv.core.Range
import net.sourceforge.tess4j.Tesseract
import net.sourceforge.lept4j.util.LoadLibs

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
  Imgproc.threshold(image3, image4, 20/*黑色阈值*/, 255/*白色*/, Imgproc.THRESH_BINARY)

  // 图像腐蚀，将图像的像素点做放大(3 * 3)处理，使得图像“颗粒化”，需要提取的数据会加粗，便于后续处理
  val factor: Mat = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3))
  val image5: Mat = new Mat(image.size(), CvType.CV_8UC1)
  Imgproc.erode(image4, image5, factor)
  
  // 过滤、切割图像
  // 每行中出现的黑色点阈值，超出（或低于）阈值判定为有效行
  val width = image5.width()
  val blackThreshold = 100 
  var flag = false
  var endFlag = false
  var y1, y2: Int = _
  val line: Mat = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(width, 1))

  Breaks.breakable {
    for (y <- 0 until image5.height()) { // 遍历行
      var count = 0
      for (x <- 0 until image5.width()) { // 遍历列
        val point = Array[Byte](1)
        image5.get(y, x, point)
        if (0 /*黑色点*/ == point(0))
          count += 1
      }

      if (!flag /*数字区域上方，表示未找到有效行*/ && count >= blackThreshold) {
        flag = true
        y1 = y
      }

      if (flag /*数字区域下方，表示已找到有效行*/ && count <= blackThreshold /*有效行结束位置*/ ) {
        y2 = y
        Breaks.break()
      }
    }
  }
  
  // 获取上下边界坐标，绘制两条分界线（非必要）
//  val border = (y1, y2)
//  val p1 : Point = new Point(0, y1)
//  val p2 : Point = new Point(width, y1)
//  val q1 : Point = new Point(0, y2)
//  val q2 : Point = new Point(width, y2)
//  Imgproc.line(image5, p1, p2, new Scalar(0,255, 255))
//  Imgproc.line(image5, q1, q2, new Scalar(0,255, 255))
  Imgcodecs.imwrite("/Users/sasaki/Desktop/image5.jpg", image5)  

  // 图像裁剪
  val r1: Range = new Range(y1, y2) // 行范围
  val r2: Range = new Range(0, width) // 列范围
  val image6: Mat = image5.submat(r1, r2)
  val urlImage6 = "/Users/sasaki/Desktop/image6.jpg"
  Imgcodecs.imwrite(urlImage6, image6)  
  
  val tessDataFolder = LoadLibs.extractNativeResources("tessdata")
  val fileImage6 = new File(urlImage6)
  val tesseract = new Tesseract()
  tesseract.setDatapath(tessDataFolder.getAbsolutePath())
  val result = tesseract.doOCR(fileImage6)
  println(result)
}