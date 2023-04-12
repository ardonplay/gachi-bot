package com.ardonplay.gachi_bot.repository;

import com.ardonplay.gachi_bot.model.User;

import org.springframework.data.repository.CrudRepository;
public interface UserRepository extends CrudRepository<User, Long> {
}
