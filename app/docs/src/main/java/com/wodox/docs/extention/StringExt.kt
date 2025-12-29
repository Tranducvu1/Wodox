package com.wodox.docs.extention

private fun convertHexColorString(color: Int): String {
    return String.format("#%06X", (0xFFFFFF and color))
}