package com.ardonplay.gachi_bot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity(name = "user")
@Data
public class User {
    @Id
    @Column(name = "id")
    private Long userID;

    @Column(name = "counter")
    private int counter;

    public User() {

    }
}
