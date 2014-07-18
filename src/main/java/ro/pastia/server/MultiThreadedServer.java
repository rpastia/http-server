package ro.pastia.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class MultiThreadedServer implements Runnable {

    protected int serverPort = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    protected ExecutorService threadPool;

    private static final Logger logger = LoggerFactory.getLogger(MultiThreadedServer.class);

    public MultiThreadedServer(int port) {
        threadPool = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(20));
        serverPort = port;
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
            this.threadPool.execute(new RequestHandler(clientSocket, MimeTypeResolver.getInstance()));
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