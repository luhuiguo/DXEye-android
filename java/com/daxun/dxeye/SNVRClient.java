package com.daxun.dxeye;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by luhuiguo on 13-6-28.
 */
public class SNVRClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SNVRClient.class);

    public static final long TIMEOUT = 10 * 1000L;

    private static SNVRClient sharedInstance;

    private String host;

    private int port;

    private String token;

    private List<Channel> channels;

    private List<Channel> monitors;

    //private NioSocketConnector connector;


    public static SNVRClient getInstance() {
        if (sharedInstance == null) {
            synchronized (SNVRClient.class) {
                if (sharedInstance == null) {
                    sharedInstance = new SNVRClient();
                }
            }
        }
        return sharedInstance;
    }

    public SNVRClient() {


    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Channel> getChannels() {
        if (channels == null) {
            channels = new ArrayList<Channel>();
        }
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public List<Channel> getMonitors() {

        if (monitors == null) {
            monitors = new ArrayList<Channel>();
        }
        return monitors;
    }

    public void setMonitors(List<Channel> monitors) {
        this.monitors = monitors;
    }

    public boolean login(String username, String password) {

        boolean ret = false;

        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(TIMEOUT);
        connector.getSessionConfig().setUseReadOperation(true);

        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new SNVRCodecFactory()));
        connector.getFilterChain().addLast("logger", new LoggingFilter());

        LOGGER.debug("Connectting to {}:{}", host, port);

        ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
        future.awaitUninterruptibly();
        if (!future.isConnected()) {
            return false;
        }
        IoSession session = future.getSession();

        try {
            LOGGER.debug("Logining {}:{}", username, password);
            session.write(new LoginRequest(username, password)).awaitUninterruptibly();

            ReadFuture readFuture = session.read();

            if (readFuture.awaitUninterruptibly(TIMEOUT, TimeUnit.MILLISECONDS)) {

                LoginResponse message = (LoginResponse) readFuture.getMessage();

                LOGGER.debug("Read message:{}", message);

                if (0 == message.getStatus()) {
                    ret = true;
                    token = message.getToken();
                    LOGGER.debug("Login success, tokin: {}", token);
                } else {
                    ret = false;
                }

            } else {
                ret = false;

            }

        } catch (Exception e) {
            LOGGER.warn("Login failed", e);
            return false;
        } finally {
            session.close(true);
            session.getService().dispose();

        }

        LOGGER.debug("ret:{}", ret);
        return ret;
    }


    public List<Channel> channel() {

        List<Channel> list = new ArrayList<Channel>();


        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(TIMEOUT);
        connector.getSessionConfig().setUseReadOperation(true);

        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new SNVRCodecFactory()));
        connector.getFilterChain().addLast("logger", new LoggingFilter());

        LOGGER.debug("Connectting to {}:{}", host, port);
        IoSession session = connector.connect(new InetSocketAddress(host, port)).awaitUninterruptibly().getSession();

        try {
            session.write(new ChannelRequest(token)).awaitUninterruptibly();

            ReadFuture readFuture = session.read();

            if (readFuture.awaitUninterruptibly(TIMEOUT, TimeUnit.MILLISECONDS)) {

                ChannelResponse message = (ChannelResponse) readFuture.getMessage();

                LOGGER.debug("Read message:{}", message);


                if (0 == message.getStatus()) {

                    list.addAll(message.getChannels());

                }

            }

        } catch (Exception e) {
            LOGGER.warn("Login failed", e);

        } finally {
            session.close(true);
            session.getService().dispose();

        }
        channels = list;

        LOGGER.debug("channels:{}", list);
        return list;

    }

    public void logout() {


        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(TIMEOUT);
        connector.getSessionConfig().setUseReadOperation(true);

        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new SNVRCodecFactory()));
        connector.getFilterChain().addLast("logger", new LoggingFilter());

        LOGGER.debug("Connectting to {}:{}", host, port);

        IoSession session = connector.connect(new InetSocketAddress(host, port)).awaitUninterruptibly().getSession();

        try {
            session.write(new LogoutRequest(token)).awaitUninterruptibly();

            ReadFuture readFuture = session.read();


            if (readFuture.awaitUninterruptibly(TIMEOUT, TimeUnit.MILLISECONDS)) {

                LogoutResponse message = (LogoutResponse) readFuture.getMessage();

                LOGGER.debug("Read message:{}", message);

                token = null;
                channels = null;
                monitors = null;


            }

        } catch (Exception e) {
            LOGGER.warn("Login failed", e);
        } finally {
            session.close(true);
            session.getService().dispose();

        }
    }

    public void ptz(short channel, byte command, byte option) {


        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(TIMEOUT);
        connector.getSessionConfig().setUseReadOperation(true);

        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new SNVRCodecFactory()));
        connector.getFilterChain().addLast("logger", new LoggingFilter());

        LOGGER.debug("Connectting to {}:{}", host, port);

        IoSession session = connector.connect(new InetSocketAddress(host, port)).awaitUninterruptibly().getSession();

        try {
            session.write(new PtzRequest(token, channel, command, option)).awaitUninterruptibly();

            ReadFuture readFuture = session.read();


            if (readFuture.awaitUninterruptibly(TIMEOUT, TimeUnit.MILLISECONDS)) {

                PtzResponse message = (PtzResponse) readFuture.getMessage();

                LOGGER.debug("Read message:{}", message);
                if (0 == message.getStatus()) {

                } else {

                }


            }

        } catch (Exception e) {
            LOGGER.warn("Login failed", e);
        } finally {
            session.close(true);
            session.getService().dispose();

        }
    }


//    public IoSession preview(short channel, int stream) {
//        NioSocketConnector connector = new NioSocketConnector();
//        connector.setConnectTimeoutMillis(TIMEOUT);
//
//        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new SNVRCodecFactory()));
//        connector.getFilterChain().addLast("logger", new LoggingFilter());
//        connector.setHandler(new PreviewHandler(token, channel, stream));
//
//        LOGGER.debug("Connectting to {}:{}", host, port);
//
//        IoSession session = connector.connect(new InetSocketAddress(host, port)).awaitUninterruptibly().getSession();
//
//        return session;
//
//    }


}
