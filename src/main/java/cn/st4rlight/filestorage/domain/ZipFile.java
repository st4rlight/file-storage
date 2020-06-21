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
@Table(name = "multi_file", indexes = {@Index(columnList = "zipName")})
public class ZipFile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long zipId;

    @Column(nullable = false)
    private String zipName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private int fileSize;

    @JsonFormat(pattern = "yyyy-MM-dd: HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd: HH:mm:ss")
    private LocalDateTime expireTime;

    @Enumerated(value = EnumType.STRING)
    private Status status;
}
