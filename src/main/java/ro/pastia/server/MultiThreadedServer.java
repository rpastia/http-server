package ro.pastia.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.*;

public class MultiThreadedServer implements Runnable {

    private int serverPort;
    private ServerSocket serverSocket;
    private boolean isStopped = false;
    private Thread runningThread;
    private ExecutorService threadPool;
    private String basePath;

    private Properties serverConfig;

    private static final Logger logger = LoggerFactory.getLogger(MultiThreadedServer.class);

    public static final String PROPERTY_SERVER_NAME = "server.name";
    public static final String PROPERTY_SERVER_PORT = "server.port";
    public static final String PROPERTY_SERVER_BASEPATH = "server.basePath";

  public MultiThreadedServer(int port, int maxThreads, String basePath, Properties serverConfig) {
      this.threadPool = new ThreadPoolExecutor(maxThreads, maxThreads, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(20));
      this.serverPort = port;
      this.basePath = basePath;
      this.serverConfig = serverConfig;
    }

    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }

        openServerSocket();

      while (!isStopped()) {
        Socket clientSocket = null;
        try {
          clientSocket = this.serverSocket.accept();
        } catch (IOException e) {
          if (isStopped()) {
            System.out.println("Server Stopped.");
            return;
          }
          throw new RuntimeException("Error accepting client connection", e);
        }
        this.threadPool.execute(
            new RequestHandler(clientSocket, MimeTypeResolver.getInstance(),
                basePath, serverConfig) );
      }

        this.threadPool.shutdown();
        System.out.println("Server Stopped.");
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
}