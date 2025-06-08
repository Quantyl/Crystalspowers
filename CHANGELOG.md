# Changelog

All notable changes to the Crystal Powers plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.1.0] - 2025-06-08

### Added

#### Core Plugin Features
- **Complete plugin rebranding** from "Origins" to "Crystal Powers"
- **Crystal Power system** with 6 unique powers: Human, Avian, Enderian, Arachnid, Merling, and Phantom
- **Immersive chest-based GUI** for crystal power selection with mystical theming
- **Persistent player data storage** with YAML configuration
- **Command system** with intuitive aliases and tab completion

#### Crystal Powers
- **Human**: Balanced baseline power with no special abilities or weaknesses
- **Avian**: Flight ability, fall damage immunity, reduced health (8 hearts), weakness to projectiles
- **Enderian**: Enhanced teleportation with ender pearls, night vision, water/rain damage vulnerability
- **Arachnid**: Wall climbing when sneaking, night vision, poison immunity, reduced health (8 hearts)
- **Merling**: Water breathing, faster swimming, underwater night vision, slower land movement
- **Phantom**: Invisibility toggle (sneak + right-click), fall damage immunity, sunlight vulnerability, reduced health (7 hearts)

#### Advanced Abilities Implementation
- **Flight system** with proper game mode checks and persistence
- **Teleportation mechanics** with enhanced ender pearl functionality
- **Wall climbing** physics for Arachnid power
- **Invisibility mechanics** with light level detection for Phantom
- **Swimming enhancements** with speed modifiers for Merling
- **Damage immunity system** for fall damage, poison, and other effects
- **Health modification system** with proper attribute handling
- **Speed multipliers** for land and water movement

#### GUI System
- **Mystical chest interface** with themed item representations
- **Interactive power selection** with detailed tooltips and lore
- **Random power assignment** option for fate-based selection
- **Confirmation system** with permanent choice warnings
- **Visual feedback** with chat messages and sound effects
- **Automatic GUI opening** for new players

#### Commands and Permissions
- `/crystalpower` - Main command with GUI access
- `/cp`, `/power`, `/crystal` - Command aliases for convenience
- **Subcommands**: `select`, `random`, `info`, `list`, `reload`, `debug`
- **Permission system**: `crystalpowers.select`, `crystalpowers.admin`, `crystalpowers.bypass`
- **Tab completion** for all commands and crystal power names
- **Debug command** for troubleshooting crystal power issues

#### Technical Features
- **Event-driven architecture** with comprehensive player event handling
- **Equipment management** including Elytrian auto-elytra and restrictions
- **Potion effect system** with permanent and temporary effects
- **Damage modification system** with multipliers and immunities
- **Game mode compatibility** ensuring proper functionality across all modes
- **Error handling** with extensive logging and debug information

#### Code Protection
- **yGuard integration** ready for JAR obfuscation (Maven profile prepared)
- **Source code protection** against reverse engineering
- **Build system integration** with Maven for automated obfuscation

#### Developer Features
- **Comprehensive API** for other plugins to interact with Crystal Powers
- **Modular architecture** with separated managers, models, and utilities
- **Extensive documentation** with detailed README and code comments
- **Debug logging system** for troubleshooting and development

### Technical Details

#### Architecture
- **Package structure**: `com.crystalpowers.plugin` with organized subpackages
- **Manager classes**: `CrystalPowerManager`, `PlayerDataManager`
- **Model classes**: `CrystalPower`, `PlayerData` with builder patterns
- **Event listeners**: Comprehensive `PlayerListener` for all game events
- **GUI system**: `CrystalPowerBookGUI` with chest-based interface
- **Utility classes**: `JsonBuilder`, `EncryptionUtil` for data handling

#### Data Management
- **YAML-based storage** in `playerdata.yml`
- **UUID-based player identification** for server compatibility
- **Cooldown system** for crystal power changes (24-hour default)
- **Data persistence** across server restarts
- **Automatic data migration** from legacy Origins format

#### Performance Optimizations
- **Efficient event handling** with null checks and early returns
- **Minimal memory footprint** with optimized data structures
- **Lazy loading** of player data when needed
- **Scheduled tasks** for delayed operations and checks

### Compatibility
- **Minecraft Version**: 1.19.4+
- **Server Software**: Spigot, Paper (recommended)
- **Java Version**: 17+
- **Dependencies**: None (standalone plugin)

### Migration
- **Complete migration** from Origins plugin structure
- **Automatic data conversion** from legacy Origins format
- **Preserved player selections** during transition
- **Backward compatibility** with existing configurations

### Known Issues
- None reported in initial release

### Security
- **Permission-based access control** for admin functions
- **Input validation** for all commands and interactions
- **Safe data handling** with proper encoding and validation
- **Code obfuscation ready** for production deployment

---

**Note**: This is the initial release of Crystal Powers, representing a complete rewrite and rebranding of the Origins plugin with enhanced features, improved architecture, and comprehensive crystal power implementations.
