package net.frozenorb.foxtrot.extras.sale.gui;

import io.github.nosequel.menu.buttons.Button;
import io.github.nosequel.menu.pagination.PaginatedMenu;
import ltd.matrixstudios.alchemist.util.Chat;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.extras.sale.Sale;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.DyeUtils;
import net.frozenorb.foxtrot.util.ItemBuilder;
import net.frozenorb.foxtrot.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.conversations.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ActiveSalesGUI extends PaginatedMenu {

    public ActiveSalesGUI(Player player) {
        super(player, CC.translate("&e&lActive Sales"), 18);
    }

    @Override
    public void tick() {
        int i = 0;
        for (Sale sale : Foxtrot.getInstance().getSaleManager().getSales()) {
            buttons[i] = new Button(getSaleButton(sale)).setClickAction(e -> {
                e.setCancelled(true);
                if (e.getWhoClicked().hasPermission("foxtrot.admin")) {
                    reasonConvo(getPlayer(), sale);
                }
            });
            i++;
        }

    }

    private ItemStack getSaleButton(Sale sale) {
        System.out.println(sale.getStartedAt() + sale.getLength() - System.currentTimeMillis());
        return ItemBuilder.of(DyeUtils.chatColorToDye(sale.getColor()))
                .name(CC.translate(sale.getDisplayName()))
                .addToLore("&7&m--------------------")
                .addToLore("&eSale Data:")
                .addToLore("&7* &eDescription: &f" + sale.getDescription())
                .addToLore("&7* &eRemaining: &c" + TimeUtils.formatLongIntoDetailedString(sale.getStartedAt() + sale.getLength() - System.currentTimeMillis()))
                .addToLore("&7&m--------------------")
                .addToLore(sale.getDescription())
                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    private void reasonConvo(Player player, Sale sale) {
        player.closeInventory();
        ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            public String getPromptText(ConversationContext context) {
                return Chat.format("&ePlease type &ayes&e, or type &cno &eto cancel.");
            }

            public Prompt acceptInput(ConversationContext context, String input) {
                if (input.equalsIgnoreCase("no")) {
                    context.getForWhom().sendRawMessage(Chat.format("&cProcess aborted."));
                    return Prompt.END_OF_CONVERSATION;
                } else {

                    Foxtrot.getInstance().getSaleManager().getSales().remove(sale);

                    Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                        player.sendMessage(Chat.format("&a" + sale.getDisplayName() + "&f has been removed"));
                    }, 5L);

                    return Prompt.END_OF_CONVERSATION;
                }
            }
        }).withEscapeSequence("/no").withLocalEcho(false).withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");
        Conversation con = factory.buildConversation(player);
        player.beginConversation(con);
    }


}
