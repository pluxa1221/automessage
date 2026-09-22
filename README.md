# AutoMessage

Plugin for Minecraft that sends messages (broadcasts) in the game chat every `n` seconds (set in the configuration).

## Configuration

```yaml
# Plugin chat prefix
prefix: "<gradient:#00ffcc:#0066ff>[AutoMessage]</gradient> "

# Command messages
command-messages:
  reload: "<green>✅ Config successfully reloaded!</green>"
  no-permission: "<red>⛔ You haven't need permission to execute this command!</red>"
  unknown: "<yellow>🤔 Unknown command. Try /automessage reload</yellow>"

# Messages for sending (examples)
messages:
  - text: "<gold>Welcome, <green>%player_name%</green>!</gold> <newline><click:run_command:'/shop'><aqua>[Open shop]</aqua></click>"
    type: chat
    period: 120

  - text: "<yellow>Your balance: <green>%vault_eco_balance_fixed%</green> coins</yellow>"
    type: actionbar
    period: 180

  - text: "<blue>📢 Subscribe to us in the Discord! </blue><click:open_url:'https://discord.gg/yourserver'><gradient:#ff00ff:#00ffff>[PRESS]</gradient></click>"
    type: chat
    period: 300

  - text:
      title: "<gradient:#ff0000:#ffaa00>⚔ PvP Zone!</gradient>"
      subtitle: "<white>Careful, <red>PVP is enabled!</red></white>"
    type: title
    period: 400

  - text: "<gradient:#ff00cc:#6600ff>❖ Vote for the server every 24 hours! ❖</gradient> <newline><click:open_url:'https://topcraft.ru/vote'><yellow>[Vote]</yellow></click>"
    type: chat
    period: 600

  - text: "<green>⏳ Left <aqua>%server_time_12%</aqua> to the event!</green>"
    type: actionbar
    period: 60

  - text:
      title: "<gradient:#00ff00:#006600>🌱 Plant trees!</gradient>"
      subtitle: "<yellow>Buy seedings in the /shop</yellow>"
    type: title
    period: 900
```

## License

This project is licensed under MIT license - see [file](LICENSE) for details.
