package com.ardonplay.gachi_bot.service.BotServices;

import com.ardonplay.gachi_bot.model.Mat;
import com.ardonplay.gachi_bot.model.User;
import com.ardonplay.gachi_bot.service.GachiBot;

import java.util.List;
import java.util.Objects;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class DbController {

    final private GachiBot bot;

    public DbController(GachiBot bot) {
        this.bot = bot;
    }

    private boolean isTheSame(Mat first, Mat second) {
        return (first.getCount() == second.getCount())
                && (Objects.equals(first.getWord(), second.getWord())
                && (Objects.equals(first.getUser().getUserID(), second.getUser().getUserID())));
    }

    private void checkIdentity(List<Mat> first, List<Mat> second) {
        for (Mat ignored : first) {
            for (Mat igogo : second) {
                if (isTheSame(ignored, igogo)) {
                    if(first.size()-1 == 0){
                        first.remove(ignored);
                        break;
                    }
                }
            }
        }
    }

    public void saveUsers(){
        List<User> userList = bot.getUsers().values().stream().toList();

        System.out.println(userList);
        userList.forEach(user -> {
            if (user.getMats() != null) {
                user.getMats().forEach(mat -> mat.setUser(user));
            }
        });

        List<Mat> mats = (List<Mat>) bot.getMatRepository().findAll();

        if (!userList.equals(bot.getUserRepository().findAll())) {
            for(User user: userList){
              checkIdentity(user.getMats(), mats);
            }
            try {
                bot.getUserRepository().saveAll(userList);
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
            }
        }
    }
}

