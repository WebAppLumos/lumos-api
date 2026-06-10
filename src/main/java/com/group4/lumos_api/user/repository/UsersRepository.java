package com.group4.lumos_api.user.repository;

import com.group4.lumos_api.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
    boolean existsByStudentNumber(String studentNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}