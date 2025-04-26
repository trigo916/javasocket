import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int MAX_CLIENTS = 100;
    private static ServerSocket serverSocket;
    private static ExecutorService threadPool;
    private static DataStore dataStore;
    private static DataPersistence persistence;

    public static void main(String[] args) {
        try {
            // 加载配置
            Properties config = new Properties();
            config.load(new FileInputStream("config.properties"));
            int port = Integer.parseInt(config.getProperty("server.port", "6379"));

            // 初始化组件
            dataStore = new DataStore();
            persistence = new DataPersistence();
            threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);

            // 加载持久化数据
            persistence.loadData(dataStore);

            // server开始工作
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            // 接受Client连接
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                threadPool.execute(new ClientHandler(clientSocket, dataStore, persistence));
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        } finally {
            if (threadPool != null) {
                threadPool.shutdown();
            }
        }
    }
} 