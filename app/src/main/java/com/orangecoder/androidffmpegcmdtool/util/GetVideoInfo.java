package com.orangecoder.androidffmpegcmdtool.util;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.orangecoder.androidffmpegcmdtool.core.Clip;
import com.orangecoder.androidffmpegcmdtool.core.FfmpegController;


public class GetVideoInfo {
	
	public static Clip excute(Context context, String videoPath)
			throws FileNotFoundException, IOException, InterruptedException
	{
		Clip clip = new Clip();
		clip.path = new File(videoPath).getCanonicalPath();
		FfmpegController.getInstance(context).getInfo(clip);
		return clip;
	}
}
