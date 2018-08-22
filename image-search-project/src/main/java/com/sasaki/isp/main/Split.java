package com.sasaki.isp.main;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class Split {

	public static void main(String[] args) {
		try {
			String native_library = "/opt/local/share/OpenCV/java/libopencv_java341.dylib";
			System.load(native_library);

			Mat src = Imgcodecs.imread("/Users/sasaki/Desktop/target3.png");
			// 读取图像到矩阵中,取灰度图像
			if (src.empty()) {
				throw new Exception("no file");
			}

			List<Mat> dst = new java.util.ArrayList<Mat>(3);
			Core.split(src, dst);

			System.out.println(dst);
			// Imgcodecs.imwrite("./images/b.jpg", dst.get(0));
			// Imgcodecs.imwrite("./images/g.jpg", dst.get(1));
			// Imgcodecs.imwrite("./images/r.jpg", dst.get(2));
		} catch (Exception e) {
			System.out.println("例外：" + e);
		}

	}

}