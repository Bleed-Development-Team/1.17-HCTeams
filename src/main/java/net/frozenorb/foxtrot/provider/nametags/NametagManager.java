package net.frozenorb.foxtrot.provider.nametags;

import com.lunarclient.bukkitapi.LunarClientAPI;
import lombok.Getter;
import lombok.SneakyThrows;
import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.provider.nametags.adapter.FoxtrotNametags;
import net.frozenorb.foxtrot.provider.nametags.listener.NametagListener;
import net.frozenorb.foxtrot.provider.nametags.packet.NametagPacket;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class NametagManager {
    private final NametagAdapter adapter;
    public final Map<UUID, Nametag> nametags;
    private final ExecutorService executor;
    
    private void updateLunarTags(Player from, Player to, String update) {
        List<String> lines = new ArrayList<>();
        /*
        PlayerTeam team = this.getInstance().getTeamManager().getByPlayer(to.getUniqueId());
        String prefix = this.getInstance().getUserManager().getPrefix(to);
        if (team != null) {
            String teamPosition = team.getTeamPosition();
            if (teamPosition != null) {
                lines.add(new FastReplaceString(this.getLunarConfig().getString("NAMETAGS.TEAM_TOP")).replaceAll("%pos%", teamPosition).replaceAll("%name%", team.getDisplayName(from)).replaceAll("%dtr-color%", team.getDtrColor()).replaceAll("%dtr%", team.getDtrString()).replaceAll("%dtr-symbol%", team.getDtrSymbol()).endResult());
            }
            else {
                lines.add(new FastReplaceString(this.getLunarConfig().getString("NAMETAGS.NORMAL")).replaceAll("%name%", team.getDisplayName(from)).replaceAll("%dtr-color%", team.getDtrColor()).replaceAll("%dtr%", team.getDtrString()).replaceAll("%dtr-symbol%", team.getDtrSymbol()).endResult());
            }
        }
        if (this.getInstance().getVersionManager().isVer16() || Utils.isVer16(from)) {
            Tasks.execute(this, () -> LunarClientAPI.getInstance().overrideNametag(to, lines, from));
            return;
        }
        lines.add(((prefix != null) ? prefix + " " : "") + update + to.getName());
        LunarClientAPI.getInstance().overrideNametag(to, lines, from);

         */
        Team team = HCF.getInstance().getTeamHandler().getTeam(to.getUniqueId());

        if (team != null){
            lines.add(CC.translate((team.getDQ() ? "&4&l✗ " : "") + team.getCustomPrefix() + "&6[" + (team.isMember(from.getUniqueId()) ? "&a" : (team.getCustomColor().equals("") ? "&c" : team.getCustomColor())) + team.getName() + " &7" + CC.VERTICAL_SEPARATOR + " &r" + team.getFormattedDTR() + "&6]"));
        }
        lines.add(CC.translate(update + to.getName()));

        Bukkit.getScheduler().runTask(HCF.getInstance(), task -> LunarClientAPI.getInstance().overrideNametag(to, lines, from));
    }
    
    public NametagManager() {
        this.nametags = new ConcurrentHashMap<>();
        this.adapter = new FoxtrotNametags();
        this.executor = Executors.newSingleThreadExecutor();
        Bukkit.getPluginManager().registerEvents(new NametagListener(), HCF.getInstance());
    }

    public void update() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                this.executor.execute(() -> {
                    String update = this.adapter.getAndUpdate(player, target);
                    this.updateLunarTags(player, target, update);
                });
            }
        }
    }
    
    @SneakyThrows
    public NametagPacket createPacket(Player player) {
        String version = "net.frozenorb.foxtrot.provider.nametags.packet.type.NametagPacketV1_16_R3";
        return (NametagPacket)Class.forName(version).getConstructor(Player.class).newInstance(player);
    }
    
}