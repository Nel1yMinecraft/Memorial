package dev.nelly

import me.memorial.utils.misc.HttpUtils.get

object RegionUtils {

    /**
     * 获取 IPv4
     */
    fun getIPv4(): String {
        return get("https://api.ipify.org/?format=txt")
    }

    /**
     * 获取 IPv4 地址信息
     */
    fun getIPv4Info(ip: String): String? {
        val result = get("https://api.vore.top/api/IPdata?ip=$ip")
        return result?.let {
            val text = getValueByKey(result, "\"text\":")
            text.substring(1, text.length - 2)
        }
    }

    /**
     * 获取省份
     */
    fun getProvinceByIPv4(ip: String): String? {
        val result = get("https://api.vore.top/api/IPdata?ip=$ip")
        return result?.let {
            val info1 = getValueByKey(result, "\"info1\":")
            info1.substring(1, info1.length - 2)
        }
    }

    /**
     * 获取城市
     */
    fun getCityByIPv4(ip: String): String? {
        val result = get("https://api.vore.top/api/IPdata?ip=$ip")
        return result?.let {
            val info2 = getValueByKey(result, "\"info2\":")
            info2.substring(1, info2.length - 2)
        }
    }

    /**
     * 根据key获取value值
     */
    private fun getValueByKey(content: String, key: String): String {
        val start = content.indexOf(key)
        var end = content.indexOf(",", start)
        if (end == -1) {
            end = content.indexOf("}", start)
        }
        return content.substring(start, end).replace(key, "")
    }
}
