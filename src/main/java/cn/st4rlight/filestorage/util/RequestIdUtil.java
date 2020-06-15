package cn.st4rlight.filestorage.util;

import java.util.UUID;

public class RequestIdUtil {
    public static final ThreadLocal<UUID> threadLocal = new ThreadLocal<>();

    public static UUID getRequestId(){
        UUID uuid = threadLocal.get();
        if(uuid == null) {
            uuid = UUID.randomUUID();
            threadLocal.set(uuid);
        }

        return uuid;
    }


    public static void removeRequestId(){
        threadLocal.remove();
    }
}
