package fr.sercurio.soulseek

sealed class Screens(val route: String) {
    data object Rooms : Screens("rooms_route")
    data object Search : Screens("search_route")
    data object Settings : Screens("settings_route")
}