package com.orangecoder.androidffmpegcmdtool.util;

import android.content.Context;

import com.orangecoder.androidffmpegcmdtool.core.FfmpegController;
import com.orangecoder.androidffmpegcmdtool.core.ShellCallback;

import java.io.File;
import java.io.IOException;


public class WaterMark {

	public static String excute(Context context, String inVideoPath)
			throws IOException, InterruptedException
	{
		String outVideoPath = new File(new File(inVideoPath).getParentFile(),
								"watermarkVideo.mp4").getCanonicalPath();

		FfmpegController.getInstance(context).watermark(inVideoPath, outVideoPath,
				new ShellCallback() {

					@Override
					public void shellOut(String shellLine) {
//						System.out.println("watermark>" + shellLine);
					}

					@Override
					public void processComplete(int exitValue) {
//						System.out.println("watermark>" + exitValue);
					}
				});

		return outVideoPath;
	}
}
