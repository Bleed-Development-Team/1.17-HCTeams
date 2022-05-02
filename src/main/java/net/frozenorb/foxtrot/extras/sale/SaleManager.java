package net.frozenorb.foxtrot.extras.sale;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class SaleManager {
    @Getter private List<Sale> sales = new ArrayList<>();

    public void create(String name, String displayName, long length, String description, ChatColor color) {
        for (Sale sale : this.sales) {
            if (sale.getName().equalsIgnoreCase(name)) {
                return;
            }
        }
        Sale sale = new Sale(name, displayName, System.currentTimeMillis(), length, description, color);
        sales.add(sale);
    }

    public Sale getSale(String name) {
        return getSales().stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);
    }
}
