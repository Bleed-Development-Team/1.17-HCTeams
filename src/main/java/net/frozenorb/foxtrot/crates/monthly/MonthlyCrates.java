package net.frozenorb.foxtrot.crates.monthly;


import net.frozenorb.foxtrot.HCF;
import net.frozenorb.foxtrot.crates.monthly.animation.FinalAnimationService;
import net.frozenorb.foxtrot.crates.monthly.animation.ScrambleService;
import net.frozenorb.foxtrot.crates.monthly.events.CrateListener;

/**
 * The core class for all monthly crate stuff.
 *
 * @author Nopox
 * @project 1.17-HCTeams
 * @since 5/30/2023
 */
public class MonthlyCrates {

    public static MonthlyCrates instance;

    public void init() {
        HCF.getInstance().getServer().getPluginManager().registerEvents(new CrateListener(), HCF.getInstance());
        new ScrambleService().runTaskTimer(HCF.getInstance(), 1L, HCF.getInstance().getConfig().getInt("time.ScrambleAnimationSpeedInTicks"));
        new FinalAnimationService().runTaskTimer(HCF.getInstance(), 1L, HCF.getInstance().getConfig().getInt("time.FinalAnimationSpeedInTicks"));
    }

    public static MonthlyCrates getInstance() {
        return instance;
    }
}
