package me.memorial.module.modules.render;

import me.memorial.Memorial;
import me.memorial.events.EventTarget;
import me.memorial.events.impl.misc.TickEvent;
import me.memorial.events.impl.render.Render2DEvent;
import me.memorial.events.impl.render.ShaderEvent;
import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.module.modules.combat.KillAura;
import me.memorial.module.modules.render.targethud.Style;
import me.memorial.utils.animations.Direction;
import me.memorial.utils.animations.impl.EaseBackIn;
import me.memorial.utils.render.RenderUtils;
import me.memorial.value.IntegerValue;
import me.memorial.value.ListValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

@ModuleInfo(name = "TargetHUD", description = "Shows your target's info", category = ModuleCategory.RENDER)
public class TargetHUD extends Module {
    public ListValue mode = new ListValue("Styles",new String[]{"Moon"},"Moon");
    public IntegerValue red = new IntegerValue("Red", 255, 0, 255);
    public IntegerValue blue = new IntegerValue("Blue", 255, 0, 255);
    public IntegerValue green = new IntegerValue("Green", 255, 0, 255);
    public IntegerValue x = new IntegerValue("X", 100, 1, (int) getScreenSize().getWidth() / 2,()-> false);
    public IntegerValue y = new IntegerValue("Y", 100, 1,(int) getScreenSize().getHeight() / 2,()-> false);
    public final EaseBackIn animation = new EaseBackIn(300,1,1.5F);

    private EntityLivingBase target;
    private final KillAura aura = (KillAura) Memorial.moduleManager.getModule(KillAura.class);
    public TargetHUD() {
        Style.init();
    }
    @EventTarget
    public void onUpdate(TickEvent event) {
        if (!aura.getState()) {
            animation.setDirection(Direction.BACKWARDS);
        }
        if (aura.getTarget() != null) {
            target = aura.getCurrentTarget();
            animation.setDirection(Direction.FORWARDS);
        }
        if ((aura.getState() && (aura.getCurrentTarget() == null || target != aura.getCurrentTarget()))) {
            animation.setDirection(Direction.BACKWARDS);
        }
        if (mc.currentScreen instanceof GuiChat) {
            animation.setDirection(Direction.FORWARDS);
            target = mc.thePlayer;
        }
    }
    @EventTarget
    public void onShader(ShaderEvent event) {
        if (target != null && target instanceof EntityPlayer) {
            RenderUtils.scaleStart(getX() + getWidth() / 2, getY() + getHeight() / 2, animation.getOutput().floatValue());
            Style.get(mode.get()).renderEffects(getX(), getY(), 255);
            RenderUtils.scaleEnd();
        }
    }
    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (target != null && target instanceof EntityPlayer) {
            RenderUtils.scaleStart(getX() + getWidth() / 2, getY() + getHeight() / 2, animation.getOutput().floatValue());
            Style.get(mode.get()).render(getX(), getY(), 255, (EntityPlayer) target);
            RenderUtils.scaleEnd();
        }
    }
    public float getWidth(){
        return Style.get(mode.get()).getWidth();
    }
    public float getHeight(){
        return Style.get(mode.get()).getHeight();
    }

    public int getX() {
        return x.get();
    }

    public int getY() {
        return y.get();
    }

    public void setX(int x) {
        this.x.set(x);
    }

    public void setY(int y) {
        this.y.set(y);
    }
    public Color getColor() {
        return new Color(red.get(),green.get(),blue.get());
    }
}
