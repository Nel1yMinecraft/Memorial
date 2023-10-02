package me.memorial.file.configs;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.memorial.Memorial;
import me.memorial.module.modules.render.XRay;
import me.memorial.file.FileConfig;
import me.memorial.file.FileManager;
import me.memorial.utils.ClientUtils;
import net.minecraft.block.Block;

import java.io.*;

public class XRayConfig extends FileConfig {

    /**
     * Constructor of config
     *
     * @param file of config
     */
    public XRayConfig(final File file) {
        super(file);
    }

    /**
     * Load config from file
     *
     * @throws IOException
     */
    @Override
    protected void loadConfig() throws IOException {
        final XRay xRay = (XRay) Memorial.moduleManager.getModule(XRay.class);

        if(xRay == null) {
            ClientUtils.getLogger().error("[FileManager] Failed to find xray module.");
            return;
        }

        final JsonArray jsonArray = new JsonParser().parse(new BufferedReader(new FileReader(getFile()))).getAsJsonArray();

        xRay.xrayBlocks.clear();

        for(final JsonElement jsonElement : jsonArray) {
            try {
                final Block block = Block.getBlockFromName(jsonElement.getAsString());

                if (xRay.xrayBlocks.contains(block) && block != null) {
                    ClientUtils.getLogger().error("[FileManager] Skipped xray block '" + block.getUnlocalizedName() + "' because the block is already added.");
                    continue;
                }

                xRay.xrayBlocks.add(block);
            }catch(final Throwable throwable) {
                ClientUtils.getLogger().error("[FileManager] Failed to add block to xray.", throwable);
            }
        }
    }

    /**
     * Save config to file
     *
     * @throws IOException
     */
    @Override
    protected void saveConfig() throws IOException {
        final XRay xRay = (XRay) Memorial.moduleManager.getModule(XRay.class);

        if(xRay == null) {
            ClientUtils.getLogger().error("[FileManager] Failed to find xray module.");
            return;
        }

        final JsonArray jsonArray = new JsonArray();

        for (final Block block : xRay.xrayBlocks)
            jsonArray.add(FileManager.PRETTY_GSON.toJsonTree(Block.getIdFromBlock(block)));

        final PrintWriter printWriter = new PrintWriter(new FileWriter(getFile()));
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonArray));
        printWriter.close();
    }
}
