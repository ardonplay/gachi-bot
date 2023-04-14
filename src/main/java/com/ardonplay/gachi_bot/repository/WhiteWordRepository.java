package com.ardonplay.gachi_bot.repository;

import com.ardonplay.gachi_bot.model.WhiteWord;
import java.util.List;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

@CacheConfig(cacheNames = "white_words")
public interface WhiteWordRepository extends JpaRepository<WhiteWord, Long> {
  @Cacheable("white_words")
  Boolean existsByWord(String word);


  @Override
  @Cacheable("white_words")
  List<WhiteWord> findAll();

  @Override
  @CachePut("white_words")
  <S extends WhiteWord> S save(S entity);
}
