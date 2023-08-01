package com.clovero.mmkv

import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface MMKVOwner {
    val mmkv: MMKV
}

open class DefaultMMKVOwner(override val mmkv: MMKV) : MMKVOwner

fun MMKV.toMMKVOwner(): MMKVOwner {
    return DefaultMMKVOwner(this)
}

fun MMKVOwner.boolValue(default: Boolean = false, key: String? = null): MMKVProperty<Boolean> {
    return MMKVProperty({ mmkv.decodeBool(it, default) }, mmkv::encode, key)
}

fun MMKVOwner.intValue(default: Int = 0, key: String? = null): MMKVProperty<Int> {
    return MMKVProperty({ mmkv.decodeInt(it, default) }, mmkv::encode, key)
}

fun MMKVOwner.longValue(default: Long = 0L, key: String? = null): MMKVProperty<Long> {
    return MMKVProperty({ mmkv.decodeLong(it, default) }, mmkv::encode, key)
}

fun MMKVOwner.floatValue(default: Float = 0f, key: String? = null): MMKVProperty<Float> {
    return MMKVProperty({ mmkv.decodeFloat(it, default) }, mmkv::encode, key)
}

fun MMKVOwner.doubleValue(default: Double = 0.0, key: String? = null): MMKVProperty<Double> {
    return MMKVProperty({ mmkv.decodeDouble(it, default) }, mmkv::encode, key)
}

fun MMKVOwner.bytesValue(key: String? = null): MMKVProperty<ByteArray?> {
    return MMKVProperty(mmkv::decodeBytes, mmkv::encode, key)
}

fun MMKVOwner.bytesValue(default: ByteArray, key: String? = null): MMKVProperty<ByteArray> {
    return MMKVProperty({ mmkv.decodeBytes(it) ?: default }, mmkv::encode, key)
}

fun MMKVOwner.stringValue(key: String? = null): MMKVProperty<String?> {
    return MMKVProperty(mmkv::decodeString, mmkv::encode, key)
}

fun MMKVOwner.stringValue(default: String, key: String? = null): MMKVProperty<String?> {
    return MMKVProperty({ mmkv.decodeString(it) ?: default }, mmkv::encode, key)
}

fun MMKVOwner.stringSetValue(key: String? = null): MMKVProperty<Set<String>?> {
    return MMKVProperty(mmkv::decodeStringSet, mmkv::encode, key)
}

fun MMKVOwner.stringSetValue(default: String, key: String? = null): MMKVProperty<String?> {
    return MMKVProperty({ mmkv.decodeString(it) ?: default }, mmkv::encode, key)
}

private class MMKVStateFlowProperty<T>(
    private val mmkvProperty: MMKVProperty<T>,
) : ReadOnlyProperty<MMKVOwner, MutableStateFlow<T>> {
    private lateinit var _value: MutableStateFlow<T>

    private var lock = this

    override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): MutableStateFlow<T> {
        if (::_value.isInitialized) {
            return _value
        }
        return synchronized(lock) {
            if (::_value.isInitialized) {
                _value
            } else {
                val v = createMutableStateFlow(thisRef, property)
                _value = v
                v
            }
        }
    }

    private fun createMutableStateFlow(
        thisRef: MMKVOwner,
        property: KProperty<*>
    ): MutableStateFlow<T> {
        val state = MutableStateFlow(mmkvProperty.getValue(thisRef, property))
        return object : MutableStateFlow<T> by state {
            override var value: T
                get() = mmkvProperty.getValue(thisRef, property)
                set(value) {
                    mmkvProperty.setValue(thisRef, property, value)
                    state.value = value
                }
        }
    }
}

fun <T> MMKVProperty<T>.asMutableStateFlow(): ReadOnlyProperty<MMKVOwner, MutableStateFlow<T>> {
    return MMKVStateFlowProperty(this)
}

class MMKVProperty<V>(
    private val decode: (String) -> V,
    private val encode: (String, V) -> Boolean,
    private var key: String? = null
) : ReadWriteProperty<MMKVOwner, V> {

    override fun getValue(thisRef: MMKVOwner, property: KProperty<*>): V =
        decode(key ?: property.name)

    override fun setValue(thisRef: MMKVOwner, property: KProperty<*>, value: V) {
        encode((key ?: property.name), value)
    }
}