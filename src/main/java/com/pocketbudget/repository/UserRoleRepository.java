package com.pocketbudget.repository;

import com.pocketbudget.model.entity.UserRole;
import com.pocketbudget.model.entity.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    UserRole getUserRoleByRole(UserRoleEnum userRoleEnum);
}
