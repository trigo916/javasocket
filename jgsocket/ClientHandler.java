import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final DataStore dataStore;
    private final DataPersistence persistence;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket, DataStore dataStore, DataPersistence persistence) {// 构造函数，初始化客户端处理器
        this.clientSocket = socket;
        this.dataStore = dataStore;
        this.persistence = persistence;
    }

    // 实现Runnable接口的run方法，处理客户端请求
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {// 读取客户端输入，直到输入为null
                String response = processCommand(inputLine);
                out.println(response);

                // 如果命令修改了数据存储，则保存数据
                if (inputLine.startsWith("set ") || inputLine.startsWith("del ") || 
                    inputLine.startsWith("lpush ") || inputLine.startsWith("rpush ") || 
                    inputLine.startsWith("lpop ") || inputLine.startsWith("rpop ") || 
                    inputLine.startsWith("ldel ")) {
                    persistence.saveData(dataStore);
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    // 处理客户端发送的命令
    private String processCommand(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length == 0) return "Error: Empty command";

        switch (parts[0].toLowerCase()) {
            case "ping":
                return "pong";
                
            case "help":
                if (parts.length == 1) {
                    return getHelpMessage();
                } else {
                    return getCommandHelp(parts[1]);
                }
                
            case "set":
                if (parts.length != 3) return "Error: Invalid number of arguments for SET";
                return dataStore.set(parts[1], parts[2]);
                
            case "get":
                if (parts.length != 2) return "Error: Invalid number of arguments for GET";
                return dataStore.get(parts[1]);
                
            case "del":
                if (parts.length != 2) return "Error: Invalid number of arguments for DEL";
                return dataStore.del(parts[1]);
                
            case "lpush":
                if (parts.length != 3) return "Error: Invalid number of arguments for LPUSH";
                return dataStore.lpush(parts[1], parts[2]);
                
            case "rpush":
                if (parts.length != 3) return "Error: Invalid number of arguments for RPUSH";
                return dataStore.rpush(parts[1], parts[2]);
                
            case "range":
                if (parts.length != 4) return "Error: Invalid number of arguments for RANGE";
                try {
                    int start = Integer.parseInt(parts[2]);
                    int end = Integer.parseInt(parts[3]);
                    return dataStore.range(parts[1], start, end);
                } catch (NumberFormatException e) {
                    return "Error: Invalid range parameters";
                }
                
            case "len":
                if (parts.length != 2) return "Error: Invalid number of arguments for LEN";
                return dataStore.len(parts[1]);
                
            case "lpop":
                if (parts.length != 2) return "Error: Invalid number of arguments for LPOP";
                return dataStore.lpop(parts[1]);
                
            case "rpop":
                if (parts.length != 2) return "Error: Invalid number of arguments for RPOP";
                return dataStore.rpop(parts[1]);
                
            case "ldel":
                if (parts.length != 2) return "Error: Invalid number of arguments for LDEL";
                return dataStore.ldel(parts[1]);
                
            default:
                return "Error: Unknown command";
        }
    }

    private String getHelpMessage() {// 获取帮助信息
        return "Available commands:\n" +
               "SET key value - Store key-value pair\n" +
               "GET key - Get value for key\n" +
               "DEL key - Delete key\n" +
               "LPUSH key value - Add value to left of list\n" +
               "RPUSH key value - Add value to right of list\n" +
               "RANGE key start end - Get range of list values\n" +
               "LEN key - Get length of list\n" +
               "LPOP key - Remove and get leftmost value\n" +
               "RPOP key - Remove and get rightmost value\n" +
               "LDEL key - Delete entire list\n" +
               "PING - Check server status\n" +
               "HELP - Show this message\n" +
               "HELP command - Show help for specific command";
    }

    private String getCommandHelp(String command) { // 获取特定命令的帮助信息
        switch (command.toLowerCase()) {
            case "set":
                return "SET key value\nStore a key-value pair in the string store";
            case "get":
                return "GET key\nRetrieve the value associated with a key";
            case "del":
                return "DEL key\nDelete a key-value pair from the string store";
            case "lpush":
                return "LPUSH key value\nAdd a value to the left end of a list";
            case "rpush":
                return "RPUSH key value\nAdd a value to the right end of a list";
            case "range":
                return "RANGE key start end\nGet a range of values from a list (0-based indices)";
            case "len":
                return "LEN key\nGet the length of a list";
            case "lpop":
                return "LPOP key\nRemove and get the leftmost value from a list";
            case "rpop":
                return "RPOP key\nRemove and get the rightmost value from a list";
            case "ldel":
                return "LDEL key\nDelete an entire list";
            case "ping":
                return "PING\nCheck if the server is responding";
            default:
                return "Unknown command";
        }
    }
} 