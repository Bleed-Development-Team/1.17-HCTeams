package net.frozenorb.foxtrot.team.menu.button;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.lib.menu.Button;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.ForceLeaderCommand;
import net.frozenorb.foxtrot.team.menu.ConfirmMenu;
import net.frozenorb.foxtrot.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MakeLeaderButton extends Button {

    @NonNull private UUID uuid;
    @NonNull private Team team;

    @Override
    public String getName(Player player) {
        return (team.isOwner(uuid) ? "§a§l" : "§7") + UUIDUtils.name(uuid);
    }

    @Override
    public List<String> getDescription(Player player) {
        ArrayList<String> lore = new ArrayList<>();

        if (team.isOwner(uuid)) {
            lore.add("§aThis player is already the leader!");
        } else {
            lore.add("§eClick to change §b" + team.getName() + "§b's§e leader");
            lore.add("§eto §6" + UUIDUtils.name(uuid));
        }

        return lore;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 3;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.PLAYER_HEAD;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (team.isOwner(uuid)) {
            player.sendMessage(ChatColor.RED + "That player is already the leader!");
            return;
        }

        new ConfirmMenu("Make " + UUIDUtils.name(uuid) + " leader?", (b) -> {
            if (b) {//Need to fix "b" throughout the classes idk tbh
                /*
                ForceLeaderCommand.forceLeader(player, uuid);

                 */
            }
        }).openMenu(player);


    }


}
