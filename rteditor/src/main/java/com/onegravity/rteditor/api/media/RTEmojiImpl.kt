package com.onegravity.rteditor.api.media

class RTEmojiImpl(
    val filename: String,
    private val scaledWidth: Int,
    private val scaledHeight: Int
) : RTMediaImpl("file:///android_asset/emojis/animal/ic_animal_1.png"), RTImage {

    override fun getFileName(): String {
        return filename
    }

    override fun getHeight(): Int {
        return 100
    }

    override fun getWidth(): Int {
        return 100
    }

    companion object {
        private const val serialVersionUID = -1639564429914951720L

//        fun getEmojiFilePath(filename: String): String {
//            val filesDir = RTApi.getApplicationContext().filesDir
//            return File(filesDir, "hehe/$filename").absolutePath
//        }
    }
}