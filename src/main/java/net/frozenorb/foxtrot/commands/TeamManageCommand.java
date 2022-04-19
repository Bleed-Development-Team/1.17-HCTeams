package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.SetTeamBalanceCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamCreateCommand;
import net.frozenorb.foxtrot.team.menu.DTRMenu;
import net.frozenorb.foxtrot.team.menu.DemoteMembersMenu;
import net.frozenorb.foxtrot.team.menu.KickPlayersMenu;
import net.frozenorb.foxtrot.team.menu.MuteMenu;
import net.frozenorb.foxtrot.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

@CommandAlias("manageteam")
@CommandPermission("foxtrot.manage")
public class TeamManageCommand extends BaseCommand {

    @Command(value = {"manageteam leader"})
    @Permission(value = "fotrot.manage")
    public static void teamLeader(@Sender Player sender, @Name("team") Team team) {
    }

    @Command(value = {"manageteam promote"})
    @Permission(value = "fotrot.manage")
    public static void promoteTeam(@Sender Player sender, @Name("team") Team team) {
    }

    @Command(value = {"manageteam demote"})
    @Permission(value = "fotrot.manage")
    public static void demoteTeam(@Sender Player sender, @Name("team") Team team) {
        new DemoteMembersMenu(team).openMenu(sender);
    }


    @Command(value = {"manageteam kick"})
    @Permission(value = "fotrot.manage")
    public static void kickTeam(@Sender Player sender, @Name("team") Team team) {
        new KickPlayersMenu(team).openMenu(sender);
    }


    @Command(value = {"manageteam balance"})
    @Permission(value = "fotrot.manage")
    public static void balanceTeam(@Sender Player sender, @Name("team") Team team) {
        conversationDouble(sender, "§bEnter a new balance for " + team.getName() + ".", (d) -> {
            SetTeamBalanceCommand.setTeamBalance(sender, team, d.floatValue());
            sender.sendRawMessage(ChatColor.GRAY + team.getName() + " now has a balance of " + team.getBalance());
        });
    }

    @Command(value = {"manageteam dtr"})
    @Permission(value = "fotrot.manage")
    public static void dtrTeam(@Sender Player sender, @Name("team") Team team) {
        if (sender.hasPermission("foxtrot.manage.setdtr")) {
            conversationDouble(sender, "§eEnter a new DTR for " + team.getName() + ".", (d) -> {
                team.setDTR(d.floatValue());
                sender.sendRawMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " has a new DTR of " + ChatColor.LIGHT_PURPLE + d.floatValue() + ChatColor.YELLOW + ".");
            });
        } else {
            new DTRMenu(team).openMenu(sender);
        }
    }

    @Command(value = {"manageteam rename"})
    @Permission(value = "fotrot.manage")
    public static void renameTeam(@Sender Player sender, @Name("team") Team team) {
        conversationString(sender, "§aEnter a new name for " + team.getName() + ".", (name) -> {
            String oldName = team.getName();
            team.rename(name);
            sender.sendRawMessage(ChatColor.GRAY + oldName + " now has a name of " + team.getName());
        });
    }


    @Command(value = {"manageteam mute"})
    @Permission(value = "fotrot.manage")
    public static void muteTeam(@Sender Player sender, @Name("team") Team team) {
        new MuteMenu(team).openMenu(sender);

    }


    @Command(value = {"manageteam manage"})
    @Permission(value = "foxtrot.manage")
    public static void manageTeam(@Sender Player sender, @Name("team") Team team) {
    }

    private static void conversationDouble(Player p, String prompt, Callback<Double> callback) {
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return prompt;
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                try {
                    callback.callback(Double.parseDouble(s));
                } catch (NumberFormatException e) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + s + " is not a number.");
                }

                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(p);
        p.beginConversation(con);

    }

    private static void conversationString(Player p, String prompt, Callback<String> callback) {
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return prompt;
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String newName) {

                if (newName.length() > 16) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (newName.length() < 3) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (!TeamCreateCommand.ALPHA_NUMERIC.matcher(newName).find()) {
                    if (Foxtrot.getInstance().getTeamHandler().getTeam(newName) == null) {
                        callback.callback(newName);
                        return Prompt.END_OF_CONVERSATION;

                    } else {
                        cc.getForWhom().sendRawMessage(ChatColor.RED + "A team with that name already exists!");
                    }
                } else {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Team names must be alphanumeric!");
                }


                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(p);
        p.beginConversation(con);

    }
}
