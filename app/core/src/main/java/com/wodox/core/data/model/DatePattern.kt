package com.wodox.core.data.model

enum class DatePattern(val pattern: String) {
    YYYY_DD_MM("yyyy-MM-dd"),
    DD_MM_YYYY("dd-MM-yyyy"),
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    HH_MM_SS("HH:mm:ss"),
    DD_MMM_YYYY("dd MMM yyyy"),
    MMMM_DD_YYYY("MMMM dd, yyyy")
}