package me.memorial.value

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import me.memorial.Memorial
import me.memorial.ui.font.Fonts
import me.memorial.utils.ClientUtils
import net.minecraft.client.gui.FontRenderer
import java.util.*

abstract class Value<T>(val name: String, protected var value: T, var canDisplay: () -> Boolean) {

    val displayable: Boolean
        get() = displayableFunc()

    private var displayableFunc: () -> Boolean = { true }
    var textHovered: Boolean = false

    fun set(newValue: T) {
        if (newValue == value) return

        val oldValue = get()

        try {
            onChange(oldValue, newValue)
            changeValue(newValue)
            onChanged(oldValue, newValue)
            Memorial.fileManager.saveConfig(Memorial.fileManager.valuesConfig)
        } catch (e: Exception) {
            ClientUtils.getLogger().error("[ValueSystem ($name)]: ${e.javaClass.name} (${e.message}) [$oldValue >> $newValue]")
        }
    }

    fun get() = value

    open fun changeValue(value: T) {
        this.value = value
    }

    abstract fun toJson(): JsonElement?
    abstract fun fromJson(element: JsonElement)

    protected open fun onChange(oldValue: T, newValue: T) {}
    protected open fun onChanged(oldValue: T, newValue: T) {}

}

/**
 * Bool value represents a value with a boolean
 */
open class BoolValue(name: String, value: Boolean, displayable: () -> Boolean) : Value<Boolean>(name, value,displayable) {
    constructor(name: String, value: Boolean): this(name, value, { true } )
    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asBoolean || element.asString.equals("true", ignoreCase = true)
    }

}

/**
 * Integer value represents a value with a integer
 */
open class IntegerValue(name: String, value: Int, val minimum: Int = 0, val maximum: Int = Integer.MAX_VALUE, displayable: () -> Boolean)
    : Value<Int>(name, value,displayable) {
    constructor(name: String, value: Int, minimum: Int, maximum: Int): this(name, value, minimum, maximum, { true } )
    fun set(newValue: Number) {
        set(newValue.toInt())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asInt
    }

}

/**
 * Float value represents a value with a float
 */
open class FloatValue(name: String, value: Float, val minimum: Float = 0F, val maximum: Float = Float.MAX_VALUE, displayable: () -> Boolean)
    : Value<Float>(name, value,displayable) {
    constructor(name: String, value: Float, minimum: Float, maximum: Float): this(name, value, minimum, maximum, { true } )
    fun set(newValue: Number) {
        set(newValue.toFloat())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asFloat
    }

}

/**
 * Double value represents a value with a float
 */
open class DoubleValue(name: String, value: Double, val minimum: Double = 0.0, val maximum: Double = Double.MAX_VALUE, displayable: () -> Boolean)
    : Value<Double>(name, value,displayable) {
    constructor(name: String, value: Double, minimum: Double, maximum: Double): this(name, value, minimum, maximum, { true } )

    fun set(newValue: Number) {
        set(newValue.toFloat())
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asFloat.toDouble()
    }

}

/**
 * Text value represents a value with a string
 */
open class TextValue(name: String, value: String, displayable: () -> Boolean) : Value<String>(name, value,displayable) {
    constructor(name: String, value: String): this(name, value, { true } )
    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive)
            value = element.asString
    }
    fun append(o: Any): TextValue {
        set(get() + o)
        return this
    }
}

/**
 * Font value represents a value with a font
 */
open class FontValue(valueName: String, value: FontRenderer, displayable: () -> Boolean) : Value<FontRenderer>(valueName, value,displayable) {

    constructor(valueName: String, value: FontRenderer): this(valueName, value, { true } )
    override fun toJson(): JsonElement? {
        val fontDetails = Fonts.getFontDetails(value) ?: return null
        val valueObject = JsonObject()
        valueObject.addProperty("fontName", fontDetails[0] as String)
        valueObject.addProperty("fontSize", fontDetails[1] as Int)
        return valueObject
    }

    override fun fromJson(element: JsonElement) {
        if (!element.isJsonObject) return
        val valueObject = element.asJsonObject
        value = Fonts.getFontRenderer(valueObject["fontName"].asString, valueObject["fontSize"].asInt)
    }
}

/**
 * Block value represents a value with a block
 */
class BlockValue(name: String, value: Int, displayable: () -> Boolean) : IntegerValue(name, value, 1, 197,displayable) {
    constructor(name: String, value: Int) : this(name, value, { true })
}
/**
 * List value represents a selectable list of values
 */
open class ListValue(name: String, val values: Array<String>, value: String, displayable: () -> Boolean) : Value<String>(name, value,displayable) {
    constructor(name: String, values: Array<String>, value: String): this(name, values, value, { true } )
    @JvmField
    var openList = false

    init {
        this.value = value
    }
    fun getModeListNumber(mode: String) = value.indexOf(mode)

    operator fun contains(string: String?): Boolean {
        return Arrays.stream(values).anyMatch { s: String -> s.equals(string, ignoreCase = true) }
    }

    override fun changeValue(value: String) {
        for (element in values) {
            if (element.equals(value, ignoreCase = true)) {
                this.value = element
                break
            }
        }
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) changeValue(element.asString)
    }


}