package model

data class MassFunction(
    var daftarPenyakit: List<String> = emptyList(),
    var nilai: Double = 0.0
    ) {
    val nilaiFof = 1 - nilai
}