package net.frozenorb.foxtrot.provider.nametags.packet.type;

import lombok.SneakyThrows;
import net.frozenorb.foxtrot.provider.nametags.extra.NameInfo;
import net.frozenorb.foxtrot.provider.nametags.extra.NameVisibility;
import net.frozenorb.foxtrot.provider.nametags.packet.NametagPacket;
import net.frozenorb.foxtrot.util.ReflectionUtils;
import net.minecraft.server.v1_16_R3.EnumChatFormat;
import net.minecraft.server.v1_16_R3.Packet;
import net.minecraft.server.v1_16_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NametagPacketV1_16_R3 extends NametagPacket {
    private final Map<String, String> teamsByPlayer;
    private final Map<String, NameInfo> teams;
    
    public NametagPacketV1_16_R3(Player llllllllllllllllIIllIIlIIlIllIll) {
        super(llllllllllllllllIIllIIlIIlIllIll);
        this.teams = new ConcurrentHashMap<>();
        this.teamsByPlayer = new ConcurrentHashMap<>();
    }
    
    private void sendPacket(Packet<?> packet) {
        PlayerConnection connection = ((CraftPlayer)this.player).getHandle().playerConnection;
        if (connection != null) {
            connection.sendPacket(packet);
        }
    }
    
    @Override
    public void addToTeam(Player player, String team) {
        String playerTeam = this.teamsByPlayer.get(player.getName());
        NameInfo info = this.teams.get(team);
        if (playerTeam != null && playerTeam.equals(team)) {
            return;
        }
        if (info == null) {
            return;
        }
        this.teamsByPlayer.put(player.getName(), team);
        this.sendPacket(new ScoreboardPacket(info, 3, Collections.singletonList(player.getName())).toPacket());
    }
    
    @Override
    public void create(String name, String color, String prefix, String suffix, boolean friendlyInvis, NameVisibility visibility) {
        NameInfo info1 = this.teams.get(name);
        if (info1 != null) {
            if (!info1.getColor().equals(color) || !info1.getPrefix().equals(prefix) || !info1.getSuffix().equals(suffix)) {
                NameInfo info2 = new NameInfo(name, color, prefix, suffix, visibility, friendlyInvis);
                this.teams.put(name, info2);
                this.sendPacket(new ScoreboardPacket(info2, 2).toPacket());
            }
            return;
        }
        NameInfo info3 = new NameInfo(name, color, prefix, suffix, visibility, friendlyInvis);
        this.teams.put(name, info3);
        this.sendPacket(new ScoreboardPacket(info3, 0).toPacket());
    }
    
    private static class ScoreboardPacket {
        private static final Field e;
        private static final Field i;
        private static final Map<String, EnumChatFormat> formats;
        private static final Field h;
        private final int action;
        private static final Field g;
        private static final Field d;
        private static final Field j;
        private static final Field a;
        private final List<String> players;
        private final NameInfo info;
        private static final Field b;
        private static final Field c;
        
        public ScoreboardPacket(NameInfo info, int action) {
            this.info = info;
            this.action = action;
            this.players = Collections.emptyList();
        }
        
        public ScoreboardPacket(NameInfo info, int action, List<String> players) {
            this.info = info;
            this.action = action;
            this.players = players;
        }
        
        @SneakyThrows
        public PacketPlayOutScoreboardTeam toPacket() {
            PacketPlayOutScoreboardTeam team = new PacketPlayOutScoreboardTeam();
            ScoreboardPacket.a.set(team, this.info.getName());
            ScoreboardPacket.i.set(team, this.action);
            if (this.action == 0 || this.action == 2) {
                ScoreboardPacket.b.set(team, CraftChatMessage.fromStringOrNull(this.info.getName()));
                ScoreboardPacket.c.set(team, CraftChatMessage.fromStringOrNull(this.info.getPrefix()));
                ScoreboardPacket.d.set(team, CraftChatMessage.fromStringOrNull(this.info.getSuffix()));
                ScoreboardPacket.g.set(team, this.getFormat(this.info.getColor()));
                ScoreboardPacket.j.set(team, this.info.isFriendlyInvis() ? 3 : 0);
                ScoreboardPacket.e.set(team, this.info.getVisibility().getName());
            }
            if (this.action == 3 || this.action == 4) {
                ScoreboardPacket.h.set(team, this.players);
            }
            return team;
        }
        
        static {
            a = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "a");
            b = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "b");
            c = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "c");
            d = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "d");
            e = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "e");
            g = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "g");
            h = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "h");
            i = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "i");
            j = ReflectionUtils.accessField(PacketPlayOutScoreboardTeam.class, "j");
            formats = Arrays.stream(EnumChatFormat.values()).collect(Collectors.toMap((Function<? super EnumChatFormat, ? extends String>)EnumChatFormat::toString, lllllllllllllllllIIIllIlIlIIlIlI -> lllllllllllllllllIIIllIlIlIIlIlI));
        }
        
        public EnumChatFormat getFormat(String lllllllllllllllllIIIllIlIlIlIllI) {
            EnumChatFormat lllllllllllllllllIIIllIlIlIlIlIl = ScoreboardPacket.formats.get(lllllllllllllllllIIIllIlIlIlIllI);
            return (lllllllllllllllllIIIllIlIlIlIlIl == null) ? EnumChatFormat.RESET : lllllllllllllllllIIIllIlIlIlIlIl;
        }
    }
}
