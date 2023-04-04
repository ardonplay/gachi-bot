import os
from datetime import datetime, timedelta

import telebot

from gachi_bot.bad_words import bad_words


class Bot:

    def __init__(self, token):
        self.bot = telebot.TeleBot(token)

        self.bad_words = bad_words

        self.users = {}

        self.restrict_time = 1

    def check_message(self, message):
        global chat_owner_id
        user_id = message.from_user.id
        if user_id not in self.users:
            self.users[user_id] = 0

        for i in self.bad_words:
            if i in message.text.lower():
                if self.users[user_id] < 4:
                    self.bot.reply_to(message, "🤡")
                    self.users[user_id] += 1
                elif self.users[user_id] < 6:
                    with open(os.path.realpath(os.path.dirname(__file__))+ "/assets/sticker.gif", "rb") as animation_file:
                        self.bot.send_animation(message.chat.id, animation_file, reply_to_message_id=message.message_id)
                    self.bot.send_message(message.chat.id, "За такие слова я тебя сейчас в бан кину")
                    self.users[user_id] += 1
                else:
                    restrict_until = datetime.now() + timedelta(minutes=self.restrict_time)

                    chat_administrators = self.bot.get_chat_administrators(message.chat.id)

                    for chat_member in chat_administrators:
                        if chat_member.status == "creator":
                            chat_owner_id = chat_member.user.id
                            break
                    if chat_owner_id == user_id:
                        self.bot.reply_to(message, "Господин, давайте не будем сквернословить?")
                        self.users[user_id] = 0
                        break
                    self.bot.reply_to(message, "Ну ты дописался, посиди в бане 30 минут")

                    chat_permissions = telebot.types.ChatPermissions()

                    chat_permissions.can_send_messages = False

                    self.bot.restrict_chat_member(message.chat.id, user_id,
                                                  until_date=int(restrict_until.timestamp()),
                                                  permissions=chat_permissions)
                    self.users[user_id] = 0
                break

    def run(self):
        @self.bot.message_handler(content_types=['new_chat_members'])
        def handler_start(message):
            if message.chat.type == 'group' or message.chat.type == 'supergroup':
                for user in message.new_chat_members:
                    if user.id == self.bot.get_me().id:
                        self.bot.send_message(message.chat.id,
                                              "Ну привет мои маленькие slaves, зовите меня своим dungeon masterом"
                                              ", я вам не дам"
                                              " повода материться, а если вы будете это делать - придется сделать "
                                              "fisting ass...")

        @self.bot.message_handler(func=lambda message: True)
        def check_all(message):
            self.check_message(message)

        self.bot.polling()


def app():
    bot = Bot("6212803918:AAGGTMFH000iKk3LY6fCX0mnCbD3YlT6b-8")
    bot.run()


if __name__ == '__main__':
    app()
