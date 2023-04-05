import json
import os

from gachi_bot.user import User


def stat_loader() -> dict:
    users_output = {}
    with open(os.path.realpath(os.path.dirname(__file__)) + '/users.json', 'r') as f:
        data = json.load(f)

    for user in data["users"]:
        users_output[user["user_id"]] = User(counter=user["counter"], stat=user["stat"], user_id=user["user_id"])

    return users_output


def stat_saver(users_dict: dict):
    users_output = []
    for user in users_dict:
        users_output.append(users_dict[user].__json__())
    with open(os.path.realpath(os.path.dirname(__file__)) + '/users.json', 'w') as f:
        json.dump({"users": users_output}, f, indent=4, ensure_ascii=False)


if __name__ == '__main__':
    users = stat_loader()
    stat_saver(users)
