package com.pocketbudget.repository;

import com.pocketbudget.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> getAllByUser_UUID(String UUID);

    Optional<Account> getAccountByUUIDAndUser_Username(String accountUUID, String userUUID);

    @Query("SELECT a FROM Account AS a JOIN FETCH a.records WHERE a.UUID = :accountUUID AND a.user.username = :userUUID")
    Optional<Account> getAccountWithRecordsByUUIDAndUser_Username(String accountUUID, String userUUID);

    int deleteAccountByUUIDAndUser_Username(String accountUUID, String username);
}
