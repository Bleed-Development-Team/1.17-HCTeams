package net.frozenorb.foxtrot.configuration;

import net.frozenorb.foxtrot.HCF;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public abstract class Config extends YamlConfiguration {
    private final File file = new File(HCF.getInstance().getDataFolder(), getFileName());

    private static Config config;

    public Config getConfig() {
        if (config == null) {
            config = this;
        }
        return config;
    }

    public void loadConfig() {
        if (!file.exists()) {
            HCF.getInstance().saveResource(file.getName(), false);
        }

        this.reload();
    }

    public abstract String getFileName();

    public void reload() {
        try {
            this.load(file);
            this.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
