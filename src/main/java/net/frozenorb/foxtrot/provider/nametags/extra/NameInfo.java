package net.frozenorb.foxtrot.provider.nametags.extra;

import lombok.Getter;

@Getter
public class NameInfo {
    private final String name;
    private final String suffix;
    private final boolean friendlyInvis;
    private final String color;
    private final NameVisibility visibility;
    private final String prefix;

    public NameInfo(String name, String color, String prefix, String suffix, NameVisibility visibility, boolean friendlyInvs) {
        this.name = name;
        this.color = color;
        this.prefix = prefix;
        this.suffix = suffix;
        this.visibility = visibility;
        this.friendlyInvis = friendlyInvs;
    }

}