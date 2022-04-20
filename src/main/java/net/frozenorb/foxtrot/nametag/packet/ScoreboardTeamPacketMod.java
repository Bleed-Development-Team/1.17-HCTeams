package net.frozenorb.foxtrot.nametag.packet;

public final class ScoreboardTeamPacketMod {
    /*
    private PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam(); //dawg I don't know what I'm doing here
    private static Field aField;
    private static Field bField;
    private static Field cField;
    private static Field dField;
    private static Field eField;
    private static Field fField;
    private static Field gField;

    public ScoreboardTeamPacketMod(String name, String prefix, String suffix, Collection players, int paramInt) {
        try {
            aField.set((Object)this.packet, name);
            fField.set((Object)this.packet, paramInt);
            if (paramInt == 0 || paramInt == 2) {
                bField.set((Object)this.packet, name);
                cField.set((Object)this.packet, prefix);
                dField.set((Object)this.packet, suffix);
                gField.set((Object)this.packet, 3);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (paramInt == 0) {
            this.addAll(players);
        }
    }

    public ScoreboardTeamPacketMod(String name, Collection players, int paramInt) {
        try {
            gField.set((Object)this.packet, 3);
            aField.set((Object)this.packet, name);
            fField.set((Object)this.packet, paramInt);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        this.addAll(players);
    }

    public void sendToPlayer(Player bukkitPlayer) {
        ((CraftPlayer)bukkitPlayer).getHandle().b.sendPacket(this.packet);
    }

    private void addAll(Collection col) {
        if (col == null) {
            return;
        }
        try {
            ((Collection)eField.get((Object)this.packet)).addAll(col);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            aField = PacketPlayOutScoreboardTeam.class.getDeclaredField("a");
            bField = PacketPlayOutScoreboardTeam.class.getDeclaredField("b");
            cField = PacketPlayOutScoreboardTeam.class.getDeclaredField("c");
            dField = PacketPlayOutScoreboardTeam.class.getDeclaredField("d");
            eField = PacketPlayOutScoreboardTeam.class.getDeclaredField("e");
            fField = PacketPlayOutScoreboardTeam.class.getDeclaredField("f");
            gField = PacketPlayOutScoreboardTeam.class.getDeclaredField("g");
            aField.setAccessible(true);
            bField.setAccessible(true);
            cField.setAccessible(true);
            dField.setAccessible(true);
            eField.setAccessible(true);
            fField.setAccessible(true);
            gField.setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

     */
}
