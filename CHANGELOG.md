# v1.9.0 - ???

### Features:
- Added 26.1.2 support
- Added a littlefoot alert (Haven't tested it, thats Narga's job)

### Changes:
- Added a stability check before resizing hotspots
- Updated flare radius and alerts logic (Timer only shows while in radius, alerts only on expiration while in radius)

### Fixes:
- Actually fixed hotspot particles being hid while highlight hotspot is off
- Fixed pet display not saving in between sessions
- Made mod sent messages not trigger event (Could cause an infinite loop that crashes game)
- Made deployable alert not trigger upon swapping islands
- Fixed the generated custom sound json not having the replace flag

### Back-end:
- Renamed `format` to `style` in sea creature configuration system
- Updated custom catch message templates to use the new `{style}` variable and included all available variables in the description
- Set bossbar to true for Thunder and Lord Jawbus in sea creature configuration
- Updated sea creature configurations for Abyssal Miner and Plhlegblast
- Refactored sea creatures system to be data-driven via `sc-config.json`
- Refactored party system to use Hypixel Mod Api for party tracking