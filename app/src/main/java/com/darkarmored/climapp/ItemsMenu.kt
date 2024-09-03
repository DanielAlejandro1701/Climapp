package com.darkarmored.climapp

sealed class ItemsMenu (
    val icon: Int,
    val title: String,
    val ruta: String
) {
    object SearchIcon: ItemsMenu(R.drawable.icon_search, "SearchIcon", "SearchIcon")
}

