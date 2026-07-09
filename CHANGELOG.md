# 1.3.0
- The Tesla Manager, which handles connection logic on the Tesla Network, has been rewritten, potentially fixing Tesla connection and desynchronization issues on dedicated servers
- Now the Sacred Pontiff can have its name translated into different languages instead of being exclusively in English (previously limited by the bossbar system)
- Armor effects now apply to any entity wearing the armor set, not just players
- The force with which certain camera shakes were performed was modified to make it more natural
- A new JEI category has been added for the frog bonanza, indicating which items are enabled to be used as currency in the machine
- Now reskins for entities are only activated when the entity has a nametag with a specific name, not just by having a nametag as before
- Added 2 missing camera shakes, at the beginning and end of His Holiness appearance animation
- Added loot to chests and barrels in the mod's structures. Loot tables and the percentage of containers that are filled are configurable via the config file
- Certain projectiles in magic books have been changed to deal vanilla magic damage instead of other type of damage
- In Fabric, the respawn totem can now respawn any tamable entity, not just companions
- The code has been partially rewritten to ensure loader parity
- Increased mutated teddy attack range
- Now the soul mage casts the black hole with greater accuracy towards his target, so that it is less likely to miss
- The soul mage's healing ring spell has been given priority, as it wasn't cast sometimes
- Certain armor pieces are no longer trimmable
- Shade Maw now generates a shockwave when it hits the ground after a jump. The jump now has a cooldown
- Enhanced the movement of the Shade Maw, to make it less rough
- Now the Shade Maw rotates slightly up or down depending on the direction of movement
- Deleted croissant dragon spawn egg, which was obtainable through commands
- The needle recipe now requires antlion fur
- Now antlions are categorized as arthropods
- Now the croissant dragon armor can be swapped
- Added compatibility with knightlib 1.6.0
- Added config options to specify names under which each entity would activate its reskin
- Added a config option to modify bonanza drops on the 2 or 3 coin head rolls
- Added a config option to prevent mob griefing (when using certain magic books)
- Added a config option to specify the chance of a container being filled with loot in a structure
- Added a config option to specify the loot tables that structures will use to fill their chests and barrels
- Added a config option to allow the enchantments on the respective armor piece to persist when crafting mage, holy robe, or crystallized blood armor sets
- Fixed companions not being detected by mods that display entity loot tables, due to a lack of loot table definitions
- Fixed a bug where the blood scythe wouldn't heal the player on hit
- Fixed a case where a max stack size lower than 64 would make the "Dinamo" impossible to tame
- Fixed a bug where the hostile imp was not dropping demon flesh in Fabric
- Fixed a potential sync issue that occurred when spawning charged creepers with the frog bonanza
- Fixed the puppet using incorrect logic, following the owner indefinitely even when instructed to do something else
- Fixed an issue where the dynamo would harm its owner's teammates, resolving problems with FTB Teams
- Fixed a case where running while riding the Shade Maw would spam its step sound constantly
- Fixed magic ray magic book not activating revenge logic on some entities
- Fixed a case where the wild cornelius would keep its chunk loaded, keeping some persistent server lag (fix applied into existing worlds too)
- Fixed block projections in the inventory and on the ground, which would have incorrect positions and rotations, or would extend beyond the inventory by overlapping other slots
- Fixed antlion base health not being set by the actual config entry 

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