# Description
- This document describe how to use MiniGameWorld pluin in your server
- Plugin works on `spigot`, `paper` bukkit with `1.14+` versions
- MiniGame types: `Solo`, `SoloBattle`, `Team`, `TeamBattle` and `custom`



# Features
- Module-based (`MiniGameWorld`, `each minigames mady by various dev`, `minigame maps`, `minigame configs` and `3rd-party` work as modules)
- Same minigame can be played in many different ways with custom config settings, maps made by users
- A player's `Inventory`, `Health`, `Food level`, `Exp`, `Potion Effects`, `Glowing`, `Hiding`, `Game Mode` and etc are saved at the game start and restored when the game finished
- GUI menu
- Party system
- View system
- Scoreboard system
- Everything can be customized with config
- Exists 3rd-party plugins (e.g. [MiniGameWorld-Reward], [MiniGameWorld-Rank])
- Auto data backup
- Update checker
- Language support system


# How to apply
1. Download [MiniGameWorld] and [wbmMC]
2. Download [MiniGames] that you want to add to your server
3. Put `MiniGameWorld`, `wbmMC` and `minigame plugins` in server `plugins` folder
4. Make a place for minigame yourself
5. Update minigame location in `plugins/MiniGameWorld/minigames/<class-name>.yml` file and run command `/minigame reload` (need OP)  
**`or`**  
Update minigame location using command `/minigame minigames <class-name> location <<player> | <x> <y> <z>>` (need OP)  



# Download
- [MiniGameWorld]: MiniGameWorld Framework
- [wbmMC]: Essential library
- [MiniGames]: MiniGames made by some makers
- [Third-Parties]: You can `give reward with rank `, `save rank data` and etc with third party plugins



# Youtube Tutorial
<a href="https://www.youtube.com/watch?v=sE0vaj0xM8Q">
<img src="youtube-user-tutorial-thumbnail.png" width="50%" ></img>
</a>



# More
- [Join / View / Leave]
- [Commands]
- [Config]
- [Permissions]
- [Party]
- [Backup]
- [View]
- [Menu]
- [Scoreboard]
- [Language Support]

# Caution
- If you `stop` or `reload` your server without player's command or server command, must make sure that all players are not joining any minigames
- If you have some problems in bungeecord server, use [BungeeGuard](https://www.spigotmc.org/resources/bungeeguard.79601/) plugin



[MiniGameWorld]: https://github.com/MiniGameWorlds/MiniGameWorld/releases
[wbmMC]: https://github.com/worldbiomusic/wbmMC/releases
[MiniGames]: https://github.com/MiniGameWorlds/AllMiniGames
[Commands]: commands.md
[Third-Parties]: https://github.com/MiniGameWorlds
[Join / View / Leave]: how-to-join-leave.md
[Youtube: User Tutorial]: https://youtu.be/sE0vaj0xM8Q
[Config]: config.md
[Permissions]: permissions.md
[Party]: party.md
[Backup]: backup.md
[View]: view.md
[Menu]: menu.md
[Scoreboard]: scoreboard.md
[Language Support]: language-support.md
[MiniGameWorld-Reward]: https://github.com/MiniGameWorlds/MiniGameWorld-Reward
[MiniGameWorld-Rank]: https://github.com/MiniGameWorlds/MiniGameWorld-Rank
