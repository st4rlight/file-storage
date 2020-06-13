package cn.st4rlight.filestorage.repository;

import cn.st4rlight.filestorage.domain.FileUpload;
import cn.st4rlight.filestorage.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    boolean existsByExtractCodeAndStatusIsNot(int code, Status status);

    Optional<FileUpload> findByExtractCodeAndStatusNot(int code, Status status);
}
