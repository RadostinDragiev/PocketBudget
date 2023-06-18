package com.pocketbudget.web;

import com.pocketbudget.model.binding.UserRoleBindingModel;
import com.pocketbudget.model.entity.UserRole;
import com.pocketbudget.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/getUserRoles")
    private ResponseEntity<Set<UserRole>> getUserRoles(@PathVariable("id") String userUUID) {
        Set<UserRole> userRoles = this.userService.getUserRoles(userUUID);
        return ResponseEntity.ok(userRoles);
    }

    @PatchMapping("/{id}/changeRoles")
    public ResponseEntity<Void> changeUserRoles(@PathVariable("id") String userUUID,
                                                @RequestBody UserRoleBindingModel userRoleBindingModel) {
        boolean changeUserRoles = this.userService.updateUserRoles(userUUID, userRoleBindingModel.getUserRoles());
        return changeUserRoles ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }
}
