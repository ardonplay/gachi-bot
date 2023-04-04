import os
from datetime import datetime, timedelta

import telebot
import asyncio
from telebot.async_telebot import AsyncTeleBot

from gachi_bot.bad_words import bad_words


class Bot(AsyncTeleBot):

    def __init__(self, token):
        super().__init__(token)

        self.bad_words = bad_words

        self.users = {}

        self.restrict_time = 1

    async def check_message(self, message):
        global chat_owner_id
        user_id = message.from_user.id
        if user_id not in self.users:
            self.users[user_id] = 0

        for i in self.bad_words:
            if i in message.text.lower():
                if self.users[user_id] < 2:
                    await self.reply_to(message, "🤡")
                    self.users[user_id] += 1
                elif self.users[user_id] < 3:
                    with open(os.path.realpath(os.path.dirname(__file__)) + "/assets/sticker.gif",
                              "rb") as animation_file:
                        await self.send_animation(message.chat.id, animation_file,
                                                  reply_to_message_id=message.message_id)
                    await self.send_message(message.chat.id, "За такие слова я тебя сейчас в бан кину")
                    self.users[user_id] += 1
                else:
                    restrict_until = datetime.now() + timedelta(minutes=self.restrict_time)

                    chat_administrators = await self.get_chat_administrators(message.chat.id)

                    for chat_member in chat_administrators:
                        if chat_member.status == "creator":
                            chat_owner_id = chat_member.user.id
                            break

                    if chat_owner_id == user_id:
                        await self.reply_to(message, "Господин, давайте не будем сквернословить?")
                        self.users[user_id] = 0
                        break

                    await self.reply_to(message, "Ну ты дописался, посиди в бане минутку")

                    chat_permissions = telebot.types.ChatPermissions()

                    chat_permissions.can_send_messages = False

                    try:
                        await self.restrict_chat_member(message.chat.id, user_id,
                                                        until_date=int(restrict_until.timestamp()),
                                                        permissions=chat_permissions)
                    except Exception:
                        print("Ну, не смог!")
                    self.users[user_id] = 0
                break

    async def handler_start(self, message):
        if message.chat.type == 'group' or message.chat.type == 'supergroup':
            for user in message.new_chat_members:
                if user.id == (await self.get_me()).id:
                    await self.send_message(message.chat.id,
                                            "Ну привет мои маленькие slaves, зовите меня своим dungeon masterом"
                                            ", я вам не дам"
                                            " повода материться, а если вы будете это делать - придется сделать "
                                            "fisting ass...")

    async def check_all(self, message):
        await self.check_message(message)

    async def run(self):
        @self.message_handler(content_types=['new_chat_members'])
        async def on_new_chat_members(message):
            await self.handler_start(message)

        @self.message_handler(func=lambda message: True)
        async def check_all(message):
            await self.check_message(message)

        await self.polling()


def app():
    bot = Bot("6212803918:AAGGTMFH000iKk3LY6fCX0mnCbD3YlT6b-8")
    loop = asyncio.get_event_loop()
    loop.run_until_complete(bot.run())


if __name__ == '__main__':
    app()
