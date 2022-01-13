package uk.gov.dwp.jsa.citizen_ui.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.Socket;

public class RedisServerBean implements InitializingBean, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisServerBean.class);

    private RedisServer redisServer;

    private String hostname;

    private int port;

    private boolean embedded;

    public final void afterPropertiesSet() {
        if (embedded) {
            if (isPortInUse(null, port)) {
                LOGGER.debug("embedded redisServer is already running on port: {}", port);
                port++;
            }

            LOGGER.debug("embedded redisServer initialising on port: {}", port);
            redisServer = new RedisServer(port);
            redisServer.start();
        } else {
            LOGGER.debug("embedded redisServer not required");
        }
    }

    public final void destroy() {
        if (redisServer != null) {
            LOGGER.debug("embedded redisServer shutting down");
            redisServer.stop();
        }
    }

    /**
     * This method checks if the given port is already in use.
     * Different TestContext instances might be created and each context creation will not know if the previous context
     * created a embedded redisServer already. Embedded redisServer does not re-use a previous instance. It does not
     * check if there is proper connectivity before starting itself, which causes the applicationContext to not be
     * loaded correctly as previous context was not destroyed yet and it might still be holding a redis instance.
     *
     * @param host
     * @param port
     * @return true if a given port is already in use.
     */
    private boolean isPortInUse(final String host, final int port) {
        try (final Socket ignored = new Socket(host, port)) {
            return true;
        } catch (IOException ignore) {
            return false;
        }
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public boolean getEmbedded() {
        return embedded;
    }

    public void setEmbedded(final boolean embedded) {
        this.embedded = embedded;
    }
}
