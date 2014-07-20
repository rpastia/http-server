package ro.pastia.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A Simple Multi-Threaded HTTP Server
 * <p>
 * Implements a simple Multi-Threaded HTTP Server with a very limited set of functionality.
 * Supports basic directory listings and streaming files.
 * </p>
 */
public class MultiThreadedServer implements Runnable {

    /**
     * Name of the server as it will be used in headers and server signature.
     */
    public static final String NAME = "Simple Server/1.0";

    public static final String PROPERTY_PORT = "server.port";
    public static final String PROPERTY_BASEPATH = "server.basePath";
    public static final String PROPERTY_MULTITHREAD_MAXTHREADS = "server.multithread.maxThreads";

    private static final Logger logger = LoggerFactory.getLogger(MultiThreadedServer.class);
    private int serverPort;
    private ServerSocket serverSocket;
    private boolean isStopped = false;
    private ExecutorService threadPool;
    private String basePath;
    private Properties serverConfig;


    /**
     * Constructs a MultiThreadedServer
     *
     * @param port         the port that the server should listen on
     * @param maxThreads   the number of threads the server should use to handle requests; the server also keep a queue
     *                     of requests if all worker threads are busy with a size of <code>maxThreads * 4</code>
     * @param basePath     the base path out of which documents will be server (root of the server)
     * @param serverConfig contains other configuration parameters
     */
    public MultiThreadedServer(int port, int maxThreads, String basePath, Properties serverConfig) {
        threadPool = new ThreadPoolExecutor(
                maxThreads, maxThreads, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(maxThreads*4));
        this.serverPort = port;
        this.basePath = basePath;
        this.serverConfig = serverConfig;
    }

    public void run() {
        openServerSocket();
        logger.info("Server started on port {}", serverPort);

        while (!isStopped()) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    logger.info("Server Stopped.");
                    return;
                } else {
                    throw new RuntimeException("Error accepting client connection", e);
                }
            }
            threadPool.execute(
                    new RequestHandler(clientSocket, MimeTypeResolver.getInstance(), basePath, serverConfig));
        }

        threadPool.shutdown();
        logger.info("Server Stopped.");
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        logger.info("Stopping server...");
        isStopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + serverPort, e);
        }
    }
}