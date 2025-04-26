//数据的持久化存储（保存与加载）
import java.io.*;
import java.util.*;

public class DataPersistence {
    private static final String STRING_STORE_FILE = "string_store.dat";
    private static final String LIST_STORE_FILE = "list_store.dat";

    public void saveData(DataStore dataStore) {
        try {
            // Save string store
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STRING_STORE_FILE))) {
                Map<String, String> stringData = new HashMap<>();
                for (String key : dataStore.getAllStringKeys()) {
                    stringData.put(key, dataStore.getStringValue(key));
                }
                oos.writeObject(stringData);
            } //将所有字符串键值对存入HashMap，然后序列化到文件

            // Save list store
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LIST_STORE_FILE))) {
                Map<String, LinkedList<String>> listData = new HashMap<>();
                for (String key : dataStore.getAllListKeys()) {
                    listData.put(key, dataStore.getListValue(key));
                }
                oos.writeObject(listData);
            }  //将所有列表数据存入HashMap，然后序列化到文件
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadData(DataStore dataStore) {
        try {
            // Load string store
            if (new File(STRING_STORE_FILE).exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STRING_STORE_FILE))) {
                    Map<String, String> stringData = (Map<String, String>) ois.readObject();
                    for (Map.Entry<String, String> entry : stringData.entrySet()) {
                        dataStore.set(entry.getKey(), entry.getValue());
                    }
                }
            }  //从文件读取HashMap，然后逐个存入DataStore

            // Load list store
            if (new File(LIST_STORE_FILE).exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(LIST_STORE_FILE))) {
                    Map<String, LinkedList<String>> listData = (Map<String, LinkedList<String>>) ois.readObject();
                    for (Map.Entry<String, LinkedList<String>> entry : listData.entrySet()) {
                        LinkedList<String> list = entry.getValue();
                        for (String value : list) {
                            dataStore.rpush(entry.getKey(), value);
                        }
                    }
                }
           }    //从文件读取HashMap，然后使用rpush将列表元素逐个存入DataStore
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }
} 