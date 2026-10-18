package me.mlgbot;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/** Valores do config.yml lidos uma vez (e de novo no /bot reload). */
public class Settings {

    public String botName;
    public boolean copySkin;
    public double reach;
    public int hitCooldown;
    public int hitBackDelay;
    public double botDamage;
    public double kbHorizontal;
    public double kbVertical;
    public double kbPerLevel;
    public boolean copyMovement;
    public boolean copyJump;
    public boolean copySneak;
    public boolean copySwing;
    public boolean copyBlockPlace;
    public boolean copyHeldItem;
    public double fallDepth;
    public boolean cleanBlocks;
    public int maxBlocks;

    private FileConfiguration cfg;

    public static Settings load(FileConfiguration c) {
        Settings s = new Settings();
        s.cfg = c;
        s.botName = c.getString("bot-name", "&cBot");
        s.copySkin = c.getBoolean("copy-skin", true);
        s.reach = c.getDouble("reach", 3.1);
        s.hitCooldown = c.getInt("hit-cooldown-ticks", 10);
        s.hitBackDelay = c.getInt("hit-back-delay-ticks", 2);
        s.botDamage = c.getDouble("bot-damage", 0.0);
        s.kbHorizontal = c.getDouble("knockback.horizontal", 0.4);
        s.kbVertical = c.getDouble("knockback.vertical", 0.4);
        s.kbPerLevel = c.getDouble("knockback.per-level", 0.5);
        s.copyMovement = c.getBoolean("copy.movement", true);
        s.copyJump = c.getBoolean("copy.jump", true);
        s.copySneak = c.getBoolean("copy.sneak", true);
        s.copySwing = c.getBoolean("copy.swing", true);
        s.copyBlockPlace = c.getBoolean("copy.block-place", true);
        s.copyHeldItem = c.getBoolean("copy.held-item", true);
        s.fallDepth = c.getDouble("fall-respawn-depth", 25.0);
        s.cleanBlocks = c.getBoolean("cleanup-blocks-on-remove", true);
        s.maxBlocks = c.getInt("max-bot-blocks", 300);
        return s;
    }

    /** Mensagem do config com prefixo e cores. */
    public String msg(String key) {
        String prefix = cfg.getString("messages.prefix", "&8[&cBot&8] &7");
        String text = cfg.getString("messages." + key, "");
        return ChatColor.translateAlternateColorCodes('&', prefix + text);
    }
}
