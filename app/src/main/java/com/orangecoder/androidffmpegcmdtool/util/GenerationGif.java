package com.orangecoder.androidffmpegcmdtool.util;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import com.orangecoder.androidffmpegcmdtool.core.FfmpegController;
import com.orangecoder.androidffmpegcmdtool.core.ShellCallback;


public class GenerationGif {

	public static String excute(Context context, String inVideoPath,
			String startTime) throws IOException, InterruptedException
	{
		String outGifPath = new File(new File(inVideoPath).getParentFile(),
									"VideoToGif.gif").getCanonicalPath();

		FfmpegController.getInstance(context).generationGif(inVideoPath,
				outGifPath, startTime,
				new ShellCallback() {

				@Override
				public void shellOut(String shellLine) {
//					System.out.println("generationGif>" + shellLine);
				}

				@Override
				public void processComplete(int exitValue) {
//					System.out.println("generationGif>" + exitValue);
				}
			});
		return outGifPath;
	}
}
