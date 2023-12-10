package dev.nelly.skin

import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelPlayer
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

object SkinManager {

    fun setPlayerSkin(player: EntityPlayer?, skin: ResourceLocation?) {
        val renderManager = Minecraft.getMinecraft().renderManager
        val model = ModelPlayer(0.0f, false)

        // 替换玩家的皮肤贴图
        model.textureWidth = 64
        model.textureHeight = 32
        model.bipedHead.showModel = true
        model.bipedHeadwear.showModel = true
        model.bipedBody.showModel = true
        model.bipedRightArm.showModel = true
        model.bipedLeftArm.showModel = true
        model.bipedRightLeg.showModel = true
        model.bipedLeftLeg.showModel = true
        model.isChild = false
        renderManager.renderEngine.bindTexture(skin)

        // 渲染自定义的玩家模型
        model.render(player, 0f, 0f, 0f, 0f, 0f, 0.0625f)
    }

    fun setThePlayerskin(skin: ResourceLocation?) {
        setPlayerSkin(Minecraft.getMinecraft().thePlayer, skin)
    }

}
