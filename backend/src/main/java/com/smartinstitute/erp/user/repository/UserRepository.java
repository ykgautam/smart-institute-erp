package com.smartinstitute.erp.user.repository;

import com.smartinstitute.erp.common.enums.UserStatus;
import com.smartinstitute.erp.user.entity.User;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    Optional<User> findByEmail(String email);

    @Query("""
       SELECT u
       FROM User u
       JOIN FETCH u.role
       WHERE u.email = :email
       """)
    Optional<User> findByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findByIdAndStatusNot(Long id, UserStatus status);

    List<User> findAll();

    Optional<User> findByEmailAndStatusNot(String email, UserStatus status);

    List<User> findByStatusNotOrderByCreatedAtDesc(UserStatus status);

    boolean existsByMobile(@Pattern(
            regexp = "^[0-9]{10}$",
            message = "Mobile must contain exactly 10 digits."
    ) String mobile);

//    List<User> findByStatusNot(UserStatus userStatus);

//    List<User> findByIdAndStatusNot(Long id, UserStatus userStatus);



}