package com.ardonplay.gachi_bot.repository;

import com.ardonplay.gachi_bot.model.Mat;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MatRepository extends JpaRepository<Mat, Long> {
  Boolean existsByUserUserID(Long user_userID);

  List<Mat> findAllByUserUserID(Long user_userID);

}
