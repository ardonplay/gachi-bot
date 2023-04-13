package com.ardonplay.gachi_bot.repository;

import com.ardonplay.gachi_bot.model.BadWord;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface BadWordRepository extends CrudRepository<BadWord, Long> {
  Boolean existsByWord(String word);
}