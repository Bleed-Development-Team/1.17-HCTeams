package net.frozenorb.foxtrot.extras.sale;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sale {
    private String name;
    private String displayName;
    private long startedAt;
    private long length;
    private String description;
    private ChatColor color;

    public void delete() {
        Foxtrot.getInstance().getSaleManager().getSales().remove(this);
    }
}
