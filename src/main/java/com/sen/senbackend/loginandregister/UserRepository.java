package com.sen.senbackend.loginandregister;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.login = ?1")
    Optional<User> findByLogin(String login);


    @Transactional
    @Modifying
    @Query("update User u set u.login = ?1, u.password = ?2 where u.id = ?3")
    void updateLoginAndPasswordById(String login, String password, Long id);

    @Transactional
    @Modifying
    @Query("delete from User u where u.login = ?1")
    void deleteByLogin(String login);
}