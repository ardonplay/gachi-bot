package com.ardonplay.gachi_bot.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "mats")
@Getter
@Setter
public class Mat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "word")
  private String word;

  @Column(name = "count")
  private int count;

  @ManyToOne(fetch = FetchType.EAGER,  cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  public Mat(){}

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Mat mat = (Mat) o;
    return count == mat.count && Objects.equals(word, mat.word)
        && Objects.equals(user, mat.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(word, count, user);
  }
}
