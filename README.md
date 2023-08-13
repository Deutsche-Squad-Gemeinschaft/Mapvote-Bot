# Mapvote-Bot
A Discord bot which organizes mapvotes for the DSG Squad Gameserver

**This bot uses information provided by [Battlemetrics](https://battlemetrics.com)**

## Installation

The bot is provided as a Docker image on the GitHub container registry. You can use the image directly throught the command line or use an docker-compose.yml to save your configuration as test-file.

Docker
```
docker run \
  -e DISCORD_TOKEN=${DISCORD_TOKEN} \
  -e BATTLEMETRICS_TOKEN=${BATTLEMETRICS_TOKEN} \
  ghcr.io//Deutsche-Squad-Gemeinschaft/mapvote-bot/app:latest 
```

Docker Compose
```
version: '3'
services:
  bot:
    image: ghcr.io//Deutsche-Squad-Gemeinschaft/mapvote-bot/app:latest
    environment:
      DISCORD_TOKEN: '${DISCORD_TOKEN}'
      BATTLEMETRICS_TOKEN: '${BATTLEMETRICS_TOKEN}'
```

## Contribute
TODO
