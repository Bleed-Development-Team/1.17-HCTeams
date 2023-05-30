package net.frozenorb.foxtrot.chat.trivia.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.frozenorb.foxtrot.HCF;
import org.bukkit.entity.Player;

@CommandAlias("trivia")
@CommandPermission("op")
public class TriviaCommand extends BaseCommand {

    @Subcommand("start")
    public void start(Player player){
        HCF.getInstance().getTriviaHandler().startTrivia();
    }

    @Subcommand("end")
    public void end(Player player){
        HCF.getInstance().getTriviaHandler().endTrivia(null);
    }
}
