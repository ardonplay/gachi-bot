package com.ardonplay.gachi_bot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "white_words")
@Setter
@Getter
@Table(name = "white_words")

public class WhiteWord {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "word")
  private  String word;
  public WhiteWord(String word){
    this.word = word;
  }


  public WhiteWord() {}
}
