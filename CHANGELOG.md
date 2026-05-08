# 1.3.0
- The Tesla Manager, which handles connection logic on the Tesla Network, has been rewritten, potentially fixing Tesla connection and desynchronization issues on dedicated servers
- Now the Sacred Pontiff can have its name translated into different languages instead of being exclusively in English (previously limited by the bossbar system)
- Armor effects now apply to any entity wearing the armor set, not just players
- The force with which certain camera shakes were performed was modified to make it more natural
- Added 2 missing camera shakes, at the beginning and end of His Holiness appearance animation
- Added a config option to modify bonanza drops on the 2 or 3 coin head rolls
- Added a config option to prevent mob griefing (when using certain magic books)
- Added a config option to allow the enchantments on the respective armor piece to persist when crafting mage, holy robe, or crystallized blood armor sets
- In Fabric, the respawn totem can now respawn any tamable entity, not just companions
- The code has been rewritten to ensure loader parity
- Added compatibility with knightlib 1.5.0
- Fixed companions not being detected by mods that display entity loot tables, due to a lack of loot table definitions
- Fixed a bug where the hostile imp was not dropping demon flesh in Fabric

# 1.2.2
- Attempt to fix recall platform loop crash
- Black hole is now summonable

# 1.2.1
- Buffed holy robe and crystallized blood armor sets defense values

# 1.2.0
- Fixed non-capitalized item names
- Fixed recall platforms working without electricity
- Fixed shade sword altar item icon facing the wrong direction
- Fixed mankh and cloak spawning not consuming their respective item
- Fixed raid crashes caused by the illager golem
- Sacred Pontiff is now respawnable by interacting on a respawn totem with a nether star
- Increased Sacred Pontiff randomized loot amount
- Now the illager golem spawns once per even raid wave
- Bonanza currency is now configurable through the config file
- Deleted BONANZA_COIN_TRIES config entry

# 1.1.2
- Added compat with knightlib 1.4.0