package me.mlgbot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MLGBotPlugin extends JavaPlugin implements Listener {

    private final Map<UUID, BotSession> sessions = new HashMap<UUID, BotSession>();
    private Settings settings;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        settings = Settings.load(getConfig());
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            public void run() {
                tickAll();
            }
        }, 1L, 1L);
        getLogger().info("MLGBot ativado. Use /bot spawn");
    }

    @Override
    public void onDisable() {
        for (BotSession s : new ArrayList<BotSession>(sessions.values())) {
            s.remove(true);
        }
        sessions.clear();
    }

    public Settings settings() {
        return settings;
    }

    private void tickAll() {
        if (sessions.isEmpty()) {
            return;
        }
        for (BotSession s : new ArrayList<BotSession>(sessions.values())) {
            boolean ok;
            try {
                ok = s.tick();
            } catch (Throwable t) {
                getLogger().warning("Erro no bot, removendo: " + t);
                t.printStackTrace();
                ok = false;
            }
            if (!ok) {
                s.remove(true);
                sessions.remove(s.getOwnerId());
            }
        }
    }

    private void removeSession(Player p) {
        BotSession s = sessions.remove(p.getUniqueId());
        if (s != null) {
            s.remove(true);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String sub = args.length > 0 ? args[0].toLowerCase() : "spawn";

        if (sub.equals("reload")) {
            if (!sender.hasPermission("mlgbot.admin")) {
                sender.sendMessage(settings.msg("no-permission"));
                return true;
            }
            reloadConfig();
            settings = Settings.load(getConfig());
            sender.sendMessage(settings.msg("reloaded"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(settings.msg("only-players"));
            return true;
        }
        Player p = (Player) sender;
        if (!p.hasPermission("mlgbot.use")) {
            p.sendMessage(settings.msg("no-permission"));
            return true;
        }

        if (sub.equals("spawn")) {
            removeSession(p);
            Location l = p.getLocation();
            Vector dir = l.getDirection();
            dir.setY(0);
            if (dir.lengthSquared() < 1.0E-6) {
                dir = new Vector(0, 0, 1);
            }
            dir.normalize().multiply(3.0);
            Location target = l.clone().add(dir);
            target.setY(l.getY());
            BotSession s = new BotSession(this, p, target);
            sessions.put(p.getUniqueId(), s);
            p.sendMessage(settings.msg("spawned"));
            return true;
        }

        if (sub.equals("remove")) {
            if (sessions.containsKey(p.getUniqueId())) {
                removeSession(p);
                p.sendMessage(settings.msg("removed"));
            } else {
                p.sendMessage(settings.msg("no-bot"));
            }
            return true;
        }

        p.sendMessage(settings.msg("usage"));
        return true;
    }

    @EventHandler
    public void onSwing(PlayerAnimationEvent e) {
        BotSession s = sessions.get(e.getPlayer().getUniqueId());
        if (s != null) {
            s.onOwnerSwing();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        BotSession s = sessions.get(e.getPlayer().getUniqueId());
        if (s != null) {
            s.onOwnerPlace(e.getBlockPlaced());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        removeSession(e.getPlayer());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        removeSession(e.getPlayer());
    }
}
