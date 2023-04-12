package com.ardonplay.gachi_bot.repository;

import com.ardonplay.gachi_bot.model.Mat;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface MatRepository extends CrudRepository<Mat, Long> {
  Boolean existsByUserUserID(Long user_userID);

  List<Mat> findAllByUserUserID(Long user_userID);

}
