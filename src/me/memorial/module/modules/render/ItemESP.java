package me.memorial.module.modules.render;

import me.memorial.events.EventTarget;
import me.memorial.events.impl.render.Render2DEvent;
import me.memorial.events.impl.render.Render3DEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.utils.ClientUtils;
import me.memorial.utils.render.ColorUtils;
import me.memorial.utils.render.RenderUtils;
import me.memorial.utils.render.shader.shaders.OutlineShader;
import me.memorial.value.BoolValue;
import me.memorial.value.IntegerValue;
import me.memorial.value.ListValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;

import java.awt.*;

@ModuleInfo(name = "ItemESP", description = "Allows you to see items through walls.", category = ModuleCategory.RENDER)
public class ItemESP extends Module {
    private final ListValue modeValue = new ListValue("Mode", new String[]{"Box", "ShaderOutline"}, "Box");
    private final IntegerValue colorRedValue = new IntegerValue("R", 0, 0, 255);
    private final IntegerValue colorGreenValue = new IntegerValue("G", 255, 0, 255);
    private final IntegerValue colorBlueValue = new IntegerValue("B", 0, 0, 255);
    private final BoolValue colorRainbow = new BoolValue("Rainbow", true);

    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        if (modeValue.get().equalsIgnoreCase("Box")) {
            final Color color = colorRainbow.get() ? ColorUtils.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());

            for (final Entity entity : mc.theWorld.loadedEntityList) {
                if (!(entity instanceof EntityItem || entity instanceof EntityArrow))
                    continue;

                RenderUtils.drawEntityBox(entity, color, true);
            }
        }
    }

    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        if (modeValue.get().equalsIgnoreCase("ShaderOutline")) {
            OutlineShader.OUTLINE_SHADER.startDraw(event.getPartialTicks());

            try {
                for (final Entity entity : mc.theWorld.loadedEntityList) {
                    if (!(entity instanceof EntityItem || entity instanceof EntityArrow))
                        continue;

                    mc.getRenderManager().renderEntityStatic(entity, event.getPartialTicks(), true);
                }
            } catch (final Exception ex) {
                ClientUtils.getLogger().error("An error occurred while rendering all item entities for shader esp", ex);
            }

            OutlineShader.OUTLINE_SHADER.stopDraw(colorRainbow.get() ? ColorUtils.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 1F, 1F);
        }
    }
}
