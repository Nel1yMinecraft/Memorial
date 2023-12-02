package me.memorial.module.modules.client;

import lombok.Getter;
import me.memorial.events.EventTarget;
import me.memorial.events.impl.render.Render2DEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.ui.font.Fonts;
import me.memorial.utils.render.RenderUtils;
import me.memorial.value.IntegerValue;
import me.memorial.value.ListValue;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;

@ModuleInfo(name = "Notifications",description = "notif", category = ModuleCategory.CLIENT)
public class Notifications extends Module {
    private final ListValue mode = new ListValue("Mode", new String[]{"test","test2","test3","none"},"test");
    public IntegerValue x = new IntegerValue("X", 110, 1, 120);
    public IntegerValue y = new IntegerValue("Y", 20, 1,120);

    @Getter
    public static Notifications instance;

    @Getter
    private float width = 0.0f;
    @Getter
    private float height = 0.0f;
    @Getter
    private float x1 = x.get();


    public Notifications() {
        instance = this;
    }

    public void add(String str, FontRenderer font, Color rectcolor,Color fontcolor, boolean shadowtext) {

        if(mode.get().equals("none")) {
            return;
        }

        width = font.getStringWidth(str);
        height = font.FONT_HEIGHT;

        drawnotif(rectcolor);

        /*
        if (shadowtext) {
            font.drawStringWithShadow(str, x1 , y.get() , fontcolor.getRGB());
        } else {
            font.drawString(str, (int) x1, y.get() , fontcolor.getRGB());
        }

         */

        Fonts.font30.drawString(str, 7F, 17F, -1);

    }

    @EventTarget
    void onRender2D(Render2DEvent event) {
        add("test",Fonts.font35,Color.BLACK,Color.WHITE,true);
    }


    private void drawnotif(Color color) {

        if (mode.get().equals("test")) {
            RenderUtils.drawRoundedRect(x1, y.get(), width, height, 0f, color);
        }

        if (mode.get().equals("test2")) {
            RenderUtils.drawRect(x1, y.get(), width, height, color);
        }

        if(mode.get().equals("test3")) {
            RenderUtils.drawRect(22F, 0F, width, height, color);
            Fonts.font35.drawString("Module", 7F, 4F, -1);
        }

        RenderUtils.resetColor();

    }


}
