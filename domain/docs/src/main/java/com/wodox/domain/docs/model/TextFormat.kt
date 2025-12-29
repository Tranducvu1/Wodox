package com.wodox.domain.docs.model


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Parcelable
import android.util.Log
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.wodox.core.data.model.Diffable
import com.wodox.core.data.model.Selectable
import com.wodox.core.extension.color
import com.wodox.core.extension.getColorFromAttr
import com.wodox.core.extension.toArrayList
import com.wodox.domain.docs.model.adapter.FontStylesAdapter
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import org.w3c.dom.Text

@SuppressLint("UnsafeOptInUsageError")
@Serializable
@Parcelize
class TextFormat(
    @SerializedName("fontName")
    var fontName: String = "",
    @SerializedName("textSize")
    var textSize: Float = 16f,
    @SerializedName("color")
    var rawColor: String? = null,
    @SerializedName("rawHighLightColor")
    var rawHighLightColor: String? = null,
    @field:JsonAdapter(FontStylesAdapter::class)
    @SerializedName("fontStyles")
    var fontStyles: ArrayList<FontStyle> = ArrayList(),
    @SerializedName("bulletType")
    var bulletType: BulletType = BulletType.NONE,
    override var isSelected: Boolean = false,
    var isPremium: Boolean = true,
) : Selectable, Parcelable, Diffable {

    enum class FontStyle(val value: String) {
        BOLD("bold"),
        ITALIC("italic"),
        UNDERLINE("underline"),
        STRIKE_THROUGH("strike_through")
    }

    fun getFontStyle(): Int {
        if (isBold && isItalic) {
            return Typeface.BOLD_ITALIC
        }

        if (isBold) {
            return Typeface.BOLD
        }

        if (isItalic) {
            return Typeface.ITALIC
        }

        return Typeface.NORMAL
    }


    @IgnoredOnParcel
    var componentToChange: ChangeComponent? = null

    enum class ChangeComponent {
        FONT_STYLE, PAINT_FLAG, FONT, TEXT_SIZE, TEXT_COLOR, HIGHLIGHT_COLOR, BULLET, BULLET_NUMERIC
    }

    @Serializable
    enum class BulletType(value: String) {
        @SerializedName("none")
        NONE("none"),

        @SerializedName("numeric")
        BULLET_NUMERIC("numeric"),

        @SerializedName("bullet")
        BULLET("bullet")
    }

    val isBold: Boolean
        get() = fontStyles.contains(FontStyle.BOLD)

    val isItalic: Boolean
        get() = fontStyles.contains(FontStyle.ITALIC)

    val isUnderline: Boolean
        get() = fontStyles.contains(FontStyle.UNDERLINE)

    val isStrikeThough: Boolean
        get() = fontStyles.contains(FontStyle.STRIKE_THROUGH)

    companion object {
        fun getDefaults(): ArrayList<TextFormat> {
            return arrayListOf(
                TextFormat("sans-serif"),
                TextFormat("sans-serif-light"),
                TextFormat("sans-serif-thin"),
                TextFormat("sans-serif-medium"),
                TextFormat("sans-serif-black"),
                TextFormat("sans-serif-condensed"),
                TextFormat("serif"),
                TextFormat("monospace"),
                TextFormat("cursive"),
                TextFormat("casual"),
                TextFormat("sans-serif-smallcaps"),
                TextFormat("sans-serif-condensed-light"),
                TextFormat("sans-serif-condensed-medium"),
                TextFormat("Roboto"),
                TextFormat("Droid Sans"),
                TextFormat("Droid Serif"),
                TextFormat("Droid Sans Mono"),
                TextFormat("Homemade Apple Regular"),
                TextFormat("Pacifico Regular"),
                TextFormat("Lobster Two Italic"),
                TextFormat("Dancing Script Regular"),
                TextFormat("Permanent Marker Regular"),
                TextFormat("Indie Flower Regular"),
                TextFormat("Patrick Hand Regular"),
                TextFormat("Fredoka One Regular"),
                TextFormat("Righteous Regular"),
                TextFormat("Kalam Regular"),
                TextFormat("Yellowtail Regular"),
                TextFormat("Fugaz One Regular"),
                TextFormat("Sriracha Regular"),
                TextFormat("Coiny Regular")
            )
        }


        val fontNameFrees = listOf(
            "nunito",
            "inria_serif_light",
            "just_another_hand_regular",
            "bebas_neue_regular",
            "anton_regular",
            "lobster_two_italic"
        )
    }

    fun typeface(context: Context): Typeface? {
        if (fontName.isEmpty()) {
            return null
        }

        return TypeFaces.getTypeFace(context, "fonts/$fontName.ttf")
    }

    fun getColor(context: Context): Int {
        if (rawColor.isNullOrEmpty()) {
            return context.getColorFromAttr(com.wodox.resources.R.attr.titleTextColor)
        }

        return rawColor?.color ?: context.getColorFromAttr(com.wodox.resources.R.attr.titleTextColor)
    }

    val highLightColor: Int?
        get() {
            if (rawHighLightColor.isNullOrEmpty()) {
                return null
            }
            return rawHighLightColor?.color
        }

    fun copy(): TextFormat {
        return TextFormat(
            fontName,
            textSize,
            rawColor,
            rawHighLightColor,
            fontStyles.map { it }.toArrayList(),
            bulletType,
        )
    }

    fun compareDescription(): String {
        val compareString = arrayListOf(
            fontName,
            textSize,
            rawColor,
            rawHighLightColor,
            bulletType,
            fontStyles.joinToString { "," }
        ).joinToString("-")

        Log.d("compareString", compareString)

        return compareString
    }

    override fun areContentsTheSame(data: Diffable, payload: String?): Boolean {
        val textFormat = data as? TextFormat ?: return false
        return textFormat.isSelected == isSelected && textFormat.isPremium == isPremium
    }
}