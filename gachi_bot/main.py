import os
from datetime import datetime, timedelta
from typing import Optional, List

import schedule
import telebot
import asyncio
from telebot.async_telebot import AsyncTeleBot

from gachi_bot.bad_words import bad_words
from gachi_bot.stat_loader import stat_loader, stat_saver
from gachi_bot.user import User


class Bot(AsyncTeleBot):

    def __init__(self, token):
        super().__init__(token)

        self.bad_words = bad_words

        self.users = stat_loader()

        self.restrict_time = 1

    async def message_finder(self, message, text):
        user_id = message.from_user.id
        if user_id not in self.users:
            self.users[user_id] = User(user_id=user_id, counter=0, stat={})
        for i in self.bad_words:
            if i in text:
                if i in self.users[user_id].stat:
                    self.users[user_id].stat[i] += 1
                elif i not in self.users[user_id].stat:
                    self.users[user_id].stat[i] = 1
                if self.users[user_id].counter < 2:
                    await self.reply_to(message, "🤡")
                    self.users[user_id].counter += 1
                elif self.users[user_id].counter < 3:
                    with open(os.path.realpath(os.path.dirname(__file__)) + "/assets/sticker.gif",
                              "rb") as animation_file:
                        await self.send_animation(message.chat.id, animation_file,
                                                  reply_to_message_id=message.message_id)
                    await self.send_message(message.chat.id, "За такие слова я тебя сейчас в бан кину")
                    self.users[user_id].counter += 1
                else:
                    await self.ban(message)
                break

    async def ban(self, message) -> None:
        user_id = message.from_user.id
        restrict_until = datetime.now() + timedelta(minutes=self.restrict_time)

        chat_administrators = await self.get_chat_administrators(message.chat.id)

        for administrator in chat_administrators:
            if administrator.user.id == user_id:
                await self.reply_to(message, "Товарищ админ, давайте не будем сквернословить?")
                self.users[user_id].counter = 0
                return

        await self.reply_to(message, "Ну ты дописался, посиди в бане минутку")

        chat_permissions = telebot.types.ChatPermissions()

        chat_permissions.can_send_messages = False

        try:
            await self.restrict_chat_member(message.chat.id, user_id,
                                            until_date=int(restrict_until.timestamp()),
                                            permissions=chat_permissions)
        except Exception:
            print("Ну, не смог!")
        self.users[user_id].counter = 0

    async def check_message(self, message):
        text = message.text.lower()
        await self.message_finder(message, text)

    async def handler_start(self, message):
        if message.chat.type == 'group' or message.chat.type == 'supergroup':
            for user in message.new_chat_members:
                if user.id == (await self.get_me()).id:
                    await self.send_message(message.chat.id,
                                            "Ну привет мои маленькие slaves, зовите меня своим dungeon masterом"
                                            ", я вам не дам"
                                            " повода материться, а если вы будете это делать - придется сделать "
                                            "fisting ass...")

    async def get_user_stat(self, message):
        if message.from_user.id not in self.users:
            self.users[message.from_user.id] = User(user_id=message.from_user.id, stat={}, counter=0)
        if len(self.users[message.from_user.id].stat) != 0:
            await self.reply_to(message, self.users[message.from_user.id].stat)
        else:
            await self.reply_to(message, "А вы молодец, пока еще ничего!")

    async def save(self):
        print("типа сохранил")
        if len(self.users) != 0:
            await stat_saver(self.users)

    async def run(self):

        @self.message_handler(commands=['статистика'])
        async def stat_handler(message):
            await self.get_user_stat(message)

        @self.message_handler(content_types=['new_chat_members'])
        async def on_new_chat_members(message):
            await self.handler_start(message)

        @self.message_handler(content_types=['photo'])
        async def photo_handler(message):
            await self.message_finder(message, message.caption)

        @self.message_handler(func=lambda message: True)
        async def check_all(message):
            await self.check_message(message)

        await self.polling()
        await self.save()


def app():
    bot = Bot("6212803918:AAGGTMFH000iKk3LY6fCX0mnCbD3YlT6b-8")
    loop = asyncio.get_event_loop()
    loop.run_until_complete(bot.run())


if __name__ == '__main__':
    app()
