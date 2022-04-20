/*
 * Decompiled with CFR 0.150.
 */
package net.frozenorb.foxtrot.nametag.qlib;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class NametagThread {
    private static Map<NametagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<NametagUpdate, Boolean>();

    /*
    public NametagThread() {
        super("Foxtrot - Nametag Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            Iterator<NametagUpdate> pendingUpdatesIterator = pendingUpdates.keySet().iterator();
            while (pendingUpdatesIterator.hasNext()) {
                NametagUpdate pendingUpdate = pendingUpdatesIterator.next();
                try {
                    FrozenNametagHandler.applyUpdate(pendingUpdate);
                    pendingUpdatesIterator.remove();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep((long)FrozenNametagHandler.getUpdateInterval() * 50L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<NametagUpdate, Boolean> getPendingUpdates() {
        return pendingUpdates;
    }

     */
}

