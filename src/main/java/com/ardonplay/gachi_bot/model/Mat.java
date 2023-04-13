package com.ardonplay.gachi_bot.model;

import jakarta.persistence.*;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "mats")
@Getter
@Setter
@Table(uniqueConstraints={@UniqueConstraint(name = "unique_email_per_username", columnNames={"user_id", "word"})})
public class Mat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @Column(name = "word")
  private String word;

  @Column(name = "count")
  private int count;

  @ManyToOne(fetch = FetchType.EAGER,  cascade = CascadeType.MERGE)
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
