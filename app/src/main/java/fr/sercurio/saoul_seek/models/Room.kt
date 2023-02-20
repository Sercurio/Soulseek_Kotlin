package fr.sercurio.saoul_seek.models

data class Room(val name: String, val private: Boolean, val owner: Boolean, val operated: Boolean) {
    var nbUsers: Int = 0
}