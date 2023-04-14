package com.ardonplay.gachi_bot.repository;

import com.ardonplay.gachi_bot.model.User;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {
}
