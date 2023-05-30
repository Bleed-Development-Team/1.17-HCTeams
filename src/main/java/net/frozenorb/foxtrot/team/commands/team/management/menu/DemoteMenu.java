package net.frozenorb.foxtrot.team.commands.team.management.menu;

import com.cryptomorin.xseries.SkullUtils;
import io.github.nosequel.menu.Menu;
import io.github.nosequel.menu.buttons.Button;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import java.util.UUID;

public class DemoteMenu extends Menu {

    private final Team team;

    public DemoteMenu(Player player, Team team) {
        super(player, CC.translate("&eDemote a team member"), 9 * 4);

        this.team = team;
    }

    @Override
    public void tick() {

        int x = 10;

        for (UUID uuid : team.getMembers()){
            if (team.isMember(uuid) || team.isOwner(uuid)) continue;

            Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();

            for (int i = 0; i <= 9; i++){
                buttons[i] = glass();
            }

            for (int i = 27; i <= 35; i++){
                buttons[i] = glass();
            }



            buttons[9] = glass();
            buttons[18] = glass();

            buttons[17] = glass();
            buttons[26] = glass();

            buttons[x++] = new Button(
                    ItemBuilder.of(Material.PLAYER_HEAD)
                    .owningPlayer(player.getName())
                    .name("&6" + player.getName())
                            .addToLore("&7&m---------------------", "&7Click this player to demote them.", "", "&b&l| &fOld Rank: " + currentRank(uuid), "&b&l| &fNew Rank: " + calcRank(uuid), "", "&7&m---------------------")
                            .build()
            ).setClickAction(
                    event -> {
                        event.setCancelled(true);
                        getPlayer().closeInventory();

                        String rank;

                        if (team.isCaptain(uuid)){
                            team.removeCaptain(uuid);
                            team.addMember(uuid);

                            rank = "Member";
                        } else {
                            team.removeCoLeader(uuid);
                            team.addCaptain(uuid);

                            rank = "Captain";
                        }

                        team.sendMessage("&b" + player.getName() + " &fhas been demoted to &b" + rank + "&f forcefully.");
                        getPlayer().sendMessage(CC.translate("&aYou have demoted " + player.getName() + " to " + rank + "."));
                    }
            );
        }
    }

    private Button glass(){
        return new Button(ItemBuilder.of(Material.ORANGE_STAINED_GLASS).name("").enchant(Enchantment.DAMAGE_ALL, 1)
                .flag(ItemFlag.HIDE_ENCHANTS).build());
    }

    private String calcRank(UUID uuid){
        String rank;

        if (team.isCaptain(uuid)){
            team.removeCaptain(uuid);
            team.addMember(uuid);

            rank = "&7Member";
        } else {
            team.removeCoLeader(uuid);
            team.addCaptain(uuid);

            rank = "&bCaptain";
        }

        return rank;
    }

    private String currentRank(UUID uuid){
        String rank;

        if (team.isCoLeader(uuid)){
            rank = "&3Co-Leader";
        } else if (team.isCaptain(uuid)){
            rank = "&bCaptain";
        } else {
            rank = "&7Member";
        }

        return rank;
    }
}
