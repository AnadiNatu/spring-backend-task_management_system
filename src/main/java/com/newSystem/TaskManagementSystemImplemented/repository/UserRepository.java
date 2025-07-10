package com.newSystem.TaskManagementSystemImplemented.repository;


import com.newSystem.TaskManagementSystemImplemented.entity.Users;
import com.newSystem.TaskManagementSystemImplemented.enums.UserRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {

    @Query("SELECT u FROM Users u WHERE LOWER(u.name) = LOWER(:name)")
    Optional<Users> findUserByNameContaining(@Param("name") String name);
    @Query("SELECT u FROM Users u WHERE LOWER(u.username) = LOWER(:username)")
    Optional<Users> findUserByUsername(@Param("username") String username);
    @Query("SELECT u FROM Users u WHERE u.userRoles = :userRoles AND LOWER(u.name) = LOWER(:name)")
    Optional<Users> findUserByNameAndRoles(@Param("userRoles") UserRoles userRoles , @Param("name") String name);
    @Query("SELECT u FROM Users u WHERE u.userRoles = :userRoles")
    Optional<Users> findByUserRoles(@Param("userRoles") UserRoles  userRoles);

    @Query("SELECT u FROM Users u WHERE u.userRoles = :userRoles")
    List<Users> findAllByUserRoles(@Param("userRoles") UserRoles  userRoles);

}