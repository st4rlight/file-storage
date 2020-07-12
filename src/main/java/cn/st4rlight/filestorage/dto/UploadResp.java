package cn.st4rlight.filestorage.dto;

import cn.st4rlight.filestorage.domain.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResp {

    private long uploadId;

    private String extractCode;

    private String qrCode;

    private int time;

    private TimeUnit timeUnit;
}
