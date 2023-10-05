package me.memorial.module.modules.render;

import me.memorial.events.EventTarget
import me.memorial.events.impl.render.Render3DEvent
import me.memorial.module.Module
import me.memorial.module.ModuleCategory
import me.memorial.module.ModuleInfo
import me.memorial.module.modules.misc.AntiBot
import me.memorial.ui.font.Fonts
import me.memorial.utils.EntityUtils
import me.memorial.utils.render.ColorUtils.stripColor
import me.memorial.utils.render.RenderUtils
import me.memorial.value.*
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import java.util.*
import kotlin.math.roundToInt


@ModuleInfo(name = "NameTags", description = "Changes the scale of the nametags so you can always read them.", category = ModuleCategory.RENDER)
class NameTags : Module() {

    val typeValue = ListValue("Mode", arrayOf("3DTag"), "3DTag")

    //2DTags
    private val armorValue = BoolValue("Armor", true)
    private val healthValue = BoolValue("Health", true)
    private val distanceValue = BoolValue("Distance", true)
    private val scaleValue = FloatValue("Scale", 1.0F, 0.5F, 2.0F)

    //3DTags
    private val healthBarValue = BoolValue("Bar", true)// !typeValue.get().equals("2dtag", true) })
    private val pingValue = BoolValue("Ping", true)// !typeValue.get().equals("2dtag", true) })
    private val translateY = FloatValue("TranslateY", 0.55F, -2F, 2F)// !typeValue.get().equals("2dtag", true) })
    private val potionValue = BoolValue("Potions", true)// !typeValue.get().equals("2dtag", true) })
    private val clearNamesValue = BoolValue("ClearNames", false)// !typeValue.get().equals("2dtag", true) })
    private val fontValue = FontValue("Font", Fonts.font40)// !typeValue.get().equals("2dtag", true) })
    private val fontShadowValue = BoolValue("Shadow", true)// !typeValue.get().equals("2dtag", true) })
    private val borderValue = BoolValue("Border", true)// !typeValue.get().equals("2dtag", true) })
    val localValue = BoolValue("LocalPlayer", true)// !typeValue.get().equals("2dtag", true) })
    val nfpValue = BoolValue("NoFirstPerson", true)// localValue.get() && !typeValue.get().equals("2dtag", true)})
    private val backgroundColorRedValue =
        IntegerValue("Background-R", 0, 0, 255)// !typeValue.get().equals("2dtag", true) })
    private val backgroundColorGreenValue =
        IntegerValue("Background-G", 0, 0, 255)// !typeValue.get().equals("2dtag", true) })
    private val backgroundColorBlueValue =
        IntegerValue("Background-B", 0, 0, 255)// !typeValue.get().equals("2dtag", true) })
    private val backgroundColorAlphaValue =
        IntegerValue("Background-Alpha", 0, 0, 255)// !typeValue.get().equals("2dtag", true) })
    private val borderColorRedValue = IntegerValue("Border-R", 0, 0, 255)// !typeValue.get().equals("2dtag", true) })
    private val borderColorGreenValue = IntegerValue("Border-G", 0, 0, 255)// !typeValue.get().equals("2dtag", true) })
    private val borderColorBlueValue = IntegerValue("Border-B", 0, 0, 255)// !typeValue.get().equals("2dtag", true) })
    private val borderColorAlphaValue =
        IntegerValue("Border-Alpha", 0, 0, 255)// !typeValue.get().equals("2dtag", true) })

    private val inventoryBackground = ResourceLocation("textures/gui/container/inventory.png")
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        when (typeValue.get().lowercase(Locale.getDefault())) {
            "3dtag" -> {
                glPushMatrix()

                glDisable(GL_LIGHTING)
                glDisable(GL_DEPTH_TEST)

                glEnable(GL_LINE_SMOOTH)

                glEnable(GL_BLEND)
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

                for (entity in mc.theWorld.loadedEntityList) {
                    if (!EntityUtils.isSelected(
                            entity,
                            false
                        ) && (!localValue.get() || entity !== mc.thePlayer || nfpValue.get() && mc.gameSettings.thirdPersonView == 0)
                    ) {
                        continue
                    }
                    renderNameTags(
                        (entity as EntityLivingBase),
                        (if (clearNamesValue.get()) stripColor(entity.getDisplayName().unformattedText) else entity.getDisplayName().unformattedText)!!
                    )
                }

                glPopMatrix()

                resetColor()
                glColor4f(1f, 1f, 1f, 1f)

            }
        }
    }

    private fun renderNameTags(entity: EntityLivingBase, tag: String) {
        when (typeValue.get().lowercase(Locale.getDefault())) {
            "3dtag" -> {
                val fontRenderer = fontValue.get()

                // Modify tag
                val bot = AntiBot.isBot(entity)
                val ping = if (entity is EntityPlayer) EntityUtils.getPing(entity) else 0

                val distanceText =
                    if (distanceValue.get()) " §a${mc.thePlayer.getDistanceToEntity(entity).roundToInt()} " else ""
                val pingText =
                    if (pingValue.get() && entity is EntityPlayer) " §7[" + (if (ping > 200) "§c" else if (ping > 100) "§e" else "§a") + ping + "ms§7]" else ""
                val healthText = if (healthValue.get()) " §a" + entity.health.toInt() + " " else ""
                val botText = if (bot) " §7[§6§lBot§7] " else ""

                val text = "$tag$healthText$distanceText$pingText$botText"

                // Push
                glPushMatrix()

                // Translate to player position
                val timer = mc.timer
                val renderManager = mc.renderManager


                glTranslated( // Translate to player position with render pos and interpolate it
                    entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX,
                    entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY + entity.eyeHeight.toDouble() + translateY.get()
                        .toDouble(),
                    entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ
                )

                glRotatef(-mc.renderManager.playerViewY, 0F, 1F, 0F)
                glRotatef(mc.renderManager.playerViewX, 1F, 0F, 0F)


                // Scale
                var distance = mc.thePlayer.getDistanceToEntity(entity) / 4F

                if (distance < 1F) {
                    distance = 1F
                }

                val scale = (distance / 150F) * scaleValue.get()

                glScalef(-scale, -scale, scale)

                //AWTFontRenderer.assumeNonVolatile = true

                // Draw NameTag
                val width = fontRenderer.getStringWidth(text) * 0.5f
                val widths = fontRenderer.getStringWidth(text) / 3.8f

                val dist = width + 4F - (-width - 2F)

                glDisable(GL_TEXTURE_2D)
                glEnable(GL_BLEND)

                val bgColor = Color(
                    backgroundColorRedValue.get(),
                    backgroundColorGreenValue.get(),
                    backgroundColorBlueValue.get(),
                    backgroundColorAlphaValue.get()
                )
                val borderColor = Color(
                    borderColorRedValue.get(),
                    borderColorGreenValue.get(),
                    borderColorBlueValue.get(),
                    borderColorAlphaValue.get()
                )

                if (borderValue.get())
                    RenderUtils.quickDrawBorderedRect(
                        -width - 2F,
                        -2F,
                        width + 4F,
                        fontRenderer.FONT_HEIGHT + 2F + if (healthBarValue.get()) 2F else 0F,
                        2F,
                        borderColor.rgb,
                        bgColor.rgb
                    )
                else
                    RenderUtils.quickDrawRect(
                        -width - 2F,
                        -2F,
                        width + 4F,
                        fontRenderer.FONT_HEIGHT + 2F + if (healthBarValue.get()) 2F else 0F,
                        bgColor.rgb
                    )

                if (healthBarValue.get())
                    if (entity.health < 4096.0) {
                        RenderUtils.quickDrawRect(
                            -width - 2F,
                            fontRenderer.FONT_HEIGHT + 3F,
                            -width - 2F + (dist * (entity.health.toFloat() / entity.maxHealth.toFloat()).coerceIn(
                                0F,
                                1F
                            )),
                            fontRenderer.FONT_HEIGHT + 4F,
                            Color(0, 255, 0).rgb
                        )
                        if (entity.health < 15.0)
                            RenderUtils.quickDrawRect(
                                -width - 2F,
                                fontRenderer.FONT_HEIGHT + 3F,
                                -width - 2F + (dist * (entity.health.toFloat() / entity.maxHealth.toFloat()).coerceIn(
                                    0F,
                                    1F
                                )),
                                fontRenderer.FONT_HEIGHT + 4F,
                                Color(255, 255, 0).rgb
                            )

                        if (entity.health < 10.0)
                            RenderUtils.quickDrawRect(
                                -width - 2F,
                                fontRenderer.FONT_HEIGHT + 3F,
                                -width - 2F + (dist * (entity.health.toFloat() / entity.maxHealth.toFloat()).coerceIn(
                                    0F,
                                    1F
                                )),
                                fontRenderer.FONT_HEIGHT + 4F,
                                Color(255, 0, 0).rgb
                            )
                    }

                glEnable(GL_TEXTURE_2D)
                fontRenderer.drawString(
                    text,
                    1f + -width,
                    if (fontRenderer == Fonts.minecraftFont) 1F else 1.5F,
                    0xFFFFFF,
                    fontShadowValue.get()
                )


                //AWTFontRenderer.assumeNonVolatile = false

                var foundPotion = false
                if (potionValue.get() && entity is EntityPlayer) {
                    val potions =
                        (entity.getActivePotionEffects() as Collection<PotionEffect>).map { Potion.potionTypes[it.getPotionID()] }
                            .filter { it.hasStatusIcon() }
                    if (!potions.isEmpty()) {
                        foundPotion = true

                        color(1.0F, 1.0F, 1.0F, 1.0F)
                        disableLighting()
                        enableTexture2D()

                        val minX = (potions.size * -20) / 2

                        var index = 0

                        glPushMatrix()
                        enableRescaleNormal()
                        for (potion in potions) {
                            color(1.0F, 1.0F, 1.0F, 1.0F)
                            mc.getTextureManager().bindTexture(inventoryBackground)
                            val i1 = potion.getStatusIconIndex()
                            RenderUtils.drawTexturedModalRect(
                                minX + index * 20,
                                -22,
                                0 + i1 % 8 * 18,
                                198 + i1 / 8 * 18,
                                18,
                                18,
                                0F
                            )
                            index++
                        }
                        disableRescaleNormal()
                        glPopMatrix()

                        enableAlpha()
                        disableBlend()
                        enableTexture2D()
                    }
                }

                if (armorValue.get() && entity is EntityPlayer)
                    for (index in 0..4) {
                        if (entity.getEquipmentInSlot(index) == null)
                            continue

                        mc.renderItem.zLevel = -147F
                        mc.renderItem.renderItemAndEffectIntoGUI(
                            entity.getEquipmentInSlot(index),
                            -50 + index * 20,
                            if (potionValue.get() && foundPotion) -42 else -22
                        )
                    }

                enableAlpha()
                disableBlend()
                enableTexture2D()


                // Disable lightning and depth test
                glDisable(GL_LIGHTING)
                glDisable(GL_DEPTH_TEST)

                glEnable(GL_LINE_SMOOTH)

                // Enable blend
                glEnable(GL_BLEND)
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

                glPopMatrix()

                RenderHelper.disableStandardItemLighting();

                // Reset color
                resetColor()

                glColor4f(1F, 1F, 1F, 1F)
            }
        }
    }

    override fun onDisable() {
        modulestate = false
    }

    override fun onEnable() {
        modulestate = true
    }
    companion object {
        var modulestate = false
    }
}

