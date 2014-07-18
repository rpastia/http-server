package ro.pastia.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MultiThreadedApp {

    private static final Logger logger = LoggerFactory.getLogger(MultiThreadedApp.class);
    public static final Properties serverConfig = new Properties();

    public static void main(String[] args) {
        logger.debug("Preparing to start server...");

        if (args.length == 1) {
            loadProperties(args[0]);
        } else {
            logger.warn("Properties file should be passed as a command line argument!");
            loadProperties(null);
        }

        int port = Integer.parseInt(serverConfig.getProperty("server.port"));
        int maxThreads = Integer.parseInt(
            serverConfig.getProperty("server.multithread.maxThreads"));
        String basePath = serverConfig.get("server.basePath").toString();

        final MultiThreadedServer server =
            new MultiThreadedServer(port, maxThreads, basePath, serverConfig);
        new Thread(server).start();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                logger.info("Stopping server!");
                server.stop();
            }
        }));


        MimeTypeResolver.getInstance();

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
