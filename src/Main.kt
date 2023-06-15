import model.MassFunction
import model.PersentaseKemungkinan
import java.lang.Exception

fun main(args: Array<String>) {
    val gejala1 = MassFunction(listOf("P29", "P39"), 0.70)
    val gejala2 = MassFunction(listOf("P29", "P39"), 0.70)
    val gejala3 = MassFunction(listOf("P01", "P02", "P05", "P07", "P15", "P17", "P23", "P28", "P29", "P38", "P39"), 0.54)
    val gejala4 = MassFunction(listOf("P01", "P17", "P27", "P38", "P39"), 0.80)
    val gejala5 = MassFunction(listOf("P27", "P29", "P39"), 0.60)
    val gejala6 = MassFunction(listOf("P29", "P39"), 0.55)
    val gejala7 = MassFunction(listOf("P29", "P39"), 0.70)
    val mKombinasi = hitungMassFunctionKombinasi(listOf(gejala1, gejala2, gejala3, gejala4, gejala5, gejala6,gejala7))

    for (m in mKombinasi) {
        println("daftar penyakit: ${m.daftarPenyakit}, nilai: ${m.nilai}")
    }

    try {
        val persentaseKemungkinan = hitungPersentase(mKombinasi)
        print("jadi persentase penyakit ${persentaseKemungkinan.penyakit} adalah sebesar ${persentaseKemungkinan.persentase}%")
    } catch (e: Exception) {
        print("tidak dapat mencari")
    }
}

private fun hitungMassFunctionKombinasi(
    semuaGejala: List<MassFunction>
): List<MassFunction> {
    var massFunctionGejalaSebelumnya: MutableList<MassFunction> = mutableListOf(semuaGejala[0])
    for ((index, gejalaSelanjutnya) in semuaGejala.withIndex()) {
        if (index != 0) {
//            massFunctionGejalaSebelumnya.add()
            // aturan kombinasi
            massFunctionGejalaSebelumnya =
                himpunanSama(massFunctionGejalaSebelumnya, gejalaSelanjutnya)
        }
    }

    return massFunctionGejalaSebelumnya
}

private fun hitungPersentase(mKombinasi: List<MassFunction>): PersentaseKemungkinan {
    // filter MassFunction yang daftar penyakit 1 list saja
    val mYangDaftarPenyakitSatuList = mKombinasi.filter { it.daftarPenyakit.size == 1 }

    // cek berisi atau tidak
    if (mYangDaftarPenyakitSatuList.isEmpty()) {
        throw Exception("Tidak di temukan")
    } else {
        // cari nilai paling tinggi yang bukan θ
        mYangDaftarPenyakitSatuList.filter { it.daftarPenyakit.containsAll(listOf(Constant.TETHA)) }
        val dapat = mYangDaftarPenyakitSatuList.maxBy { it.nilai }
        return PersentaseKemungkinan(dapat!!.daftarPenyakit[0],dapat.nilai)
    }
}

private fun himpunanSama(
    semuaMassFunctionGejalaSebelumnya: List<MassFunction>,
    massFunctionGejalaBerikutnya: MassFunction
): MutableList<MassFunction> {
    var semuaMassFunctionBaru: MutableList<MassFunction> = mutableListOf()
//    val totalNilai = 0.0
    for (massFunction in semuaMassFunctionGejalaSebelumnya) {
        // kombinasi dilakukan pada m{daftar penyakit} berikutnya dan m{0} barikutnya tapi terbatas tidak adanya m{0} sebelumnya

        // kombinasi untuk yang m{daftar penyakit} berikutnya
        val massFunctionSementaraUntukMSebelumnya = MassFunction()
        var irisanDaftarPenyakit: List<String>? =
            massFunction.daftarPenyakit.filter { massFunctionGejalaBerikutnya.daftarPenyakit.contains(it) }
        if (irisanDaftarPenyakit!!.isEmpty() && !massFunction.daftarPenyakit.containsAll(listOf(Constant.TETHA))) {
            irisanDaftarPenyakit = listOf(Constant.HIMPUNAN_KOSONG)
        }
        if (massFunction.daftarPenyakit.containsAll(listOf(Constant.TETHA))) {
            irisanDaftarPenyakit = massFunctionGejalaBerikutnya.daftarPenyakit
        }

        massFunctionSementaraUntukMSebelumnya.daftarPenyakit = irisanDaftarPenyakit
        massFunctionSementaraUntukMSebelumnya.nilai = massFunction.nilai * massFunctionGejalaBerikutnya.nilai
        semuaMassFunctionBaru.add(massFunctionSementaraUntukMSebelumnya)

        // kombinasi untuk yang m{0} berikutnya
        val massFunctionSementaraUntukMθberikutnya =
            MassFunction(massFunction.daftarPenyakit, massFunction.nilai * massFunctionGejalaBerikutnya.nilaiFof)
//        massFunctionSementaraUntukM0Sebelumnya.daftarPenyakit = massFunction.daftarPenyakit
//        massFunctionSementaraUntukM0Sebelumnya.nilai = massFunction.nilaiFof * massFunctionGejalaBerikutnya.nilai
        semuaMassFunctionBaru.add(massFunctionSementaraUntukMθberikutnya)

    }

    // untuk kombinasi m{0} dengan m{daftar penyakit} berikutnya dan m{0} berikutnya


    val tetha = semuaMassFunctionBaru.filter { it.daftarPenyakit.contains(Constant.TETHA) } as MutableList<MassFunction>
    var nilaiMØ = 0.0
    if (tetha.size == 0) {
        // untuk m{daftar penyakit} berikutnya
        nilaiMØ = semuaMassFunctionGejalaSebelumnya[0].nilaiFof

        val hasilPerkalianNilaiM0DenganMassFunctionBaru = nilaiMØ * massFunctionGejalaBerikutnya.nilai
        semuaMassFunctionBaru.add(
            MassFunction(
                massFunctionGejalaBerikutnya.daftarPenyakit,
                hasilPerkalianNilaiM0DenganMassFunctionBaru
            )
        )

        // untuk m{0} berikutnya
        semuaMassFunctionBaru.add(MassFunction(listOf(Constant.TETHA), nilaiMØ * massFunctionGejalaBerikutnya.nilaiFof))
    }


    // perhitungan fungsi kombinasi
    semuaMassFunctionBaru = perhitunganFungsiKombinasi(semuaMassFunctionBaru)
    // hapus himpunan kosong
    semuaMassFunctionBaru =
        semuaMassFunctionBaru.filter { !it.daftarPenyakit.contains(Constant.HIMPUNAN_KOSONG) } as MutableList<MassFunction>


    return semuaMassFunctionBaru
}

fun perhitunganFungsiKombinasi(semuaMassFunctionBaru: MutableList<MassFunction>): MutableList<MassFunction> {
    val hasilPerhitunganFungsiKombinasi: MutableList<MassFunction> = mutableListOf()
    val daftarYangSudah: MutableList<List<String>> = mutableListOf()

    // fungsi ini mengkelompokkan yang sama atau sendiri
    val kelompokDaftarMassFunction: MutableList<List<MassFunction>> = mutableListOf()
    var saringan: List<MassFunction>
    for (m in semuaMassFunctionBaru) {
        saringan = semuaMassFunctionBaru.filter { it.daftarPenyakit == m.daftarPenyakit }
        val cekSudahAda = daftarYangSudah.filter { it.containsAll(m.daftarPenyakit) }

        if (saringan.isNotEmpty() && cekSudahAda.isEmpty()) {
            daftarYangSudah.add(m.daftarPenyakit)
            kelompokDaftarMassFunction.add(
                saringan
            )
        }
    }

    // cari himpunan kosong
    var totalNilaiHimpunanKosong = 0.0
    val daftarHimpunanKosong: List<MassFunction> =
        semuaMassFunctionBaru.filter { it.daftarPenyakit.contains(Constant.HIMPUNAN_KOSONG) }
    if (daftarHimpunanKosong.isNotEmpty()) {
        for (m in daftarHimpunanKosong) {
            totalNilaiHimpunanKosong += m.nilai
        }
    }

    // for in kelompok daftarMass function, masukkan kedalam rumus
    for (daftarMassFunction in kelompokDaftarMassFunction) {
        hasilPerhitunganFungsiKombinasi.add(rumusAturanKombinasi(daftarMassFunction, totalNilaiHimpunanKosong))
    }

    return hasilPerhitunganFungsiKombinasi
}

fun rumusAturanKombinasi(daftarMassFunction: List<MassFunction>, k: Double = 0.0): MassFunction {

    var jumlahNilai = 0.0
    for (m in daftarMassFunction) {
        jumlahNilai += m.nilai
    }
    val satuKurangK = 1 - k
    return MassFunction(daftarMassFunction[0].daftarPenyakit, (jumlahNilai / satuKurangK))
}
