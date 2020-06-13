package cn.st4rlight.filestorage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResp {

    private int extractCode;

    private String qrCode;

    private int time;

    private TimeUnit timeUnit;

    public static enum TimeUnit {
        HOURS,
        DAYS
    }
}
