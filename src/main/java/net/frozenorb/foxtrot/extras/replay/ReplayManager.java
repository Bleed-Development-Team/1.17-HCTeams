package net.frozenorb.foxtrot.extras.replay;

import lombok.Getter;

public class ReplayManager {
    @Getter private static ReplayManager instance;

    public ReplayManager() {
        instance = this;
    }
}
