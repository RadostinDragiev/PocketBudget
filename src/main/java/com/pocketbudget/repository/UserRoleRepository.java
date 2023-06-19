package com.pocketbudget.repository;

import com.pocketbudget.model.entity.UserRole;
import com.pocketbudget.model.entity.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    UserRole getUserRoleByRole(UserRoleEnum userRoleEnum);

    Set<UserRole> getUserRolesByRoleIn(Collection<UserRoleEnum> userRoleEnums);
}
