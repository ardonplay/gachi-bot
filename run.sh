#!/bin/bash

poetry install
screen -d -m -S gachi_bot poetry run bot
