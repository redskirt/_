package org.sasaki.isp.main;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class SampleCV {

	public static void main(String[] args) {

		// System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		String path = "/opt/local/share/OpenCV/java/libopencv_java341.dylib";
		System.load(path);

		// 读取图像，不改变图像的原始信息
		Mat m = Imgcodecs.imread("/Users/sasaki/Desktop/_/timthumb.jpg", Imgcodecs.CV_LOAD_IMAGE_COLOR);

		// 将图片转换成灰度图片
		Mat gray = new Mat(m.size(), CvType.CV_8UC1);
		Imgproc.cvtColor(m, gray, Imgproc.COLOR_RGB2GRAY);

		// 计算灰度直方图
		List<Mat> images = new ArrayList<Mat>();
		images.add(gray);

		MatOfInt channels = new MatOfInt(0);
		MatOfInt histSize = new MatOfInt(256);
		MatOfFloat ranges = new MatOfFloat(0, 256);
		Mat hist = new Mat();
		Imgproc.calcHist(images, channels, new Mat(), hist, histSize, ranges);

		// mat求和
		System.out.println(Core.sumElems(hist));

		// 保存转换的图片
		Imgcodecs.imwrite("/Users/sasaki/Desktop/_/timthumb_.jpg", gray);
	}
}