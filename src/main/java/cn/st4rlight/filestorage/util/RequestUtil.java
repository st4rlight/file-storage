package cn.st4rlight.filestorage.util;

import java.util.UUID;

public class RequestUtil {
    public static final ThreadLocal<UUID> uuidLocal = new ThreadLocal<>();
    public static final ThreadLocal<Long> timeCostLocal = new ThreadLocal<>();


    // requestId部分
    public static UUID getRequestId(){
        UUID uuid = uuidLocal.get();
        if(uuid == null) {
            uuid = UUID.randomUUID();
            uuidLocal.set(uuid);
        }

        return uuid;
    }
    public static void removeRequestId(){
        uuidLocal.remove();
    }



    // 请求时间部分
    public static void setStartTime(){
        timeCostLocal.set(System.currentTimeMillis());
    }
    public static long getStartTime(){
        return timeCostLocal.get();
    }
    public static void removeStartTime(){
        timeCostLocal.remove();
    }

}
