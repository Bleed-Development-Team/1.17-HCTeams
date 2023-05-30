package net.frozenorb.foxtrot.team.commands.pvp;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.util.TimeUtils;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CommandAlias("pvp|timer|pvptimer")
public class PvPCommand extends BaseCommand {

    @Subcommand("addlives")
    @Description("Adds lives to a player")
    @CommandPermission("foxtrot.pvp.addlives")
    public static void pvpSetLives(CommandSender sender, @Name("target") Player fakePlayer, @Name("lifeType") String lifeType, @Name("amount") int amount) {
        UUID player = fakePlayer.getUniqueId();
        if (lifeType.equalsIgnoreCase("soulbound")) {
            HCF.getInstance().getSoulboundLivesMap().setLives(player, HCF.getInstance().getSoulboundLivesMap().getLives(player) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " " + amount + " soulbound lives.");

            Player bukkitPlayer = Bukkit.getPlayer(player);
            if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                String suffix = sender instanceof Player ? " from " + sender.getName() : "";
                bukkitPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " lives" + suffix);
            }

        } else if (lifeType.equalsIgnoreCase("friend")) {
            HCF.getInstance().getFriendLivesMap().setLives(player, HCF.getInstance().getFriendLivesMap().getLives(player) + amount);
            sender.sendMessage(ChatColor.YELLOW + "Gave " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " " + amount + " friend lives.");

            Player bukkitPlayer = Bukkit.getPlayer(player);
            if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                String suffix = sender instanceof Player ? " from " + sender.getName() : "";
                bukkitPlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " lives" + suffix);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Not a valid life type: Options are soulbound or friend");
        }
    }


    @Subcommand("create")
    @Description("Creates a new PvP timer")
    @CommandPermission("foxtrot.pvp.create")
    public static void pvpCreate(Player sender, @Optional Player player) {//TODO Smth for optional here there was a self after optional
        HCF.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(30));
        player.sendMessage(ChatColor.YELLOW + "You have 30 minutes of PVP Timer!");

        if (sender != player) {
            HCF.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(30));
            sender.sendMessage(ChatColor.YELLOW + "Gave 30 minutes of PVP Timer to " + player.getName() + ".");
            player.sendMessage(ChatColor.YELLOW + "You have 30 minutes of PVP Timer!");

            return;
        }
        HCF.getInstance().getPvPTimerMap().createTimer(sender.getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(30));
        player.sendMessage(ChatColor.YELLOW + "You have 30 minutes of PVP Timer!");

    }
    @Subcommand("enable|remove")
    @Description("Enables or disables a PvP timer")
    public static void pvpEnable(Player sender, @Optional Player target) {//TODO: Might need to fix this got no clue tbh
        if (target != sender && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return;
        }

        if (HCF.getInstance().getPvPTimerMap().hasTimer(target.getUniqueId())) {
            HCF.getInstance().getPvPTimerMap().removeTimer(target.getUniqueId());

            if (target == sender) {
                sender.sendMessage(ChatColor.RED + "Your PvP Timer has been removed!");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + "'s PvP Timer has been removed!");
            }
        } else {
            if (target == sender) {
                sender.sendMessage(ChatColor.RED + "You do not have a PvP Timer!");
            } else {
                sender.sendMessage(ChatColor.RED + target.getName() + " does not have a PvP Timer.");
            }
        }
    }

    @Subcommand("lives")
    @Description("Shows the amount of lives you have")
    public static void pvpLives(Player sender) {
        UUID player = sender.getUniqueId();
        String name = UUIDUtils.name(player);

        sender.sendMessage(ChatColor.GOLD + name + "'s Soulbound Lives: " + ChatColor.WHITE + HCF.getInstance().getSoulboundLivesMap().getLives(player));
        sender.sendMessage(ChatColor.GOLD + name + "'s Friend Lives: " + ChatColor.WHITE + HCF.getInstance().getFriendLivesMap().getLives(player));
    }

    @Default
    @HelpCommand
    @Description("Shows the current PvP timer")
    public static void pvp(Player sender) {
        String[] msges = {
                "§8§m-----------------------------------------------------",
                "§b§lPvP Help",
                "",
                "§7/pvp lives [target] §f- Display the amount of lives a player has.",
                "§7/pvp revive <player> §f- Revive a player.",
                "§7/pvp time §f- Display time left on PVP Timer.",
                "§7/pvp enable §f- Remove PVP Timer.",
                "§8§m-----------------------------------------------------"};

        sender.sendMessage(msges);
    }

    @Subcommand("revive")
    @Description("Revives a player!")
    public static void pvpRevive(Player sender, OfflinePlayer fakePlayer) {
        UUID player = fakePlayer.getUniqueId();
        int friendLives = HCF.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());

        if (HCF.getInstance().getServerHandler().isPreEOTW()) {
            sender.sendMessage(ChatColor.RED + "The server is in EOTW Mode: Lives cannot be used.");
            return;
        }

        if (friendLives <= 0) {
            sender.sendMessage(ChatColor.RED + "You have no lives which can be used to revive other players!");
            return;
        }

        if (!HCF.getInstance().getDeathbanMap().isDeathbanned(player)) {
            sender.sendMessage(ChatColor.RED + "That player is not deathbanned!");
            return;
        }

        if (HCF.getInstance().getServerHandler().getBetrayer(player) != null) {
            sender.sendMessage(ChatColor.RED + "Betrayers may not be revived!");
            return;
        }

        // Use a friend life.
        HCF.getInstance().getFriendLivesMap().setLives(sender.getUniqueId(), friendLives - 1);
        sender.sendMessage(ChatColor.YELLOW + "You have revived " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " with a friend life!");


        HCF.getInstance().getDeathbanMap().revive(player);
    }

    @Subcommand("setlives")
    @Description("Sets a player's lives")
    @CommandPermission("foxtrot.admin")
    public static void pvpSetLives(Player sender, Player fakePlayer, String lifeType, int amount) {
        UUID player = fakePlayer.getUniqueId();
        if (lifeType.equalsIgnoreCase("soulbound")) {
            HCF.getInstance().getSoulboundLivesMap().setLives(player, amount);
            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + "'s soulbound life count to " + amount + ".");
        } else if (lifeType.equalsIgnoreCase("friend")) {
            HCF.getInstance().getFriendLivesMap().setLives(player, amount);
            sender.sendMessage(ChatColor.YELLOW + "Set " + ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + "'s friend life count to " + amount + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "Not a valid life type: Options are soulbound or friend.");
        }
    }

    @Subcommand("time")
    @Description("Show the time left on the PvP Timer")
    public static void pvpTime(Player sender) {
        if (HCF.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You have " + TimeUtils.formatIntoMMSS(HCF.getInstance().getPvPTimerMap().getSecondsRemaining(sender.getUniqueId())) + " left on your PVP Timer.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have a PVP Timer on!");
        }
    }

}