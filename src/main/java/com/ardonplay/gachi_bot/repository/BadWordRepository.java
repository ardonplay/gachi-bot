package com.ardonplay.gachi_bot.repository;

import com.ardonplay.gachi_bot.model.BadWord;
import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

@CacheConfig(cacheNames = "bad_words")
public interface BadWordRepository extends JpaRepository<BadWord, Long> {
  Boolean existsByWord(String word);
  @Override
  @Cacheable("bad_words")
  List<BadWord> findAll();

  @Override
  @CachePut("bad_words")
  <S extends BadWord> S save(S entity);
}