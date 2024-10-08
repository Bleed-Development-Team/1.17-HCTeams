package net.frozenorb.foxtrot.chat.listeners;

import com.google.common.collect.ImmutableMap;
import me.clip.placeholderapi.PlaceholderAPI;
import net.frozenorb.foxtrot.HCFConstants;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.chat.ChatHandler;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamCommands;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST) // this handler prevents people from getting banned for spam in faction (or ally) chat
    public void onAsyncPlayerChatEarly(AsyncPlayerChatEvent event) {
        ChatMode playerChatMode = HCF.getInstance().getChatModeMap().getChatMode(event.getPlayer().getUniqueId());
        ChatMode forcedChatMode = ChatMode.findFromForcedPrefix(event.getMessage().charAt(0));
        ChatMode finalChatMode;

        if (forcedChatMode != null) {
            finalChatMode = forcedChatMode;
        } else {
            finalChatMode = playerChatMode;
        }

        if (finalChatMode != ChatMode.PUBLIC) {
            event.getPlayer().setMetadata("NoSpamCheck", new FixedMetadataValue(HCF.getInstance(), true));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        event.getPlayer().removeMetadata("NoSpamCheck", HCF.getInstance());

        Team playerTeam = HCF.getInstance().getTeamHandler().getTeam(event.getPlayer());
        String rankPrefix = PlaceholderAPI.setPlaceholders(event.getPlayer(), "%luckperms_prefix%");

        String customPrefix = null;
        String customColor = null;

        if (playerTeam != null){
            customPrefix = playerTeam.getCustomPrefix();
            customColor = playerTeam.getCustomColor();
        }

        ChatMode playerChatMode = HCF.getInstance().getChatModeMap().getChatMode(event.getPlayer().getUniqueId());
        ChatMode forcedChatMode = ChatMode.findFromForcedPrefix(event.getMessage().charAt(0));
        ChatMode finalChatMode;

        // If they provided us with a forced chat mode, we have to remove it from the final message.
        // We also .trim() the message because people will do things like '! hi', which just looks annoying in chat.
        if (forcedChatMode != null) {
            event.setMessage(event.getMessage().substring(1).trim());
        }

        if (forcedChatMode != null) {
            finalChatMode = forcedChatMode;
        } else {
            finalChatMode = playerChatMode;
        }

        // If another plugin cancelled this event before it got to us (we are on MONITOR, so it'll happen)
        if (event.isCancelled() && finalChatMode == ChatMode.PUBLIC) { // Only respect cancelled events if this is public chat. Who cares what their team says.
            return;
        }

        // Any route we go down will cancel the event eventually.
        // Let's just do it here.
        event.setCancelled(true);

        // If someone's not in a team, instead of forcing their 'channel' to public,
        // we just tell them they can't.
        if (finalChatMode != ChatMode.PUBLIC && playerTeam == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in non-public chat if you're not in a team!");
            return;
        }

        if (finalChatMode != ChatMode.PUBLIC) {
            if (finalChatMode == ChatMode.OFFICER && !playerTeam.isCaptain(event.getPlayer().getUniqueId()) && !playerTeam.isCoLeader(event.getPlayer().getUniqueId()) && !playerTeam.isOwner(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in officer chat if you're not an officer!");
                return;
            }
        }


        // and here starts the big logic switch
        switch (finalChatMode) {
            case PUBLIC -> {
                if (TeamCommands.getTeamMutes().containsKey(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team is muted!");
                    return;
                }

                String publicChatFormat = HCFConstants.publicChatFormat(playerTeam, " " + rankPrefix, customPrefix);

                if (HCF.getInstance().getConfig().getBoolean("legions")) {
                    publicChatFormat = HCFConstants.publicChatFormatTwoPointOhBaby(event.getPlayer(), playerTeam, rankPrefix, customPrefix, customColor);
                }

                String finalMessage = String.format(publicChatFormat, event.getPlayer().getDisplayName(), event.getMessage())
                        .replace("null", "");

                // Loop those who are to receive the message (which they won't if they have the sender /ignore'd or something),
                // not online players
                for (Player player : event.getRecipients()) {
                    if (playerTeam == null) {
                        // If the player sending the message is shadowmuted (if their team was and they left it)
                        // then we don't allow them to. We probably could move this check "higher up", but oh well.
                        if (TeamCommands.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())) {
                            continue;
                        }

                        // If their chat is enabled (which it is by default) or the sender is op, send them the message
                        // The isOp() fragment is so OP messages are sent regardless of if the player's chat is toggled
                        if (event.getPlayer().isOp() || HCF.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId())) {
                            player.sendMessage(finalMessage);
                        }
                    } else {
                        if (playerTeam.isMember(player.getUniqueId())) {
                            // Gypsies way to get a custom color if they're allies/teammates
                            player.sendMessage(finalMessage.replace(ChatColor.GOLD + "[" + HCF.getInstance().getServerHandler().getDefaultRelationColor(), ChatColor.GOLD + "[" + ChatColor.DARK_GREEN));
                        } else if (playerTeam.isAlly(player.getUniqueId())) {
                            // Gypsie way to get a custom color if they're allies/teammates
                            player.sendMessage(finalMessage.replace(ChatColor.GOLD + "[" + HCF.getInstance().getServerHandler().getDefaultRelationColor(), ChatColor.GOLD + "[" + Team.ALLY_COLOR));
                        } else {
                            // We only check this here as...
                            // Team members always see their team's messages
                            // Allies always see their allies' messages, 'cause they'll probably be in a TS or something
                            // and they could figure out this code even exists
                            if (TeamCommands.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())) {
                                continue;
                            }

                            // If their chat is enabled (which it is by default) or the sender is op, send them the message
                            // The isOp() fragment is so OP messages are sent regardless of if the player's chat is toggled
                            if (event.getPlayer().isOp() || HCF.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId())) {
                                player.sendMessage(finalMessage);
                            }
                        }
                    }
                }
                ChatHandler.getPublicMessagesSent().incrementAndGet();
                HCF.getInstance().getServer().getConsoleSender().sendMessage(finalMessage);
            }
            case ALLIANCE -> {
                String allyChatFormat = HCFConstants.allyChatFormat(event.getPlayer(), event.getMessage());
                String allyChatSpyFormat = HCFConstants.allyChatSpyFormat(playerTeam, event.getPlayer(), event.getMessage());

                // Loop online players and not recipients just in case you're weird and
                // /ignore your teammates/allies
                for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                    if (playerTeam.isMember(player.getUniqueId()) || playerTeam.isAlly(player.getUniqueId())) {
                        player.sendMessage(allyChatFormat);
                    } else if (HCF.getInstance().getChatSpyMap().getChatSpy(player.getUniqueId()).contains(playerTeam.getUniqueId())) {
                        player.sendMessage(allyChatSpyFormat);
                    }
                }

                // Log to ally's allychat log.
                for (ObjectId allyId : playerTeam.getAllies()) {
                    Team ally = HCF.getInstance().getTeamHandler().getTeam(allyId);

                    if (ally != null) {
                        TeamActionTracker.logActionAsync(ally, TeamActionType.ALLY_CHAT_MESSAGE, ImmutableMap.<String, Object>builder()
                                .put("allyTeamId", playerTeam.getUniqueId())
                                .put("allyTeamName", playerTeam.getName())
                                .put("playerId", event.getPlayer().getUniqueId())
                                .put("playerName", event.getPlayer().getName())
                                .put("message", event.getMessage())
                                .build()
                        );
                    }
                }
                TeamActionTracker.logActionAsync(playerTeam, TeamActionType.ALLY_CHAT_MESSAGE, ImmutableMap.of(
                        "playerId", event.getPlayer().getUniqueId(),
                        "playerName", event.getPlayer().getName(),
                        "message", event.getMessage()
                ));
                HCF.getInstance().getServer().getLogger().info("[Ally Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
            }
            case TEAM -> {
                String teamChatFormat = HCFConstants.teamChatFormat(event.getPlayer(), event.getMessage());
                String teamChatSpyFormat = HCFConstants.teamChatSpyFormat(playerTeam, event.getPlayer(), event.getMessage());

                // Loop online players and not recipients just in case you're weird and
                // /ignore your teammates
                for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                    if (playerTeam.isMember(player.getUniqueId())) {
                        player.sendMessage(teamChatFormat);
                    } else if (HCF.getInstance().getChatSpyMap().getChatSpy(player.getUniqueId()).contains(playerTeam.getUniqueId())) {
                        player.sendMessage(teamChatSpyFormat);
                    }
                }

                // Log to our teamchat log.
                TeamActionTracker.logActionAsync(playerTeam, TeamActionType.TEAM_CHAT_MESSAGE, ImmutableMap.of(
                        "playerId", event.getPlayer().getUniqueId(),
                        "playerName", event.getPlayer().getName(),
                        "message", event.getMessage()
                ));
                HCF.getInstance().getServer().getLogger().info("[Team Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
            }
            case OFFICER -> {
                String officerChatFormat = HCFConstants.officerChatFormat(event.getPlayer(), event.getMessage());

                // Loop online players and not recipients just in case you're weird and
                // /ignore your teammates
                for (Player player : HCF.getInstance().getServer().getOnlinePlayers()) {
                    if (playerTeam.isCaptain(player.getUniqueId()) || playerTeam.isCoLeader(player.getUniqueId()) || playerTeam.isOwner(player.getUniqueId())) {
                        player.sendMessage(officerChatFormat);
                    }
                }

                // Log to our teamchat log.
                TeamActionTracker.logActionAsync(playerTeam, TeamActionType.OFFICER_CHAT_MESSAGE, ImmutableMap.of(
                        "playerId", event.getPlayer().getUniqueId(),
                        "playerName", event.getPlayer().getName(),
                        "message", event.getMessage()
                ));
                HCF.getInstance().getServer().getLogger().info("[Officer Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
            }
        }
    }


}