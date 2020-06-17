package cn.st4rlight.filestorage.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<T> {

    private int code;
    private String message;
    private long timeCost;
    private String requestId;

    private T data;

    public RestResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> RestResponse<T> of(T data){
        return new RestResponse<>(0, "ok", data);
    }

    public static <T> RestResponse<T> error(Exception ex){
        return new RestResponse<>(-1, ex.getMessage(), null);
    }

    public static <T> RestResponse<T> ok(){
        return new RestResponse<>(0, "ok", null);
    }

    public static <T> RestResponse<T> errorMsg(int code, String msg){
        return new RestResponse<>(code, msg, null);
    }
}
