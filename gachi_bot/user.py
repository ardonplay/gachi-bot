class User:
    def __init__(self, counter, stat: dict, user_id):
        self.user_id = user_id
        self.counter = counter
        self.stat = stat

    def __json__(self) -> dict:
        return {
            'user_id': self.user_id,
            'counter': self.counter,
            'stat': self.stat
        }
