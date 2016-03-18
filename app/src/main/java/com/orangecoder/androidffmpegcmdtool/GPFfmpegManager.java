package com.orangecoder.androidffmpegcmdtool;

import android.content.Context;

import com.orangecoder.androidffmpegcmdtool.core.FfmpegController;
import com.orangecoder.androidffmpegcmdtool.util.GPFfmpegHandleListener;
import com.orangecoder.androidffmpegcmdtool.util.GenerationGif;
import com.orangecoder.androidffmpegcmdtool.util.GenerationPic;
import com.orangecoder.androidffmpegcmdtool.util.WaterMark;

import java.io.IOException;


public class GPFfmpegManager {

	public static String WaterMark(Context context, String inVideoPath)
									throws IOException, InterruptedException {
		return WaterMark.excute(context, inVideoPath);
	}
	
	public static String GenerationGif(Context context, String inVideoPath,
				String startTime) throws IOException, InterruptedException {
		return GenerationGif.excute(context, inVideoPath, startTime);
	}
	
	public static String generationPic(Context context, String inVideoPath, String startTime,
			 GPFfmpegHandleListener listener) throws IOException, InterruptedException {
		return GenerationPic.excute(context, inVideoPath, startTime, listener);
	}
	
	public static int KillProcess(Context context) throws IOException {
		return FfmpegController.getInstance(context).killVideoProcessor(false, false);
	}
}
