package me.memorial.module.modules.movement;

import me.memorial.events.*;
import me.memorial.events.impl.misc.TickEvent;
import me.memorial.events.impl.move.MotionEvent;
import me.memorial.events.impl.move.MoveEvent;
import me.memorial.events.impl.player.UpdateEvent;
import me.memorial.module.modules.movement.speeds.other.*;

import me.memorial.module.Module;
import me.memorial.module.ModuleCategory;
import me.memorial.module.ModuleInfo;
import me.memorial.module.modules.movement.speeds.SpeedMode;
import me.memorial.utils.MovementUtils;
import me.memorial.value.BoolValue;
import me.memorial.value.FloatValue;
import me.memorial.value.ListValue;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "Speed", description = "Allows you to move faster.", category = ModuleCategory.MOVEMENT)
public class Speed extends Module {

    private final SpeedMode[] speedModes = new SpeedMode[] {
            new CustomSpeed()
    };

    public final ListValue modeValue = new ListValue("Mode", getModes(), "Custom") {

        @Override
        protected void onChange(final String oldValue, final String newValue) {
            if(getState())
                onDisable();
        }

        @Override
        protected void onChanged(final String oldValue, final String newValue) {
            if(getState())
                onEnable();
        }
    };

    public final FloatValue customSpeedValue = new FloatValue("CustomSpeed", 1.6F, 0.2F, 2F);
    public final FloatValue customYValue = new FloatValue("CustomY", 0F, 0F, 4F);
    public final FloatValue customTimerValue = new FloatValue("CustomTimer", 1F, 0.1F, 2F);
    public final BoolValue customStrafeValue = new BoolValue("CustomStrafe", true);
    public final BoolValue resetXZValue = new BoolValue("CustomResetXZ", false);
    public final BoolValue resetYValue = new BoolValue("CustomResetY", false);


    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if(mc.thePlayer.isSneaking())
            return;

        if(MovementUtils.isMoving())
            mc.thePlayer.setSprinting(true);

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onUpdate();
    }

    @EventTarget
    public void onMotion(final MotionEvent event) {
        if(mc.thePlayer.isSneaking() || event.getState() != EventState.PRE)
            return;

        if(MovementUtils.isMoving())
            mc.thePlayer.setSprinting(true);

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onMotion();
    }

    @EventTarget
    public void onMove(MoveEvent event) {
        if(mc.thePlayer.isSneaking())
            return;

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onMove(event);
    }

    @EventTarget
    public void onTick(final TickEvent event) {
        if(mc.thePlayer.isSneaking())
            return;

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onTick();
    }

    @Override
    public void onEnable() {
        if(mc.thePlayer == null)
            return;

        mc.timer.timerSpeed = 1F;

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onEnable();
    }

    @Override
    public void onDisable() {
        if(mc.thePlayer == null)
            return;

        mc.timer.timerSpeed = 1F;

        final SpeedMode speedMode = getMode();

        if(speedMode != null)
            speedMode.onDisable();
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }

    private SpeedMode getMode() {
        final String mode = modeValue.get();

        for(final SpeedMode speedMode : speedModes)
            if(speedMode.modeName.equalsIgnoreCase(mode))
                return speedMode;

        return null;
    }

    private String[] getModes() {
        final List<String> list = new ArrayList<>();
        for(final SpeedMode speedMode : speedModes)
            list.add(speedMode.modeName);
        return list.toArray(new String[0]);
    }

}
