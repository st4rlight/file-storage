package cn.st4rlight.filestorage.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file_upload", indexes = {@Index(columnList = "extractCode")})
public class FileUpload implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long uploadId;

    @Column(nullable = false)
    private long extractCode;

    private String password;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] fileIds;

    private String fileName;

    @JsonFormat(pattern = "yyyy-MM-dd: HH:mm:ss")
    private LocalDateTime uploadTime;

    @JsonFormat(pattern = "yyyy-MM-dd: HH:mm:ss")
    private LocalDateTime expireTime;

    @Enumerated(value = EnumType.STRING)
    private Status status;
}
