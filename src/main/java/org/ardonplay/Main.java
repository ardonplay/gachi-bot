package org.ardonplay;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
  public static void main(String[] args)
      throws TelegramApiException{



    TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

    try {
      botsApi.registerBot(new Bot("6212803918:AAGGTMFH000iKk3LY6fCX0mnCbD3YlT6b-8"));
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }


  }
}