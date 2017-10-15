# amybot backend

It processes events and stuff yo.  

## Configuration

```Bash
BOT_TOKEN="your token here"
REDIS_HOST="redis://redis:6379"
REDIS_PASS="whatever"
COMMAND_PREFIX="amy!"
```

## JDA is a dependency why can't I use JDA objects!??!??!?!

JDA is used for REST requests ***only***. None of the cache is present, there is no websocket connection, ... Basically you're on your own. While there is some effort put into building an external cache, it's by no means the "best" thing ever or any such thing. 