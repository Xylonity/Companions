# 1.3.3
- Minimum required version of knightlib increased to 1.6.2
- Fixed demon flesh appearing in every entity loot table in JER
- 
# 1.3.2
- Fixed companion attack logic receiving infinite cooldowns, which prevented several companions from dealing damage and Cornelius from completing its taming behavior
- Fixed hostile imps not pursuing their current targets, as well as Dinamo hitting duplicate targets
- Fixed puppet cannon stakes spawning and flying lower than intended

# 1.3.1
- Imp tents now have 30% more chance of spawning
- Fixed a case where the teammate validation could generate a recursive loop that ended up in a server kick
- Fixed a case where the teddy could trigger a crash caused by its own logic discriminators
- Fixed newly generated imp tents being placed on illegal places
- Attempt to fix a case where the ice shard projectile would crash the server caused by an incorrect itemstack serialization
- Fixed companion attack goals receiving effectively infinite cooldowns, which prevented several companions from dealing damage and Cornelius from completing its taming behavior
- Fixed Dinamo and hostile imps not pursuing their current targets, as well as Dinamo hitting duplicate or out-of-range targets
- Fixed puppet cannon stakes spawning and flying lower than intended

 # 1.3.0
- Added a new companion, the shade bat. The default version consists of some small bats that do basic damage, while the blood version is a big bat that makes tackles (and applies poison) to the current target.
- Added a new teddy variant, the holy teddy. It is activated by interacting with a normal teddy using a porcelain pottery. Interacting with the teddy while holding an item with the curse of vanishing enchantment would eliminate it
- Added 2 new items (placeable), porcelain pottery and holy porcelain pottery. The former is used to transform the teddy in its holy variant, while the latter is used to contain entities and allows to move them freely
- The Tesla Manager, which handles connection logic on the Tesla Network, has been rewritten, potentially fixing Tesla connection and desynchronization issues on dedicated servers
- Cornelius blackjack game is now available for all players in the server, not only the owner, and the game is now played sequentially per active player
- Now the Sacred Pontiff can have its name translated into different languages instead of being exclusively in English (previously limited by the bossbar system)
- The bonanza's 2 skull drop anvil feels special today, don't you dare annoy it
- Armor effects now apply to any entity wearing the armor set, not just players
- The force with which certain camera shakes were performed was modified to make it more natural
- A new JEI category has been added for the frog bonanza, indicating which items are enabled to be used as currency in the machine
- Now reskins for entities are only activated when the entity has a nametag with a specific name, not just by having a nametag as before
- Added 2 missing camera shakes, at the beginning and end of His Holiness appearance animation
- Added loot to chests and barrels in the mod's structures. Loot tables and the percentage of containers that are filled are configurable via the config file
- Certain projectiles in magic books have been changed to deal vanilla indirect magic damage instead
- In Fabric, the respawn totem can now respawn any tamable entity, not just companions
- The code has been partially rewritten to ensure loader parity
- Increased mutated teddy attack range
- The plasma lamp now lits when active
- Enhanced the visuals for the holiness star
- Now the soul mage casts the black hole with greater accuracy towards his target, so that it is less likely to miss
- The soul mage's healing ring spell has been given priority, as it wasn't cast sometimes
- Certain armor pieces are no longer trimmable
- Shade Maw now generates a shockwave when it hits the ground after a jump. The jump now has a cooldown
- Enhanced the movement of the Shade Maw, to make it less rough
- Now the Shade Maw rotates slightly up or down depending on the direction of movement
- Deleted croissant dragon spawn egg, which was obtainable through commands
- The needle recipe now requires antlion fur, and the voltaic relay now requires a tesla coil
- Now antlions are categorized as arthropods
- Now the croissant dragon armor can be swapped
- Reworked bonanza drops, making it more difficult to keep playing
- Enhanced the visuals of the eternal fire
- Added compatibility with knightlib 1.6.0
- Added config options to specify names under which each entity would activate its reskin
- Added a config option to modify companions! structures biome and biome tag worldgen generation
- Added a config option to modify the attack rate for companions (potentially making them attack more often or less often)
- Added a config option that lets override armor point values for companions
- Added a config option that lets specify if companions can actively attack nearby hostile mobs without the owner having to attack first
- Added a config option to modify bonanza drops on the 2 or 3 coin head rolls
- Added a config option to prevent mob griefing (when using certain magic books)
- Added a config option to specify the chance of a container being filled with loot in a structure
- Added a config option to specify the loot tables that structures will use to fill their chests and barrels
- Added a config option to allow the enchantments on the respective armor piece to persist when crafting mage, holy robe, or crystallized blood armor sets
- Fixed companions not being detected by mods that display entity loot tables, due to a lack of loot table definitions
- Fixed a bug where the blood scythe wouldn't heal the player on hit
- Fixed a case where the soul mage living candle wouldn't work
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
- Fixed the teddy needle hit sound taking a long time to sound
- Fixed a case where companions spawned inside structures wouldn't see their base health modified even though the max health value was changed through the config file
- Fixed crystallized blood axe using a wrong on-ground scale
- Fixed broken block renderers on newer geckolib versions

# 1.2.4
- Added a missing flag that made the great chalice uncraftable

# 1.2.3
- Added compatibility with Knightlib 1.5.0
- Fixed an issue in Fabric where the eternal fire would not be transparent and would have a black texture instead
- Preparing the ground for the major update 1.3.0

# 1.2.2
- Attempt to fix recall platform loop crash
- Black hole is now summonable

# 1.2.1
- Fixed armor set durabilities defaulted to 35 instead of their real values
- Buffed holy robe and crystallized blood armor sets defense values

# 1.2.0
- Fixed non-capitalized item names
- Fixed recall platforms working without electricity
- Fixed shade sword altar item icon facing the wrong direction
- Fixed respawn totem not spawning neither mankh nor cloak
- Fixed mankh and cloak spawning not consuming their respective item
- Fixed raid crashes caused by the illager golem
- Sacred Pontiff is now respawnable by interacting on a respawn totem with a nether star
- Increased Sacred Pontiff randomized loot amount
- Now the illager golem spawns once per even raid wave
- Bonanza currency is now configurable through the config file
- Deleted BONANZA_COIN_TRIES config entry
