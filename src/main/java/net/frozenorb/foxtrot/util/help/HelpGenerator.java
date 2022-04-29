package net.frozenorb.foxtrot.util.help;

import me.vaperion.blade.command.BladeCommand;
import me.vaperion.blade.command.impl.BukkitUsageMessage;
import me.vaperion.blade.context.BladeContext;
import me.vaperion.blade.utils.PaginatedOutput;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class HelpGenerator implements me.vaperion.blade.help.HelpGenerator {

    @Override
    public @NotNull List<String> generate(@NotNull BladeContext context, @NotNull List<BladeCommand> commands) {
        commands = commands.stream().distinct().filter(c -> !c.isHidden()).collect(Collectors.toList());

        return new PaginatedOutput<BladeCommand>(10) {
            @Override
            public String formatErrorMessage(Error error, Object... args) {
                return switch (error) {
                    case NO_RESULTS -> CC.translate("&cNo results found.");
                    case PAGE_OUT_OF_BOUNDS -> CC.translate("&cThe page index %d is out of bounds.");
                };
            }

            @Override
            public String getHeader(int page, int totalPages) {
                return CC.translate("&7&m------------------------------------");
            }

            @Override
            public String getFooter(int page, int totalPages) {
                return CC.translate("&7&m------------------------------------");
            }

            @Override
            public String formatLine(BladeCommand result, int index) {
                return CC.translate("&c" + ChatColor.stripColor(result.getUsageMessage().ensureGetOrLoad(() -> new BukkitUsageMessage(result)).toString().replace("Usage: ", "")) + (result.getDescription().isEmpty() ? "" : " &7- " + result.getDescription()));
            }
        }.generatePage(commands, parsePage(context.argument(0)));
    }

    private int parsePage(String argument) {
        if (argument == null) return 1;
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
