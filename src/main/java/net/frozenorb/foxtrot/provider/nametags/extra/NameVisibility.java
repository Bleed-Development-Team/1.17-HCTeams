package net.frozenorb.foxtrot.provider.nametags.extra;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NameVisibility {
    ALWAYS("always"),
    HIDE_FOR_OWN_TEAM("hideForOwnTeam"),
    NEVER("never"),
    HIDE_FOR_OTHER_TEAMS("hideForOtherTeams");
    private final String name;
}