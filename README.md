# Crystal Powers Minecraft Plugin

A Minecraft Spigot/Paper plugin that adds crystal-powered abilities and unique powers, inspired by the popular Origins mod.

## Features

### Available Crystal Powers

1. **Human** - Balanced power with no special abilities or weaknesses
2. **Avian** - Bird-like beings who can fly but are fragile
   - Flight ability
   - No fall damage
   - Less health (8 hearts)
   - Weak to projectiles
3. **Enderian** - Mysterious beings from the End with teleportation abilities
   - Enhanced ender pearl teleportation
   - Night vision
   - Takes damage from water and rain
4. **Arachnid** - Spider-like beings who can climb walls
   - Wall climbing (sneak against walls)
   - Night vision
   - Poison immunity
   - Less health (8 hearts)
5. **Merling** - Aquatic beings who thrive underwater
   - Water breathing
   - Faster swimming
   - Night vision underwater
   - Slower movement on land
6. **Phantom** - Ghostly beings with phasing abilities
   - Invisibility toggle (sneak + right-click)
   - Immune to fall damage
   - Burns in sunlight
   - Less health (7 hearts)

### Chest-Based Interface

The plugin features an immersive **mystical chest GUI** interface for crystal power selection:

- **Interactive Chest GUI**: Crystal powers are presented in a beautifully designed chest interface
- **Detailed Descriptions**: Each power has detailed tooltips with lore-friendly descriptions
- **Visual Selection**: Click through power items to explore different options
- **Random Selection**: Includes a "Random Power" button for random assignment
- **Permanent Choice**: Once selected, powers cannot be changed (as warned in the GUI)
- **Immersive Experience**: Themed items and mystical presentation enhance roleplay
- **Easy Navigation**: Back buttons and intuitive layout for seamless browsing

The chest GUI automatically opens when new players join the server, or can be accessed anytime with `/crystalpower`, `/cp gui`, or `/power menu`.

## Installation

1. Download the latest release
2. Place the `CrystalPowers.jar` file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/CrystalPowers/config.yml` if needed

## Commands

- `/crystalpower` - Open the mystical Crystal Powers chest GUI
- `/cp gui` - Open the Crystal Powers chest GUI (explicit)
- `/power menu` - Open the Crystal Powers chest GUI (alternative alias)
- `/crystalpower select <power>` - Select a specific crystal power
- `/crystalpower random` - Let fate choose a random crystal power for you
- `/crystalpower info [power]` - Show information about your current power or a specific power
- `/crystalpower list` - List all available crystal powers
- `/crystalpower reload` - Reload the plugin configuration (requires `crystalpowers.admin` permission)

### Debug Commands (Admin Only)
- `/crystalpower debug` - Show detailed debug information about crystal power system
- `/crystalpower reload` - Reload the plugin configuration (requires `crystalpowers.admin` permission)

## Permissions

- `crystalpowers.select` - Allows players to select a crystal power (default: true)
- `crystalpowers.admin` - Allows access to admin commands (default: op)
- `crystalpowers.bypass` - Bypass crystal power change cooldown (default: op)

## Configuration

The plugin creates a `config.yml` file where you can:
- Enable/disable specific crystal powers
- Modify crystal power properties
- Change cooldown settings
- Customize messages
- Configure GUI settings
- Adjust debug logging levels

### Code Protection

The Crystal Powers plugin includes code protection features to prevent reverse engineering:

- **JAR Obfuscation**: The compiled JAR file can be obfuscated using yGuard to protect source code
- **Class Name Scrambling**: Class and method names are obfuscated to prevent easy decompilation
- **Control Flow Obfuscation**: Code logic is made harder to follow and understand
- **String Encryption**: Important strings and constants are encrypted within the bytecode
- **Anti-Decompilation**: Multiple layers of protection against common decompilation tools
- **Maven Integration**: Obfuscation is integrated into the build process via Maven

The obfuscation is applied during the build process and does not affect plugin functionality or performance.

## Building from Source

### Prerequisites
- Java 17 or higher
- Maven

### Build Steps
```bash
git clone <repository-url>
cd crystal-powers-plugin
mvn clean package
```

The compiled plugin will be in the `target` folder. To build with obfuscation:

```bash
mvn clean package -Pobfuscate
```

This will create an obfuscated JAR file that protects the source code from reverse engineering.

## API Usage

Other plugins can interact with Crystal Powers using the provided API:

```java
// Get a player's crystal power
CrystalPowersPlugin plugin = (CrystalPowersPlugin) Bukkit.getPluginManager().getPlugin("CrystalPowers");
PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
String powerId = data.getCrystalPowerId();

// Check if player has specific power
if ("avian".equals(powerId)) {
    // Player has Avian crystal power
}
```

## Compatibility

- **Minecraft Version**: 1.19.4+
- **Server Software**: Spigot, Paper (recommended)
- **Java Version**: 17+

## Support

For bug reports, feature requests, or questions, please create an issue on the GitHub repository.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Credits

Inspired by the Origins mod by Apace100 for Fabric/Forge.
