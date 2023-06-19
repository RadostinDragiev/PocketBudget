package com.pocketbudget.repository;

import com.pocketbudget.model.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, String> {
    List<Record> getAllByAccount_UUID(String accountUUID);
}
