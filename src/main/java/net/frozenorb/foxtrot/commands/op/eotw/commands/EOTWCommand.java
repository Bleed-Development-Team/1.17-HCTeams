package net.frozenorb.foxtrot.commands.op.eotw.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.commands.op.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.op.eotw.EOTWHandler;
import net.frozenorb.foxtrot.server.listener.impl.EndListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

@CommandAlias("eotw")
@CommandPermission("foxtrot.eotw")
public class EOTWCommand extends BaseCommand {

    @Getter @Setter private static boolean ffaEnabled = false;
    @Getter @Setter private static long ffaActiveAt = -1L;
    public static int seconds = 10;

    private static final EOTWHandler eotwHandler = HCF.getInstance().getEotwHandler();

    @Subcommand("start")
    public static void eotw(Player sender, @Name("time") String time) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        if (HCF.getInstance().getServerHandler().isEOTW() || eotwHandler.eotwRunnable != null) {
            sender.sendMessage(ChatColor.RED + "The EOTW timer is already active.");
            return;
        }

        int seconds = TimeUtils.parseTime(time);

        if (seconds < 10){
            sender.sendMessage(ChatColor.RED + "Invalid time.");
            return;
        }

        eotwHandler.commenceCountdown(seconds);
    }

    @Subcommand("stop")
    public static void eotwStop(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        if (eotwHandler.eotwRunnable != null){
            eotwHandler.eotwRunnable.cancel();
            eotwHandler.eotwRunnable = null;
        }

        if (eotwHandler.preEotwRunnable != null){
            eotwHandler.preEotwRunnable.cancel();
            eotwHandler.preEotwRunnable = null;
        }

        if (CustomTimerCreateCommand.customTimers.containsKey("&4&lEOTW In")) {
            CustomTimerCreateCommand.customTimers.remove("&4&lEOTW In");
        }

        HCF.getInstance().getServerHandler().setEOTW(false);

        for (Team team : HCF.getInstance().getTeamHandler().getTeams()){
            team.setDTR(team.getMaxDTR());
            team.setDTRCooldown(0L);
        }

        EndListener.endActive = true;

        sender.sendMessage(CC.translate("&cYou have cancelled the EOTW timer."));

    }

    @Subcommand("tpall")
    public static void eotwTpAll(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        if (!HCF.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "This command must be ran during EOTW. (/eotw)");
            return;
        }

        for (Player onlinePlayer : HCF.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.teleport(sender.getLocation());
        }

        sender.sendMessage(ChatColor.RED + "Players teleported.");
    }

    @Subcommand("reduce")
    public void onReduceCommand(Player sender, @Name("amount")int amount) {
        double newAmount = Bukkit.getServer().getWorld("world").getWorldBorder().getSize() - amount;
        Bukkit.broadcastMessage(CC.translate("&7[&6&lBorder&7] &fThe border will be reduced to &6" + newAmount + " &fin &610 &fseconds."));
        seconds = 10;
        new BukkitRunnable(){
            @Override
            public void run() {
                seconds--;
                if (seconds <= 5 && seconds > 0) {
                    Bukkit.broadcastMessage(CC.translate("&7[&6&lBorder&7] &fThe border will be reduced to &6" + newAmount + " &fin &6" + seconds + "&f seconds."));
                }
                if (seconds <= 0) {
                    Bukkit.broadcastMessage(CC.translate("&7[&6&lBorder&7] &fThe border has been reduced to &6" + sender.getWorld().getWorldBorder().getSize() + "&f."));
                    sender.getWorld().getWorldBorder().setSize(newAmount);
                    cancel();
                }
            }
        }.runTaskTimer(HCF.getInstance(), 0, 20);
    }

    @Subcommand("ffa")
    public static void ffa(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }
 
        ConversationFactory factory = new ConversationFactory(HCF.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to enter FFA mode? This will start a countdown that cannot be cancelled. Type yes or no to confirm.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    ffaEnabled = true;
                    ffaActiveAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "FFA countdown initiated.");
                    
                    Bukkit.getScheduler().runTask(HCF.getInstance(), () -> {
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█");
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW " + ChatColor.GOLD.toString() + ChatColor.BOLD + "FFA" + ChatColor.RED.toString() + ChatColor.BOLD + " will commence in: 5:00.");
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + "If you ally, you will be punished.");
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████");
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
                    });

                    Bukkit.getScheduler().runTaskLater(HCF.getInstance(), () -> {
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█");
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW " + ChatColor.GOLD.toString() + ChatColor.BOLD + "FFA" + ChatColor.RED.toString() + ChatColor.BOLD + " has now commenced!");
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "Good luck and have fun!");
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████");
                        HCF.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
                    }, 5 * 60 * 20);
                    
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "FFA initation aborted.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §byes§a to confirm or §cno§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }



    public static boolean realFFAStarted() {
        return ffaEnabled && ffaActiveAt < System.currentTimeMillis();
    }

}