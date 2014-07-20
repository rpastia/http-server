package ro.pastia.server;

import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Bootstraps a <code>MultiThreadedServer</code> instance
 */
public class MultiThreadedApp {

    public static final String PROPERTY_DEBUG = "app.debug";

    public static final Properties serverConfig = new Properties();
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadedApp.class);

    public static void main(String[] args) {
        logger.debug("Preparing to start server...");

        if (args.length == 1) {
            loadProperties(args[0]);
        } else {
            logger.warn("Properties file should be passed as a command line argument!");
            loadProperties(null);
        }

        int port = Integer.parseInt(serverConfig.getProperty(MultiThreadedServer.PROPERTY_PORT));
        int maxThreads = Integer.parseInt(serverConfig.getProperty(MultiThreadedServer.PROPERTY_MULTITHREAD_MAXTHREADS));
        String basePath = serverConfig.get(MultiThreadedServer.PROPERTY_BASEPATH).toString();

        boolean debug = Boolean.parseBoolean(serverConfig.get(PROPERTY_DEBUG).toString());
        if (debug) {
            org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
            root.setLevel(Level.DEBUG);
            logger.debug("Running in debug mode!");
        }

        final MultiThreadedServer server = new MultiThreadedServer(port, maxThreads, basePath, serverConfig);
        new Thread(server).start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                logger.info("Shutdown command received!");
                server.stop();
            }
        }));
    }

    private static void loadProperties(String propPath) {
        //Load default properties
        try (
                InputStream defaultPropFile = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("server.properties")
        ) {
            serverConfig.load(defaultPropFile);
        } catch (IOException e) {
            throw new RuntimeException("Can't load default properties for server!", e);
        }

        //Override default properties
        if (propPath != null) {
            try (FileInputStream propFile = new FileInputStream(propPath)) {
                serverConfig.load(propFile);
            } catch (IOException e) {
                logger.warn("Can't load from specified properties file!");
            }
        }
    }


}
