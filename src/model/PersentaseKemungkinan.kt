package model

data class PersentaseKemungkinan(
    val penyakit: String,
    val m: Double
) {
    val persentase = m * 100
}