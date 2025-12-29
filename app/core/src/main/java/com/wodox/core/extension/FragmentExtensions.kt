package com.wodox.core.extension

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.io.Serializable

inline fun <reified T : Fragment> FragmentActivity.openFragment(
    containerId: Int,
    addToBackStack: Boolean = true,
    tag: String? = null,
    vararg args: Pair<String, Any?>) {
    val fragment = T::class.java.newInstance()

    if (args.isNotEmpty()) {
        val bundle = Bundle()
        args.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                is Float -> bundle.putFloat(key, value)
                is Double -> bundle.putDouble(key, value)
                is ArrayList<*> -> {
                    when {
                        value.firstOrNull() is String ->
                            bundle.putStringArrayList(key, value as ArrayList<String>)
                        value.firstOrNull() is Int ->
                            bundle.putIntegerArrayList(key, value as ArrayList<Int>)
                    }
                }
                is Array<*> -> {
                    when {
                        value.firstOrNull() is String ->
                            bundle.putStringArray(key, value as Array<String>)
                    }
                }
                else -> bundle.putSerializable(key, value as? Serializable)
            }
        }
        fragment.arguments = bundle
    }

    supportFragmentManager.beginTransaction().apply {
        replace(containerId, fragment, tag)
        if (addToBackStack) {
            addToBackStack(tag ?: T::class.java.simpleName)
        }
        commit()
    }
}


inline fun <reified T : Fragment> Fragment.openFragment(
    containerId: Int,
    addToBackStack: Boolean = true,
    tag: String? = null,
    vararg args: Pair<String, Any?>
) {
    val fragment = T::class.java.newInstance()

    if (args.isNotEmpty()) {
        val bundle = Bundle()
        args.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                is Float -> bundle.putFloat(key, value)
                is Double -> bundle.putDouble(key, value)
                is ArrayList<*> -> {
                    when {
                        value.firstOrNull() is String ->
                            bundle.putStringArrayList(key, value as ArrayList<String>)
                        value.firstOrNull() is Int ->
                            bundle.putIntegerArrayList(key, value as ArrayList<Int>)
                    }
                }
                else -> bundle.putSerializable(key, value as? Serializable)
            }
        }
        fragment.arguments = bundle
    }

    childFragmentManager.beginTransaction().apply {
        replace(containerId, fragment, tag)
        if (addToBackStack) {
            addToBackStack(tag ?: T::class.java.simpleName)
        }
        commit()
    }
}

inline fun <reified T : Fragment> FragmentActivity.addFragment(
    containerId: Int,
    addToBackStack: Boolean = true,
    tag: String? = null,
    vararg args: Pair<String, Any?>) {
    val fragment = T::class.java.newInstance()

    if (args.isNotEmpty()) {
        val bundle = Bundle()
        args.forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Boolean -> bundle.putBoolean(key, value)
                else -> bundle.putSerializable(key, value as? Serializable)
            }
        }
        fragment.arguments = bundle
    }

    supportFragmentManager.beginTransaction().apply {
        add(containerId, fragment, tag)
        if (addToBackStack) {
            addToBackStack(tag ?: T::class.java.simpleName)
        }
        commit()
    }
}