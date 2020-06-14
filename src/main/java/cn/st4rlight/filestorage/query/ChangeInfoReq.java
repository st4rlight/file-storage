package cn.st4rlight.filestorage.query;

import cn.st4rlight.filestorage.domain.TimeUnit;
import lombok.Data;

@Data
public class ChangeInfoReq {
    private long uploadId;

    private int oldCode;

    private int newCode;

    private String password;

    private int time;

    private TimeUnit timeUnit;
}