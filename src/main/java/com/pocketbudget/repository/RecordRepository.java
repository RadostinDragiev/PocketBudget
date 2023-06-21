package com.pocketbudget.repository;

import com.pocketbudget.model.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<Record, String> {
    Optional<Record> getRecordByUUIDAndAccount_UUIDAndAccount_User_Username(String recordUUID, String accountUUID, String username);

    List<Record> getAllByAccount_UUIDAndAccount_User_UsernameOrderByCreatedDateTimeDesc(String accountUUID, String username);
}
