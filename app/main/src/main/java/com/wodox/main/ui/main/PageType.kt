package com.wodox.main.ui.main

enum class PageType(val index: Int, val group: Group) {
    HOME(0, Group.BOTTOM),
    ACTIVITY(1, Group.BOTTOM),
    CREATE(2, Group.BOTTOM),
    MY_WORK(3, Group.BOTTOM),

    // TopBar
    RECENT(4, Group.TOP),
    FAVOURITE(5, Group.TOP),
    ANALYST(6, Group.TOP),
    DOCS(7, Group.TOP),
    CHAT(8, Group.TOP),
    SPACE_BUILDING(9, Group.TOP);

    enum class Group { TOP, BOTTOM }

    companion object {
        fun fromIndex(index: Int): PageType? {
            return PageType.entries.find { it.index == index }
        }
    }
}
