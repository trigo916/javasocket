//数据存储
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private final ConcurrentHashMap<String, String> stringStore;
    private final ConcurrentHashMap<String, LinkedList<String>> listStore;

    public DataStore() {
        stringStore = new ConcurrentHashMap<>();
        listStore = new ConcurrentHashMap<>();
    }

    // String的操作
    public synchronized String set(String key, String value) { //将键值对存入stringStore，返回"OK"表示操作成功
        stringStore.put(key, value);
        return "OK";
    }

    public synchronized String get(String key) {//根据键获取值，如果键不存在，返回"(nil)"
        return stringStore.getOrDefault(key, "(nil)");
    }

    public synchronized String del(String key) { //删除键值对，如果键存在并成功删除，返回"1"，否则返回"0"
        return stringStore.remove(key) != null ? "1" : "0";
    }

    // List的操作
    public synchronized String lpush(String key, String value) { //将值插入到指定键的列表的头部，然后返回"OK"
        listStore.computeIfAbsent(key, k -> new LinkedList<>()).addFirst(value);
        return "OK";
    }

    public synchronized String rpush(String key, String value) {//将值插入到指定键的列表的尾部，然后返回"OK"
        listStore.computeIfAbsent(key, k -> new LinkedList<>()).addLast(value);
        return "OK";
    }

    public synchronized String range(String key, int start, int end) { //获取指定键的列表中从start到end的元素，然后返回字符串
        LinkedList<String> list = listStore.get(key);
        if (list == null) return "(nil)";
        
        int size = list.size();
        if (start < 0) start = size + start;
        if (end < 0) end = size + end;
        
        if (start >= size || end < 0) return "(nil)";
        
        StringBuilder result = new StringBuilder();
        for (int i = start; i <= end && i < size; i++) {
            result.append(i + 1).append(") ").append(list.get(i)).append("\n");
        }
        return result.toString();
    }

    public synchronized String len(String key) {
        LinkedList<String> list = listStore.get(key);
        return list != null ? String.valueOf(list.size()) : "0";
    }

    public synchronized String lpop(String key) {
        LinkedList<String> list = listStore.get(key);
        return list != null && !list.isEmpty() ? list.removeFirst() : "(nil)";
    }

    public synchronized String rpop(String key) {
        LinkedList<String> list = listStore.get(key);
        return list != null && !list.isEmpty() ? list.removeLast() : "(nil)";
    }

    public synchronized String ldel(String key) {
        return listStore.remove(key) != null ? "1" : "0";
    }

    // 获取所有用于持久化的key
    public Set<String> getAllStringKeys() {
        return stringStore.keySet();
    }

    public Set<String> getAllListKeys() {
        return listStore.keySet();
    }

    public String getStringValue(String key) {
        return stringStore.get(key);
    }

    public LinkedList<String> getListValue(String key) {
        return listStore.get(key);
    }
} 