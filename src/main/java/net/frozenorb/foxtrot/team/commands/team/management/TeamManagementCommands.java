package net.frozenorb.foxtrot.team.commands.team.management;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamCommands;
import net.frozenorb.foxtrot.team.commands.team.management.menu.DemoteMenu;
import net.frozenorb.foxtrot.team.commands.team.management.menu.KickMenu;
import net.frozenorb.foxtrot.team.commands.team.management.menu.PromoteMenu;
import net.frozenorb.foxtrot.team.commands.team.management.menu.TeamLeaderMenu;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Callback;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

@CommandAlias("manageteam")
@CommandPermission("foxtrot.manage")
public class TeamManagementCommands extends BaseCommand {

    @Subcommand("leader")
    @CommandCompletion("@team")
    public static void leader(Player player, @Name("team") Team team){
        new TeamLeaderMenu(player, team).updateMenu();
    }

    @Subcommand("promote")
    @CommandCompletion("@team")
    public static void promote(Player player, @Name("team") Team team){
        new PromoteMenu(player, team).updateMenu();
    }

    @Subcommand("demote")
    @CommandCompletion("@team")
    public static void demote(Player player, @Name("team") Team team){
        new DemoteMenu(player, team).updateMenu();
    }


    @Subcommand("kick")
    @CommandCompletion("@team")
    public static void kick(Player player, @Name("team") Team team){
        new KickMenu(player, team).updateMenu();
    }

    @Subcommand("dtr")
    @CommandCompletion("@team")
    public static void dtr(Player player, @Name("team") Team team){
        conversationDouble(player, CC.translate("&eEnter a new DTR for " + team.getName() + "."), (amount) -> {
            team.setDTR(amount.floatValue());

            player.sendMessage(CC.translate("&aYou have set the DTR of " + team.getName() + " to " + amount.floatValue()));
        });
    }

    @Subcommand("dq")
    @CommandCompletion("@team")
    public static void dq(Player player, @Name("team") Team team){
        team.setDQ(!team.getDQ());

        player.sendMessage(CC.translate("&fYou have &r" + (team.getDQ() ? "&cdisqualified" : "&aundisqualified") + " &fthe team &d" + team.getName() + ""));
    }

    @Subcommand("balance")
    @CommandCompletion("@team")
    public static void balance(Player player, @Name("team") Team team){
        conversationDouble(player, CC.translate("&eEnter a new balance for " + team.getName() + "."), (amount) -> {
            team.setBalance(amount);

            player.sendMessage(CC.translate("&aYou have set the balance of " + team.getName() + " to " + amount));
        });
    }

    @Subcommand("rename")
    @CommandCompletion("@team")
    public static void rename(Player player, @Name("team") Team team){
        conversationString(player, CC.translate("&eEnter a new name for " + team.getName() + "."), (name) -> {
            String oldName = team.getName();

            team.setName(name);

            player.sendMessage(CC.translate("&a" + oldName + " has been renamed to " + team.getName() + "."));
        });
    }

    @Subcommand("points")
    @CommandCompletion("@team")
    public static void points(Player player, @Name("team") Team team){
        conversationInteger(player, CC.translate("&eEnter the points you want to set for " + team.getName() + "."), (amount) -> {
            team.setPoints(amount);

            player.sendMessage(CC.translate("&a" + team.getName() + "'s points has been updated to " + team.getName() + "."));
        });
    }

    private static void conversationInteger(Player p, String prompt, Callback<Integer> callback) {
        ConversationFactory factory = new ConversationFactory(HCF.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return prompt;
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                try {
                    callback.callback(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + s + " is not a number.");
                }

                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(p);
        p.beginConversation(con);

    }


    private static void conversationDouble(Player p, String prompt, Callback<Double> callback) {
        ConversationFactory factory = new ConversationFactory(HCF.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

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
        ConversationFactory factory = new ConversationFactory(HCF.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

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

                if (!TeamCommands.ALPHA_NUMERIC.matcher(newName).find()) {
                    if (HCF.getInstance().getTeamHandler().getTeam(newName) == null) {
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
