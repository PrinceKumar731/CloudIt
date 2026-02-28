package org.example.cloudit.repository;

import org.example.cloudit.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepo extends JpaRepository<Photo,String> {

    List<Photo> findAllByOrderByCreatedAtDesc();

    List<Photo> findAllByFolderNameOrderByCreatedAtDesc(String folderName);

    @Query("SELECT DISTINCT p.folderName FROM Photo p ORDER BY p.folderName ASC")
    List<String> findDistinctFolderNames();

    long countByFolderName(String folderName);

}

