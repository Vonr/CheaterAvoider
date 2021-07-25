# CheaterAvoider
Minecraft 1.8.9 mod to report cheaters (and anyone else) you face, adding them to a list in your Minecraft installation folder and warns in chat you should you face them.

Spiritual successor to [@leduyquang753](https://github.com/leduyquang753)'s [NoCheaters](https://hypixel.net/threads/forge-1-8-nocheaters-%E2%80%93-avoid-cheaters-in-game.2317497/) mod

Saves reported player list to <minecraft_install_folder>/uuidreports.txt and namereports.txt

## Commands:
- /creport \<playerName> \<reasons...>: Reports a player using their UUID so that they will keep being in your list even if they change their name
- /ccreport \<playerName> \<reasons...>: Reports a player using their name, usually for offline (cracked) servers
- /cunreport \<playerName>: Removes a player that was reported using /creport
- /ccunreport \<playerName>: Removes a player that was reported using /ccreport
- /creportlist: Lists all players reported with /creport
- /ccreportlist: Lists all players reported with /ccreport
