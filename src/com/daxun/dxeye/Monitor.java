package com.daxun.dxeye;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.List;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * Created by luhuiguo on 13-7-19.
 */
public class Monitor extends SurfaceView implements Callback, IoHandler {

	private static final int MSG_SUCCESS = 0;
	private static final int MSG_FAILURE = 1;
	private static final int MSG_FRAME = 2;

	private static final String TAG = Monitor.class.getSimpleName();

	private SurfaceHolder mSurfaceHolder = null;

	private Channel mChannel;

	private int mVideoWidth;

	private int mVideoHeight;

	private Bitmap mBitmap;

	private boolean mEnableOSD;

	private boolean mEnableAudio;

	private int mStream;

	private IoSession mSession;

	private Extractor mExtractor;

	static class RenderHandler extends Handler {
		WeakReference<Monitor> mMonitor;

		RenderHandler(Monitor monitor) {
			mMonitor = new WeakReference<Monitor>(monitor);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SUCCESS:

				break;

			case MSG_FAILURE:

				break;
			case MSG_FRAME:
				Log.d(TAG, "handleMessage");
				try {
					//Bitmap bitmap = (Bitmap) msg.obj;
					Payload p = (Payload) msg.obj;

					Monitor monitor = mMonitor.get();
					Bitmap bitmap  = monitor.getBitmap();

					SurfaceHolder holder = monitor.getHolder();
					Canvas canvas = null;
					try {
						canvas = holder.lockCanvas();
						// Log.d(TAG, "canvas"+ canvas.getWidth() + " x "
						// +canvas.getHeight());

						if (canvas != null) {
							canvas.drawColor(Color.BLACK);
							Paint paint = new Paint();
							paint.setAntiAlias(true);
							paint.setFilterBitmap(true);
							paint.setDither(true);


							Matrix matrix = new Matrix();

							int dwidth = bitmap.getWidth();
							int dheight = bitmap.getHeight();

							int vwidth = canvas.getWidth();
							int vheight = canvas.getHeight();

							RectF rectSrc = new RectF();
							RectF rectDst = new RectF();
							rectSrc.set(0, 0, dwidth, dheight);
							rectDst.set(0, 0, vwidth, vheight);
							matrix.setRectToRect(rectSrc, rectDst,
									Matrix.ScaleToFit.START);
							

							
							float scale = Math.min((float) vwidth
										/ (float) dwidth, (float) vheight
										/ (float) dheight);
							
							int w = (int)(dwidth*scale);
							int h = (int)(dheight*scale);
							paint.setTextSize(h/15);
							
							canvas.drawBitmap(bitmap, matrix, paint);
							
							if (monitor.mEnableOSD) {
								paint.setColor(Color.WHITE);
								paint.setTypeface(Typeface.DEFAULT_BOLD);
								paint.setShadowLayer(1f, 0f, 1f, Color.GRAY);
								if (p.getLines() != null && p.getLines().size() > 0) {
									for (Line line : p.getLines()) {
										Rect bounds = new Rect();
										int x = line.getX();
										int y = line.getY();
										String text = line.getContent();
										//Log.e(TAG, "line:" + line);
										paint.getTextBounds(text, 0, text.length(), bounds);
										FontMetrics fontMetrics = paint.getFontMetrics();  
										if (x < 0) {
											x = w - bounds.width();
										}
										if (y < 0) {
											y = h - bounds.height() - (int)fontMetrics.bottom;
										}
										
										y = y - (int)fontMetrics.top;

										canvas.drawText(text, x, y, paint);

									}									
								}
						    
					        }
						}

					} finally {
						if (canvas != null)
							holder.unlockCanvasAndPost(canvas);
					}

				} catch (Exception e) {
					Log.w(TAG, e);

				}

				break;
			}
			super.handleMessage(msg);
		}
	};

	private Handler mHandler = new RenderHandler(this);
	private Thread mThread;

	public Monitor(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (!isInEditMode()) {
			initVideoView();
		}

	}

	public Monitor(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			initVideoView();
		}
	}

	public Monitor(Context context) {
		super(context);
		if (!isInEditMode()) {
			initVideoView();
		}
	}

	private void initVideoView() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		getHolder().addCallback(this);
		mExtractor = new Extractor();
	}

	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	// Log.d(TAG, "onMeasure " + widthMeasureSpec + " x " + heightMeasureSpec);
	// }

	public SurfaceHolder getSurfaceHolder() {
		return mSurfaceHolder;
	}

	public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
		mSurfaceHolder = surfaceHolder;
	}

	public Channel getChannel() {
		return mChannel;
	}

	public void setChannel(Channel channel) {
		mChannel = channel;
	}

	public Bitmap getBitmap() {
		return mBitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
	}

	public int getVideoWidth() {
		return mVideoWidth;
	}

	public void setVideoWidth(int videoWidth) {
		mVideoWidth = videoWidth;
	}

	public int getVideoHeight() {
		return mVideoHeight;
	}

	public void setVideoHeight(int videoHeight) {
		mVideoHeight = videoHeight;
	}

	public int getStream() {
		return mStream;
	}

	public void setStream(int stream) {
		mStream = stream;
	}

	public boolean isEnableOSD() {
		return mEnableOSD;
	}

	public void setEnableOSD(boolean enableOSD) {
		mEnableOSD = enableOSD;
	}

	public boolean isEnableAudio() {
		return mEnableAudio;
	}

	public void setEnableAudio(boolean enableAudio) {
		mEnableAudio = enableAudio;
	}

	public IoSession getmSession() {
		return mSession;
	}

	public void setmSession(IoSession mSession) {
		this.mSession = mSession;
	}

	public void play() {
		Log.d(TAG, "play");
		if (mChannel == null) {
			return;
		}

		// if (mThread == null) {
		mThread = new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					NioSocketConnector connector = new NioSocketConnector();
					connector.setConnectTimeoutMillis(10000);

					connector.getFilterChain().addLast("codec",
							new ProtocolCodecFilter(new SNVRCodecFactory()));
					connector.getFilterChain().addLast("logger",
							new LoggingFilter());
					connector.setHandler(Monitor.this);

					mSession = connector
							.connect(
									new InetSocketAddress(SNVRClient
											.getInstance().getHost(),
											SNVRClient.getInstance().getPort()))
							.awaitUninterruptibly().getSession();

					Log.d(TAG, "session" + mSession);

				} catch (Exception e) {
					Log.w(TAG, e);
				}

			}
		});

		// }

		mThread.start();

	}

	public void stop() {
		Log.d(TAG, "stop");
		try {
			if(mSession!=null){
				mSession.close(true);
			}
		} catch (Exception e) {
			Log.w(TAG, e);
		}

		

	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		Log.d(TAG, "sessionCreated");
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		Log.d(TAG, "sessionOpened");
		session.write(new PreviewRequest(SNVRClient.getInstance().getToken(),
				(short) mChannel.getId(), mStream));

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		Log.d(TAG, "sessionClosed");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus idleStatus)
			throws Exception {
		Log.d(TAG, "sessionIdle");
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable throwable)
			throws Exception {
		Log.d(TAG, "exceptionCaught", throwable);
		session.close(true);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		//Log.d(TAG, "channel " + mChannel + " messageReceived");
		// Log.d(TAG, "messageReceived: " + message);
		if (message instanceof PreviewResponse) {
			PreviewResponse previewResponse = (PreviewResponse) message;

			if (previewResponse.getStatus() == 0) {
				mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
			} else {
				mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
			}

		} else if (message instanceof PreviewData) {
			PreviewData previewData = (PreviewData) message;
			Payload p = previewData.getPayload();

			Log.d(TAG, "video size: " + p.getWidth() + " x " + p.getHeight());

			Bitmap b = mBitmap;
			if (p.getWidth() > 0 && p.getWidth() != mVideoWidth
					&& p.getHeight() > 0 && p.getHeight() != mVideoHeight) {
				b = Bitmap.createBitmap(p.getWidth(), p.getHeight(),
						Bitmap.Config.ARGB_8888);
			}

			int ret = mExtractor.extract(p, b);
			Log.d(TAG, "ret: " + ret);

			if (ret < 0) {

			} else {
//				if (mEnableOSD) {
//					drawOSDLines(b, p.getLines());
//				}
				mHandler.obtainMessage(MSG_FRAME, p).sendToTarget();
				mBitmap = b;
				mVideoWidth = p.getWidth();
				mVideoHeight = p.getHeight();
			}
		}

	}

	@Override
	public void messageSent(IoSession session, Object o) throws Exception {
		Log.d(TAG, "messageSent");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(TAG, "surfaceChanged " + width + " x " + height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");
		mSurfaceHolder = holder;
		play();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "surfaceDestroyed");
		stop();

	}

	public static void drawOSDLines(Bitmap bitmap, List<Line> lines) {
		if (lines != null && lines.size() > 0) {

			Canvas canvas = new Canvas(bitmap);

			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setDither(true);
			
			paint.setTextSize(bitmap.getHeight()/15);
			paint.setShadowLayer(1f, 0f, 1f, Color.GRAY);
			//canvas.drawText("Test", 100, 100, paint);
			
			for (Line line : lines) {
				Rect bounds = new Rect();
				int x = line.getX();
				int y = line.getY();
				String text = line.getContent();
				//Log.e(TAG, "line:" + line);
				paint.getTextBounds(text, 0, text.length(), bounds);
				FontMetrics fontMetrics = paint.getFontMetrics();  
				if (x < 0) {
					x = bitmap.getWidth() - bounds.width();
				}
				if (y < 0) {
					y = bitmap.getHeight() - bounds.height() - (int)fontMetrics.bottom;
				}
				
				y = y - (int)fontMetrics.top;

				canvas.drawText(text, x, y, paint);

			}
		}

	}
}
