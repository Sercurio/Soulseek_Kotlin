package fr.sercurio.soulseek.entities

data class SoulFile(
    var path: String,
    val filename: String,
    val folderPath: String,
    val folder: String,
    var size: Long /*64*/,
    val extension: String,
    val bitrate: Int,
    val vbr: Int,
    val duration: Int
) {

    override fun toString(): String {
        return "SoulFile(path='$path', filename='$filename', folderPath='$folderPath', folder='$folder', size=$size, extension='$extension', bitrate=$bitrate, vbr=$vbr, duration=$duration)"
    }
}