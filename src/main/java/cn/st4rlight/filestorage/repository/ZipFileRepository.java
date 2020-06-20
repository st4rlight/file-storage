package cn.st4rlight.filestorage.repository;

import cn.st4rlight.filestorage.domain.Status;
import cn.st4rlight.filestorage.domain.ZipFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZipFileRepository extends JpaRepository<ZipFile, Long> {

    Optional<ZipFile> findByZipNameAndStatusNot(String name, Status status);
}
