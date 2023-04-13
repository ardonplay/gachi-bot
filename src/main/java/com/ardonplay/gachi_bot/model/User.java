package com.ardonplay.gachi_bot.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "user")
@Getter
@Setter
public class User {
    @Id
    @Column(name = "id")
    private Long userID;

    @Column(name = "counter")
    private int counter;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Mat> mats;

    public User() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return counter == user.counter && Objects.equals(userID, user.userID)
            && Objects.equals(mats, user.mats);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID, counter, mats);
    }
}
