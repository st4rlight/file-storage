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

    private T data;

    public static <T> RestResponse<T> of(T data){
        return new RestResponse<>(0, "ok", data);
    }

    public static RestResponse error(Exception ex){
        return new RestResponse(-1, ex.getMessage(), null);
    }
}
