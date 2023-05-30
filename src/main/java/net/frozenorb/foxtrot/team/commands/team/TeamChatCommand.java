package net.frozenorb.foxtrot.team.commands.team;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("fc|tc")
public class TeamChatCommand extends BaseCommand {

    @Default
    public static void fc(Player sender) {
        setChat(sender, ChatMode.TEAM);
    }

    public static void setChat(Player player, ChatMode chatMode) {
        if (chatMode != null) {
            Team playerTeam = HCF.getInstance().getTeamHandler().getTeam(player);

            if (chatMode != ChatMode.PUBLIC) {
                if (playerTeam == null) {
                    player.sendMessage(ChatColor.RED + "You must be on a team to use this chat mode.");
                    return;
                } else if (chatMode == ChatMode.OFFICER && !playerTeam.isCaptain(player.getUniqueId()) && !playerTeam.isCoLeader(player.getUniqueId()) && !playerTeam.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You must be an officer or above in your team to use this chat mode.");
                    return;
                }
            }

            switch (chatMode) {
                case PUBLIC:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in public chat.");
                    break;
                case ALLIANCE:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in alliance chat.");
                    break;
                case TEAM:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in team chat.");
                    break;
                case OFFICER:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in officer chat.");
                    break;
            }

            HCF.getInstance().getChatModeMap().setChatMode(player.getUniqueId(), chatMode);
        } else {
            switch (HCF.getInstance().getChatModeMap().getChatMode(player.getUniqueId())) {
                case PUBLIC -> {
                    Team team = HCF.getInstance().getTeamHandler().getTeam(player);
                    boolean teamHasAllies = team != null && team.getAllies().size() > 0;
                    setChat(player, teamHasAllies ? ChatMode.ALLIANCE : ChatMode.TEAM);
                }
                case ALLIANCE -> setChat(player, ChatMode.TEAM);
                case TEAM -> {
                    Team team2 = HCF.getInstance().getTeamHandler().getTeam(player);
                    boolean isOfficer = team2 != null && (team2.isCaptain(player.getUniqueId()) || team2.isCoLeader(player.getUniqueId()) || team2.isOwner(player.getUniqueId()));
                    setChat(player, isOfficer ? ChatMode.OFFICER : ChatMode.PUBLIC);
                }
                case OFFICER -> setChat(player, ChatMode.PUBLIC);
            }
        }
    }

}