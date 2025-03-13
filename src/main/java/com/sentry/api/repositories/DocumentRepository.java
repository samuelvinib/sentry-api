package com.sentry.api.repositories;

import com.sentry.api.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("SELECT SUM(d.size) FROM Document d")
    Long getTotalFileSize();

}
