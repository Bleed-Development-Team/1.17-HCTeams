package net.frozenorb.foxtrot.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import me.vaperion.blade.annotation.Command;
import me.vaperion.blade.annotation.Name;
import me.vaperion.blade.annotation.Permission;
import me.vaperion.blade.annotation.Sender;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("ass|associate")
@CommandPermission("op")
public class AssociateAccountsCommand extends BaseCommand {



    @Default
    public void associate(Player sender, @Flags("other") Player target, @Flags("other") Player associate) {
        if(Foxtrot.getInstance().getWhitelistedIPMap().contains(target.getUniqueId())) {
            UUID other = Foxtrot.getInstance().getWhitelistedIPMap().get(target.getUniqueId());
            Foxtrot.getInstance().getWhitelistedIPMap().add(associate.getUniqueId(), other);
        } else if( Foxtrot.getInstance().getWhitelistedIPMap().contains(associate.getUniqueId())) {
            UUID other = Foxtrot.getInstance().getWhitelistedIPMap().get(associate.getUniqueId());
            Foxtrot.getInstance().getWhitelistedIPMap().add(target.getUniqueId(), other);
        } else {
            if( Foxtrot.getInstance().getWhitelistedIPMap().containsValue(target.getUniqueId())) {
                Foxtrot.getInstance().getWhitelistedIPMap().add(associate.getUniqueId(), target.getUniqueId());
            } else if( Foxtrot.getInstance().getWhitelistedIPMap().containsValue(associate.getUniqueId())) {
                Foxtrot.getInstance().getWhitelistedIPMap().add(target.getUniqueId(), associate.getUniqueId());
            } else {
                Foxtrot.getInstance().getWhitelistedIPMap().add(associate.getUniqueId(), target.getUniqueId());
            }
        }
        sender.sendMessage(ChatColor.GREEN + "You have successfully associated these accounts");
    }



}
