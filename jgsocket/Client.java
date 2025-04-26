import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 6379;

    public static void main(String[] args) {
        try {
            // 加载配置
            Properties config = new Properties();
            config.load(new FileInputStream("config.properties"));
            int port = Integer.parseInt(config.getProperty("server.port", String.valueOf(DEFAULT_PORT)));

            // 创建socket连接到服务器，并创建输入输出流
            try (Socket socket = new Socket(DEFAULT_HOST, port);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 Scanner scanner = new Scanner(System.in)) {

                System.out.println("Connected to server. Type 'exit' to quit.");
                System.out.println("Type 'help' to see available commands.");

                while (true) {// 命令输入循环
                    System.out.print("> ");
                    String command = scanner.nextLine().trim();

                    if (command.equalsIgnoreCase("exit")) {
                        break;
                    }

                    out.println(command);
                    String response = in.readLine();
                    System.out.println(response);
                }
            }
        } catch (IOException e) { //异常处理
            System.err.println("Client error: " + e.getMessage());
        }
    }
} 