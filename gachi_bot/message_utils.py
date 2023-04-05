import os

from gachi_bot.user import User


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
