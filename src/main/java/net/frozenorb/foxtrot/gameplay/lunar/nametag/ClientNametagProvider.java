package net.frozenorb.foxtrot.gameplay.lunar.nametag;

import net.frozenorb.foxtrot.HCF;

public class ClientNametagProvider implements Runnable{

    @Override
    public void run() {
        HCF.getInstance().getNametagManager().update();
    }
}
