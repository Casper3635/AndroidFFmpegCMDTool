package com.orangecoder.androidffmpegcmdtool.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.orangecoder.androidffmpegcmdtool.GPFfmpegManager;
import com.orangecoder.androidffmpegcmdtool.R;
import com.orangecoder.androidffmpegcmdtool.util.Basic_StringUtil;
import com.orangecoder.androidffmpegcmdtool.util.DensityUtil;
import com.orangecoder.androidffmpegcmdtool.util.FileUtil;
import com.orangecoder.androidffmpegcmdtool.util.GPFfmpegHandleListener;
import com.orangecoder.androidffmpegcmdtool.view.CustomProgressDialog;
import com.orangecoder.androidffmpegcmdtool.view.GPHorizontalScrollView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;


public class VideoProcessActivity extends Activity {

	private static final String TAG = "GenerationGifActivity";
	public static final String KEY_PROCESS_TYPE = "process_type";
	public static final String KEY_SHARE_URL = "share_url";
	public static final String KEY_VIDEO＿URL = "video_url";
	public static final String KEY_VIDEO_AUTHOR = "video_author";
	public static final int VALUE_GENERATION_GIF = 1;  //生成gif
	public static final int VALUE_GENERATION_PIC = 2;  //生成9连拍图
	
	private int processType = 0;
	private Handler mHandler = new Handler();
	private MyOnClickListener mOnClickListener;
	
	private String shareUrl;  //post url 用来分享
	private String videoUrl;  //视频url
	private String videoAuthor;  //视频作者
	private String videoLocalPath;  //视频本地地址
	
	private TextView tv_title;
	private VideoView videoView;
	private ProgressBar pb_video;
	
	private GPHorizontalScrollView horizontalScrollView;
	private float horizontalScrollViewShowWidth;  //滑动条最大显示长度
	private float horizontalScrollViewTotalWidth;  //滑动条总共长度;
	private LinearLayout layout_thumbnail;  //缩略图容器控件
	private int videoTimes;  //视频长度（ms）
	private int curTime = 0;  //当前起始时间（ms）
	
	private CustomProgressDialog progressDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_videoprocess);
		
		initData();
		initView();
		initEvent();
		getVideo();
	}
	
	private void initData() {
		processType = getIntent().getIntExtra(KEY_PROCESS_TYPE, 0);
		shareUrl = getIntent().getExtras().getString(
				KEY_SHARE_URL, "http://orangecoder.com");
		videoUrl = getIntent().getExtras().getString(
				KEY_VIDEO＿URL, "");
		videoAuthor = getIntent().getExtras().getString(
				KEY_VIDEO_AUTHOR, "orangecoder");
		
		if(processType==0 || Basic_StringUtil.isNullOrEmpty(videoUrl))
		{
			return;
		}
		
		mOnClickListener = new MyOnClickListener();
		horizontalScrollViewShowWidth = DensityUtil.getWidthInPx(VideoProcessActivity.this)
				- 2*DensityUtil.dip2px(VideoProcessActivity.this, 25);
	}
	
	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_videoProcessActivity_title);
		switch (processType) {
		case VALUE_GENERATION_GIF:
			tv_title.setText("生成GIF");
			break;
		case VALUE_GENERATION_PIC:
			tv_title.setText("生成连拍图");
			break;
		}
		
		videoView = (VideoView) findViewById(R.id.vv_videoProcessActivity);
		pb_video = (ProgressBar) findViewById(R.id.pb_videoProcessActivity_video);
		
		horizontalScrollView = (GPHorizontalScrollView) findViewById(R.id.hsv_videoProcessActivity);
		horizontalScrollView.setListener(new MyGPHorizontalScrollViewListener());
		layout_thumbnail = (LinearLayout) findViewById(R.id.layout_videoProcessActivity_thumbnail);
		layout_thumbnail.removeAllViews();
	}

	private void initEvent() {
		findViewById(R.id.layout_videoProcessActivity_cancle)
			.setOnClickListener(mOnClickListener);
		findViewById(R.id.layout_videoProcessActivity_ok)
			.setOnClickListener(mOnClickListener);
	}
	
	private void getVideo() {

		FileDownloader.getImpl().create(videoUrl)
				.setPath(FileUtil.getDownloadVideoPath(VideoProcessActivity.this, videoUrl))
				.setCallbackProgressTimes(0) //不显示进度
				.setListener(new FileDownloadSampleListener() {
					@Override
					protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
						super.progress(task, soFarBytes, totalBytes);
					}

					@Override
					protected void error(BaseDownloadTask task, Throwable e) {
						super.error(task, e);
					}

					@Override
					protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
						super.paused(task, soFarBytes, totalBytes);
					}

					@Override
					protected void completed(BaseDownloadTask task) {
						super.completed(task);
						videoLocalPath = task.getPath();
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								initVideoView();
							}
						});
					}

					@Override
					protected void warn(BaseDownloadTask task) {
						super.warn(task);
					}
				}).start();
	}
	
	private void initVideoView() {
		if(Basic_StringUtil.isNullOrEmpty(videoLocalPath))
		{
			return;
		}
		videoView.setVideoPath(videoLocalPath);
		videoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				mp.setLooping(true);
				
				pb_video.setVisibility(View.GONE);
				
				videoView.seekTo(0); // 按照初始位置播放
				videoView.start();
				showThumbnail();
			}
		});
		videoView.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				return true;
			}
		});
	}
	
	private void showThumbnail() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if(Basic_StringUtil.isNullOrEmpty(videoLocalPath))
				{
					return;
				}
				MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		        retriever.setDataSource(videoLocalPath);
		        String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
		        if(hasVideo != null)
		        {
		        	String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		        	videoTimes = Integer.parseInt(duration);
		        	final int bitmapWidth = DensityUtil.dip2px(VideoProcessActivity.this, 33);
		        	final int bitmapHeight = DensityUtil.dip2px(VideoProcessActivity.this, 44);
		        	Bitmap bitmap = retriever.getFrameAtTime(videoTimes/2*1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
		        	if(bitmap != null)
		        	{
		        		bitmap = resizeImage(bitmap, bitmapWidth, bitmapHeight*bitmap.getHeight()/bitmap.getWidth());
	            		final Bitmap thumbnailBitmap = bitmap;
	            		
	            		float oneSencondPicNum = horizontalScrollViewShowWidth/bitmapWidth/3;
		        		int thumbnailNum = (int) (oneSencondPicNum * videoTimes / 1000);
		        		horizontalScrollViewTotalWidth = bitmapWidth * thumbnailNum;
		        		runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								layout_thumbnail.removeAllViews();
							}
						});
		        		
		        		for(int i=0; i<thumbnailNum; i++)
						{
		        			mHandler.post(new Runnable() {
								
								@Override
								public void run() {
									ImageView imageView = new ImageView(VideoProcessActivity.this);
				        			LinearLayout.LayoutParams lp_thumbnail = new LinearLayout.LayoutParams(
				        					bitmapWidth, LinearLayout.LayoutParams.MATCH_PARENT);
				        			imageView.setBackgroundColor(Color.parseColor("#ffffff"));
				        			imageView.setLayoutParams(lp_thumbnail);
				        			imageView.setImageBitmap(thumbnailBitmap);
				        			imageView.setScaleType(ScaleType.CENTER_CROP);
									layout_thumbnail.addView(imageView);
								}
							});
						}
		        	}
		        }
		        retriever.release();
		        retriever = null;
			}
		}).start();
	}
	
	public Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;  
        int width = BitmapOrg.getWidth();  
        int height = BitmapOrg.getHeight();  
        int newWidth = w;  
        int newHeight = h;  
 
        float scaleWidth = ((float) newWidth) / width;  
        float scaleHeight = ((float) newHeight) / height;  
 
        Matrix matrix = new Matrix();  
        matrix.postScale(scaleWidth, scaleHeight);  
        // if you want to rotate the Bitmap   
        // matrix.postRotate(45);   
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,  
                        height, matrix, true);  
        return resizedBitmap;  
    }
	
	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layout_videoProcessActivity_cancle:
				finish();
				break;
			case R.id.layout_videoProcessActivity_ok:
				handleOKBtn();
				break;
			}
		}
	}
	
	private void handleOKBtn() {
		if(Basic_StringUtil.isNullOrEmpty(videoLocalPath))
		{
			return;
		}
		switch (processType) {
		case VALUE_GENERATION_GIF:
			HandleGenerationGifTask handleGenerationGifTask = new HandleGenerationGifTask();
			handleGenerationGifTask.execute();
			break;
		case VALUE_GENERATION_PIC:
			HandleGenerationPicTask handleGenerationPicTask = new HandleGenerationPicTask();
			handleGenerationPicTask.execute();
			break;
		}
	}

	class MyGPHorizontalScrollViewListener implements GPHorizontalScrollView.GPHorizontalScrollViewListener{

		@Override
		public void onScrollChanged(final int l, int t, int oldl, int oldt) {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if(horizontalScrollViewTotalWidth<=0)
					{
						return;
					}
					videoView.pause();
					curTime = (int) (videoTimes * l / horizontalScrollViewTotalWidth);
					videoView.seekTo(curTime);
				}
			});
		}
	}

	private void startProgressDialog() {
		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(this);
			progressDialog.setCancelable(false);
			progressDialog.setMessage("视频处理中...");
		}

		progressDialog.show();
	}

	private void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
	
	class HandleGenerationGifTask extends AsyncTask<Void, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			startProgressDialog();
		}

		@Override
		protected String doInBackground(Void... params) {
			//生成gif
			String gifPath = null;
			try {
				gifPath = GPFfmpegManager.GenerationGif(VideoProcessActivity.this,
						videoLocalPath, String.valueOf((curTime + 0.0) / 1000));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return gifPath;

			//生成水印
//			try {
//				GPFfmpegManager.WaterMark(VideoProcessActivity.this, videoLocalPath);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if(!Basic_StringUtil.isNullOrEmpty(result)) {
				gotoPreview(result);
			}
			stopProgressDialog();
		}
		
		private void gotoPreview(String gifPath) {
			Intent intent = new Intent();
			intent.putExtra(PreviewVideoProcessResultActivity.KEY_TYPE,
					PreviewVideoProcessResultActivity.VALUE_PREVIEW_GIF);
			intent.putExtra(PreviewVideoProcessResultActivity.KEY_GIF_LOCALPATH, 
					gifPath);
			intent.setClass(VideoProcessActivity.this, 
					PreviewVideoProcessResultActivity.class);
			startActivity(intent);
		}
	}
	
	class HandleGenerationPicTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			startProgressDialog();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				GPFfmpegManager.generationPic(VideoProcessActivity.this,
						videoLocalPath, String.valueOf((curTime + 0.0) / 1000),
						new GPFfmpegHandleListener() {

							@Override
							public void finish(int exitValue, String outPath) {
								if (exitValue == 0) {
									try {
										String picPath = generationPic(outPath);
										gotoPreview(picPath);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						});
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			stopProgressDialog();
		}

		private String generationPic(String picCatalog) throws IOException {
			String result = picCatalog+File.separator+System.currentTimeMillis()+".jpg";
			
			File picCatalogFile = new File(picCatalog);
			File[] childFiles = picCatalogFile.listFiles();
			
			//视频缩略图
			Bitmap bmpVideoThubnail = BitmapFactory.decodeFile(childFiles[0].getCanonicalPath());
			int bmpVideoThubnailWidth = bmpVideoThubnail.getWidth();
			int bmpVideoThubnailHeight = bmpVideoThubnail.getHeight();
			
			//二维码
			int qrWidth = 180;
			int qrHeight = 180;
			Bitmap qrBitmap = createQRImage(shareUrl, qrWidth, qrHeight);
			
			//文字
			Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG
	                | Paint.DEV_KERN_TEXT_FLAG);  
			textPaint.setColor(Color.parseColor("#afaba1"));  
			textPaint.setTextSize(20f);  
			textPaint.setTextAlign(Paint.Align.RIGHT);
	        Rect textBounds = new Rect();  
	        textPaint.getTextBounds(videoAuthor, 0, videoAuthor.length(), textBounds);
	        int textHeight = textBounds.bottom - textBounds.top;
	        
	        int topHeight = textHeight + 30;
	        
			int bmpwidth = bmpVideoThubnailWidth*3 + 5*2 + 15*2;
			int bmpHeight = topHeight + bmpVideoThubnailHeight*3 + 5*2 + qrHeight;
			
			Bitmap bitmapResult = Bitmap.createBitmap(bmpwidth, bmpHeight, bmpVideoThubnail.getConfig());
			Canvas canvas = new Canvas(bitmapResult);
			canvas.drawColor(Color.parseColor("#ffffff"));
			  
	        canvas.drawText(videoAuthor, bmpVideoThubnailWidth*3+15+5*2, 15+textHeight, textPaint); 
	        
			Bitmap bitmap1 = BitmapFactory.decodeFile(childFiles[0].getCanonicalPath());
			canvas.drawBitmap(bitmap1, 15, topHeight, null);
			Bitmap bitmap2 = BitmapFactory.decodeFile(childFiles[1].getCanonicalPath());
			canvas.drawBitmap(bitmap2, bmpVideoThubnailWidth+20, topHeight, null);
			Bitmap bitmap3 = BitmapFactory.decodeFile(childFiles[2].getCanonicalPath());
			canvas.drawBitmap(bitmap3, bmpVideoThubnailWidth*2+25, topHeight, null);
			
			Bitmap bitmap4 = BitmapFactory.decodeFile(childFiles[3].getCanonicalPath());
			canvas.drawBitmap(bitmap4, 15, bmpVideoThubnailHeight+5+topHeight, null);
			Bitmap bitmap5 = BitmapFactory.decodeFile(childFiles[4].getCanonicalPath());
			canvas.drawBitmap(bitmap5, bmpVideoThubnailWidth+20, bmpVideoThubnailHeight+5+topHeight, null);
			Bitmap bitmap6 = BitmapFactory.decodeFile(childFiles[5].getCanonicalPath());
			canvas.drawBitmap(bitmap6, bmpVideoThubnailWidth*2+25, bmpVideoThubnailHeight+5+topHeight, null);
			
			Bitmap bitmap7 = BitmapFactory.decodeFile(childFiles[6].getCanonicalPath());
			canvas.drawBitmap(bitmap7, 15, bmpVideoThubnailHeight*2+10+topHeight, null);
			Bitmap bitmap8 = BitmapFactory.decodeFile(childFiles[7].getCanonicalPath());
			canvas.drawBitmap(bitmap8, bmpVideoThubnailWidth+20, bmpVideoThubnailHeight*2+10+topHeight, null);
			Bitmap bitmap9 = BitmapFactory.decodeFile(childFiles[8].getCanonicalPath());
			canvas.drawBitmap(bitmap9, bmpVideoThubnailWidth*2+25, bmpVideoThubnailHeight*2+10+topHeight, null);
			
			canvas.drawBitmap(qrBitmap, bmpwidth-qrWidth, bmpHeight-qrHeight, null);
			
			FileOutputStream out = new FileOutputStream(result);  
			bitmapResult.compress(Bitmap.CompressFormat.JPEG, 90, out);  
			return result;
		}
		
		private void gotoPreview(final String picPath) {
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Intent intent = new Intent();
					intent.putExtra(PreviewVideoProcessResultActivity.KEY_TYPE,
							PreviewVideoProcessResultActivity.VALUE_PREVIEW_PIC);
					intent.putExtra(PreviewVideoProcessResultActivity.KEY_PIC_LOCALPATH, 
							picPath);
					intent.putExtra(PreviewVideoProcessResultActivity.KEY_VIDEO_URL,
							videoUrl);
					intent.setClass(VideoProcessActivity.this, 
							PreviewVideoProcessResultActivity.class);
					startActivity(intent);
				}
			});
		}
		
		public Bitmap createQRImage(String url, int qrWidth, int qrHeight) {
			try {
				// 判断URL合法性
				if (url == null || "".equals(url) || url.length() < 1) {
					return null;
				}
				Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
				hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
				// 图像数据转换，使用了矩阵转换
				BitMatrix bitMatrix = new QRCodeWriter().encode(url,
						BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
				int[] pixels = new int[qrWidth * qrHeight];
				// 下面这里按照二维码的算法，逐个生成二维码的图片，
				// 两个for循环是图片横列扫描的结果
				for (int y = 0; y < qrHeight; y++) {
					for (int x = 0; x < qrWidth; x++) {
						if (bitMatrix.get(x, y)) {
							pixels[y * qrWidth + x] = 0xff000000;
						} else {
							pixels[y * qrWidth + x] = 0xffffffff;
						}
					}
				}
				// 生成二维码图片的格式，使用ARGB_8888
				Bitmap bitmap = Bitmap.createBitmap(qrWidth, qrHeight,
						Bitmap.Config.ARGB_8888);
				bitmap.setPixels(pixels, 0, qrWidth, 0, 0, qrWidth, qrHeight);
				return bitmap;
			} catch (WriterException e) {
				e.printStackTrace();
			}
			return null;
		}
		
	}
}
