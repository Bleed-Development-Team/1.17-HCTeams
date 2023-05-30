package net.frozenorb.foxtrot.gameplay.airdrops;

import lombok.Getter;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.gameplay.airdrops.listener.AirDropListener;
import net.frozenorb.foxtrot.gameplay.airdrops.reward.AirDropReward;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AirDropHandler {
    private HCF instance;

    @Getter
    @Setter
    private List<UUID> variable = new ArrayList<>();
    @Getter
    @Setter
    private List<AirDropReward> cache = new ArrayList<>();

    @Getter
    private File file;
    @Getter
    private FileConfiguration data;

    @Getter
    private ItemStack itemStack;

    public AirDropHandler(HCF instance){
        this.instance = instance;

        this.itemStack = ItemBuilder.of(Material.DROPPER).name(ChatColor.AQUA + ChatColor.BOLD.toString() + "Air Drop").setLore(Arrays.asList(CC.translate("&7Obtainable at &ffrozenhcf.tebex.io&7."), "", ChatColor.YELLOW + "Place this down to designate a location for the drop!")).build();
        this.loadLootTable();

        Bukkit.getPluginManager().registerEvents(new AirDropListener(HCF.getInstance()), HCF.getInstance());
    }


    public void loadLootTable() {
        this.file = new File(HCF.getInstance().getDataFolder(), "data/airdrops.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (this.data.get("rewards") == null) {
            return;
        }

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> this.data.getConfigurationSection("rewards").getKeys(false).forEach(it ->
                this.cache.add(new AirDropReward(this.data.getItemStack("rewards." + it + ".itemStack"),
                        this.data.getDouble("rewards." + it + ".chance"),
                        this.data.getString("rewards." + it + ".command"),
                        this.data.getBoolean("rewards." + it + ".giveItem")))), 5);
    }



    public void saveLootTable() {
        this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

        int i = 0;

        for (AirDropReward airDropReward : this.cache) {
            i++;
            this.data.set("rewards.reward_" + i + ".itemStack", airDropReward.getItemStack());
            this.data.set("rewards.reward_" + i + ".chance", airDropReward.getChance());
            this.data.set("rewards.reward_" + i + ".command", airDropReward.getCommand());
            this.data.set("rewards.reward_" + i + ".giveItem", airDropReward.isGrantItem());
        }

        try {
            this.data.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setContents(Block block, Player player, boolean airdropAll) {
        if (!(block.getState() instanceof Dropper)) {
            return;
        }

        final Dropper dropper = (Dropper) block.getState();

        for (int i = 0; i < 9; i++) {
            dropper.getInventory().setItem(i, this.findReward(player, airdropAll));
        }
    }

    private ItemStack findReward(Player player, boolean airdropAll) {
        final List<AirDropReward> rewards = new ArrayList<>(this.cache);
        double sumNumber = rewards.stream().mapToDouble(AirDropReward::getChance).sum();
        double random = Math.random() * sumNumber;

        AirDropReward choice = new AirDropReward(null, 0, "", false);

        for (AirDropReward airDropReward : rewards) {
            double chance = airDropReward.getChance();

            if (chance > 0.0 && airdropAll && !airDropReward.getCommand().equalsIgnoreCase("none") && !airDropReward.getCommand().equalsIgnoreCase("")) {
                chance /= 2.5;
            }

            choice = airDropReward;
            random -= chance;
            if (random < 0) {
                break;
            }
        }

        final String command = choice.getCommand();

        if (!command.equalsIgnoreCase("none") && !command.equalsIgnoreCase("")) {
            final String item = choice.getItemStack().getItemMeta().hasDisplayName() ? choice.getItemStack().getItemMeta().getDisplayName() : choice.getItemStack().getType().name();
            for (Player onlinePlayer : this.instance.getServer().getOnlinePlayers()) {
                onlinePlayer.sendMessage("");
                onlinePlayer.sendMessage(CC.translate("&f" + player.getName() + " &6has just won a " + item + " &6from a &b&lAirdrop&6."));
                onlinePlayer.sendMessage(ChatColor.GRAY + "Purchase an Airdrop at " + ChatColor.WHITE + "&ffrozenhcf.tebex.io");
                onlinePlayer.sendMessage("");
            }

            this.instance.getServer().dispatchCommand(this.instance.getServer().getConsoleSender(), command.replace("{player}", player.getName()).replace("{displayName}", item));
        }

        if (itemStack.getAmount() <= 0) {
            itemStack.setAmount(1);
        }

        return !choice.isGrantItem() ? new ItemStack(Material.AIR) : ItemBuilder.copyOf(choice.getItemStack().clone()).amount(itemStack.getAmount() <= 0 ? 1 : choice.getItemStack().getAmount()).build();
    }
}

