package com.ardonplay.gachi_bot.repository;

import com.ardonplay.gachi_bot.model.WhiteWord;
import org.springframework.data.repository.CrudRepository;

public interface WhiteWordRepository extends CrudRepository<WhiteWord, Long> {
  Boolean existsByWord(String word);

}
