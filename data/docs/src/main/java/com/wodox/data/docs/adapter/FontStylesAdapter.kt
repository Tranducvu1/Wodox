package com.wodox.data.docs.adapter

import com.google.gson.TypeAdapter
import com.wodox.domain.docs.model.TextFormat

class FontStylesAdapter : TypeAdapter<ArrayList<TextFormat.FontStyle>>() {
    override fun write(
        out: com.google.gson.stream.JsonWriter?,
        value: ArrayList<TextFormat.FontStyle>?
    ) {
        out?.beginArray()
        value?.forEach { team ->
            out?.value(team.value)
        }
        out?.endArray()
    }

    override fun read(`in`: com.google.gson.stream.JsonReader?): ArrayList<TextFormat.FontStyle>? {
        val list = ArrayList<TextFormat.FontStyle>()

        `in`?.beginArray()
        while (`in`?.hasNext() == true) {
            val value = `in`.nextString()
            val team = TextFormat.FontStyle.entries.firstOrNull { it.value == value }
            team?.let { list.add(it) }
        }
        `in`?.endArray()

        return list
    }
}