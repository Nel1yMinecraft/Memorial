package me.memorial.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.memorial.file.configs.*;
import me.memorial.Memorial;
import me.memorial.utils.ClientUtils;
import me.memorial.utils.MinecraftInstance;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;

public class FileManager extends MinecraftInstance {

    public final File dir = new File(mc.mcDataDir, Memorial.CLIENT_NAME + "-1.8");
    public final File settingsDir = new File(dir, "settings");

    public final FileConfig modulesConfig = new ModulesConfig(new File(dir, "modules.json"));
    public final FileConfig valuesConfig = new ValuesConfig(new File(dir, "values.json"));
    public final FileConfig clickGuiConfig = new ClickGuiConfig(new File(dir, "clickgui.json"));
    public final AccountsConfig accountsConfig = new AccountsConfig(new File(dir, "accounts.json"));
    public final FriendsConfig friendsConfig = new FriendsConfig(new File(dir, "friends.json"));
    public final FileConfig xrayConfig = new XRayConfig(new File(dir, "xray-blocks.json"));
    public final FileConfig shortcutsConfig = new ShortcutsConfig(new File(dir, "shortcuts.json"));

    public final File backgroundFile = new File(dir, "userbackground.png");

    public boolean firstStart =  false;

    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Constructor of file manager
     * Setup everything important
     */
    public FileManager() {
        setupFolder();
        loadBackground();
    }

    /**
     * Setup folder
     */
    public void setupFolder() {
        if(!dir.exists()) {
            dir.mkdir();
            firstStart = true;
        }

        if(!settingsDir.exists())
            settingsDir.mkdir();
    }

    /**
     * Load all configs in file manager
     */
    public void loadAllConfigs() {
        for(final Field field : getClass().getDeclaredFields()) {
            if(field.getType() == FileConfig.class) {
                try {
                    if(!field.isAccessible())
                        field.setAccessible(true);

                    final FileConfig fileConfig = (FileConfig) field.get(this);
                    loadConfig(fileConfig);
                }catch(final IllegalAccessException e) {
                    ClientUtils.getLogger().error("Failed to load config file of field " + field.getName() + ".", e);
                }
            }
        }
    }

    /**
     * Load a list of configs
     *
     * @param configs list
     */
    public void loadConfigs(final FileConfig... configs) {
        for(final FileConfig fileConfig : configs)
            loadConfig(fileConfig);
    }

    /**
     * Load one config
     *
     * @param config to load
     */
    public void loadConfig(final FileConfig config) {
        if(!config.hasConfig()) {
            me.memorial.utils.ClientUtils.loginfo("[FileManager] Skipped loading config: " + config.getFile().getName() + ".");

            saveConfig(config, true);
            return;
        }

        try {
            config.loadConfig();
            me.memorial.utils.ClientUtils.loginfo("[FileManager] Loaded config: " + config.getFile().getName() + ".");
        }catch(final Throwable t) {
            ClientUtils.getLogger().error("[FileManager] Failed to load config file: " + config.getFile().getName() + ".", t);
        }
    }

    /**
     * Save all configs in file manager
     */
    public void saveAllConfigs() {
        for(final Field field : getClass().getDeclaredFields()) {
            if(field.getType() == FileConfig.class) {
                try {
                    if(!field.isAccessible())
                        field.setAccessible(true);

                    final FileConfig fileConfig = (FileConfig) field.get(this);
                    saveConfig(fileConfig);
                }catch(final IllegalAccessException e) {
                    ClientUtils.getLogger().error("[FileManager] Failed to save config file of field " +
                            field.getName() + ".", e);
                }
            }
        }
    }

    /**
     * Save a list of configs
     *
     * @param configs list
     */
    public void saveConfigs(final FileConfig... configs) {
        for(final FileConfig fileConfig : configs)
            saveConfig(fileConfig);
    }

    /**
     * Save one config
     *
     * @param config to save
     */
    public void saveConfig(final FileConfig config) {
        saveConfig(config, false);
    }

    /**
     * Save one config
     *
     * @param config         to save
     * @param ignoreStarting check starting
     */
    private void saveConfig(final FileConfig config, final boolean ignoreStarting) {
        if (!ignoreStarting && Memorial.INSTANCE.isStarting())
            return;

        try {
            if(!config.hasConfig())
                config.createConfig();

            config.saveConfig();
            me.memorial.utils.ClientUtils.loginfo("[FileManager] Saved config: " + config.getFile().getName() + ".");
        }catch(final Throwable t) {
            ClientUtils.getLogger().error("[FileManager] Failed to save config file: " +
                    config.getFile().getName() + ".", t);
        }
    }

    /**
     * Load background for background
     */
    public void loadBackground() {
        if(backgroundFile.exists()) {
            try {
                final BufferedImage bufferedImage = ImageIO.read(new FileInputStream(backgroundFile));

                if(bufferedImage == null)
                    return;

                Memorial.INSTANCE.setBackground(new ResourceLocation("liquidbounce" + "/background.png"));
                mc.getTextureManager().loadTexture(Memorial.INSTANCE.getBackground(), new DynamicTexture(bufferedImage));
                me.memorial.utils.ClientUtils.loginfo("[FileManager] Loaded background.");
            }catch(final Exception e) {
                ClientUtils.getLogger().error("[FileManager] Failed to load background.", e);
            }
        }
    }
}
