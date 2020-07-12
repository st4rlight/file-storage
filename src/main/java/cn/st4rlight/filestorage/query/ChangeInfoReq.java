package cn.st4rlight.filestorage.query;

import cn.st4rlight.filestorage.domain.TimeUnit;
import lombok.Data;

@Data
public class ChangeInfoReq {
    private long uploadId;

    private String oldCode;

    private String newCode;

    private String password;

    private int time;

    private TimeUnit timeUnit;
}