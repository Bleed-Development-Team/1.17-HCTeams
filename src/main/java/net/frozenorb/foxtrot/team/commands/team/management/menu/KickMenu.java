package net.frozenorb.foxtrot.team.commands.team.management.menu;

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

public class KickMenu extends Menu {

    private final Team team;

    public KickMenu(Player player, Team team) {
        super(player, CC.translate("&eKick a team member"), 9 * 4);

        this.team = team;
    }

    @Override
    public void tick() {

        int x = 10;

        for (UUID uuid : team.getMembers()){
            if (team.isOwner(uuid)) continue;

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
                            .owningPlayer("")
                    .name("&6" + player.getName())
                            .addToLore("&7&m---------------------", "&7Click this player to kick them.", "&7&m---------------------")
                            .build()
            ).setClickAction(
                    event -> {
                        event.setCancelled(true);
                        getPlayer().closeInventory();

                        team.removeMember(uuid);
                        team.removeCaptain(uuid);
                        team.removeCoLeader(uuid);

                        team.sendMessage("&b" + player.getName() + " &fhas been kicked forcefully.");
                        getPlayer().sendMessage(CC.translate("&aYou have kicked " + player.getName() + "."));
                    }
            );
        }
    }

    private Button glass(){
        return new Button(ItemBuilder.of(Material.ORANGE_STAINED_GLASS).name("").enchant(Enchantment.DAMAGE_ALL, 1)
                .flag(ItemFlag.HIDE_ENCHANTS).build());
    }
}
