package me.memorial.module

import me.memorial.Memorial
import me.memorial.events.Listenable
import me.memorial.module.modules.client.Notifications
import me.memorial.ui.font.Fonts
import me.memorial.utils.ClientUtils
import me.memorial.utils.MinecraftInstance
import me.memorial.utils.render.ColorUtils.stripColor
import me.memorial.value.Value
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.awt.Dimension
import java.awt.Toolkit

open class Module : MinecraftInstance(), Listenable {

    // Module information
    // TODO: Remove ModuleInfo and change to constructor (#Kotlin)
    val screenSize: Dimension? = Toolkit.getDefaultToolkit().screenSize

    var name: String
    var description: String
    var category: ModuleCategory
    var keyBind = Keyboard.CHAR_NONE
        set(keyBind) {
            field = keyBind

            if (!Memorial.isStarting)
                Memorial.fileManager.saveConfig(Memorial.fileManager.modulesConfig)
        }
    var array = true
        set(array) {
            field = array

            if (!Memorial.isStarting)
                Memorial.fileManager.saveConfig(Memorial.fileManager.modulesConfig)
        }
    private val canEnable: Boolean

    var slideStep = 0F

    init {
        val moduleInfo = javaClass.getAnnotation(ModuleInfo::class.java)!!

        name = moduleInfo.name
        description = moduleInfo.description
        category = moduleInfo.category
        keyBind = moduleInfo.keyBind
        array = moduleInfo.array
        canEnable = moduleInfo.canEnable
    }

    // Current state of module
    var state = false
        set(value) {
            if (field == value) return

            // Call toggle
            onToggle(value)


            ClientUtils.displayChatMessage("Module $name is currently ${if (value) "enabled" else "disabled"}.")

            // Call on enabled or disabled
            if (value) {
                onEnable()

                if (canEnable)
                    field = true
            } else {
                onDisable()
                field = false
            }

            // Save module state
            Memorial.fileManager.saveConfig(Memorial.fileManager.modulesConfig)
        }



    // Tag
    open val tag: String?
        get() = null

    val tagName: String
        get() = "$name${if (tag == null) "" else " §7$tag"}"

    /**
     * Toggle module
     */
    fun toggle() {
        state = !state
    }

    /**
     * Called when module toggled
     */
    open fun onToggle(state: Boolean) {
        val notifications = Memorial.moduleManager.getModule(Notifications::class.java) as Notifications

        notifications.add(
            "${
                if (state) "Enabled "
                else "Disabled "
            }$name",
            Color.BLACK,
            if (state) Color(0x60E092)
            else Color(0xFF2F2F),
            true
        )

    }

    /**
     * Called when module enabled
     */
    open fun onEnable() {}

    /**
     * Called when module disabled
     */
    open fun onDisable() {}

    /**
     * Get module by [valueName]
     */
    open fun getValue(valueName: String) = values.find { it.name.equals(valueName, ignoreCase = true) }

    /**
     * Get all values of module
     */
    open val values: List<Value<*>>
        get() = javaClass.declaredFields.map { valueField ->
            valueField.isAccessible = true
            valueField[this]
        }.filterIsInstance<Value<*>>()

    /**
     * Events should be handled when module is enabled
     */
    override fun handleEvents() = state
}