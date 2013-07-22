package com.daxun.dxeye;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

import static com.daxun.dxeye.Constants.*;

/**
 * Created by luhuiguo on 13-7-19.
 */
public class Monitor implements IoHandler {

    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    private static final int MSG_FRAME = 2;
    private static final int MSG_INVALID_PACKET= 3;
    private static final int MSG_NOT_VIDEO_PACKET= 4;
    private static final int MSG_UNSUPPORTED_CODEC= 5;
    private static final int MSG_OPEN_DECODER_FAILED= 6;
    private static final int MSG_WAIT_KEY_FRAME= 7;
    private static final int MSG_VIDEO_DECODE_FAILED= 8;


    private static final String TAG = Monitor.class.getSimpleName();


    private Channel channel;

    private Bitmap bitmap;

    private int stream;

    private IoSession session;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case MSG_SUCCESS:
//
//                    break;
//
//                case MSG_FAILURE:
//
//                    break;
            }
        }
    };

    private Thread mThread;


    public Monitor(Channel channel) {

        this.channel = channel;
    }

    public void play(int stream) {
        this.stream = stream;

        if (mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    NioSocketConnector connector = new NioSocketConnector();
                    connector.setConnectTimeoutMillis(10000);

                    connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new SNVRCodecFactory()));
                    connector.getFilterChain().addLast("logger", new LoggingFilter());
                    connector.setHandler(Monitor.this);

                    session = connector.connect(new InetSocketAddress(SNVRClient.getInstance().getHost(), SNVRClient.getInstance().getPort())).awaitUninterruptibly().getSession();

                }
            });


        }

        mThread.start();


    }

    public void stop() {

    }


    @Override
    public void sessionCreated(IoSession session) throws Exception {

    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.write(new PreviewRequest(SNVRClient.getInstance().getToken(),(short)channel.getId(),stream));

    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {

    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus idleStatus) throws Exception {

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable throwable) throws Exception {
        Log.d(TAG,"exceptionCaught",throwable);
        session.close(true);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        Log.d(TAG,"messageReceived: "+message);
        if(message instanceof PreviewResponse){
            PreviewResponse previewResponse = (PreviewResponse) message;

            if (previewResponse.getStatus() == 0){
                mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
            }else{
                mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
            }

        }else if(message instanceof PreviewData){
            PreviewData previewData = (PreviewData) message;
            Payload p = previewData.getPayload();
            Bitmap bitmap =  Bitmap.createBitmap(320, 240, Bitmap.Config.ARGB_8888);
            int ret = Extractor.extractFrame(p,bitmap);
            Log.d(TAG,"ret: "+ret);


            mHandler.obtainMessage(MSG_FRAME,bitmap).sendToTarget();

        }

    }

    @Override
    public void messageSent(IoSession session, Object o) throws Exception {

    }
}
