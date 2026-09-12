package com.kasimkoc.kasatakip

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : Activity() {

    private var nakit = 0.0
    private var banka = 0.0
    private var gider = 0.0
    private var baslangicKasa = 0.0
    private var borc = 0.0
    private var borcNot = ""

    private lateinit var nakitText: TextView
    private lateinit var bankaText: TextView
    private lateinit var giderText: TextView
    private lateinit var kasaText: TextView
    private lateinit var borcText: TextView
    private lateinit var toplamText: TextView
    private lateinit var tarihText: TextView

    private val prefs by lazy {
        getSharedPreferences("KasaTakip", MODE_PRIVATE)
    }

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(30, 30, 30, 30)

        val baslik = TextView(this)
        baslik.text = "KASA TAKİP"
        baslik.textSize = 28f
        baslik.setTextColor(Color.BLACK)
        baslik.gravity = Gravity.CENTER
        layout.addView(baslik)

        tarihText = TextView(this)
        tarihText.textSize = 20f
        tarihText.gravity = Gravity.CENTER
        tarihText.setPadding(10, 20, 10, 20)
        layout.addView(tarihText)

        val tarihButton = Button(this)
        tarihButton.text = "TARİH SEÇ"
        tarihButton.setOnClickListener {
            tarihSec()
        }
        layout.addView(tarihButton)

        val tarihNavi = LinearLayout(this)
        tarihNavi.orientation = LinearLayout.HORIZONTAL
        tarihNavi.gravity = Gravity.CENTER

        val oncekiButton = Button(this)
        oncekiButton.text = "ÖNCEKİ GÜN"
        oncekiButton.setOnClickListener {
            kaydet()
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            yukle()
        }

        val sonrakiButton = Button(this)
        sonrakiButton.text = "SONRAKİ GÜN"
        sonrakiButton.setOnClickListener {
            kaydet()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            yukle()
        }

        tarihNavi.addView(oncekiButton)
        tarihNavi.addView(sonrakiButton)
        layout.addView(tarihNavi)

        nakitText = bilgi("Elden Alınan: 0,00 €")
        bankaText = bilgi("Banka Havalesi: 0,00 €")
        giderText = bilgi("Giderler: 0,00 €")
        kasaText = bilgi("Kasada Olması Gereken: 0,00 €")
        borcText = bilgi("Borç / Veresiye: 0,00 €")
        toplamText = bilgi("Toplam Para: 0,00 €")

        layout.addView(nakitText)
        layout.addView(bankaText)
        layout.addView(giderText)
        layout.addView(kasaText)
        layout.addView(borcText)
        layout.addView(toplamText)

        val nakitButton = Button(this)
        nakitButton.text = "+ ELDEN ALINAN"
        nakitButton.setOnClickListener {
            paraGir("Elden Alınan") {
                nakit += it
                kaydet()
                guncelle()
            }
        }
        layout.addView(nakitButton)

        val bankaButton = Button(this)
        bankaButton.text = "+ BANKA HAVALESİ"
        bankaButton.setOnClickListener {
            paraGir("Banka Havalesi") {
                banka += it
                kaydet()
                guncelle()
            }
        }
        layout.addView(bankaButton)

        val giderButton = Button(this)
        giderButton.text = "+ GİDER"
        giderButton.setOnClickListener {
            paraGir("Gider") {
                gider += it
                kaydet()
                guncelle()
            }
        }
        layout.addView(giderButton)

        val baslangicButton = Button(this)
        baslangicButton.text = "BAŞLANGIÇ KASASI"
        baslangicButton.setOnClickListener {
            paraGir("Başlangıç Kasası") {
                baslangicKasa = it
                kaydet()
                guncelle()
            }
        }
        layout.addView(baslangicButton)

        val borcButton = Button(this)
        borcButton.text = "+ BORÇ / VERESİYE"
        borcButton.setOnClickListener {
            borcGir()
        }
        layout.addView(borcButton)

        val sifirlaButton = Button(this)
        sifirlaButton.text = "GÜNÜ SIFIRLA"
        sifirlaButton.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Günü Sıfırla")
                .setMessage("Seçili günün tüm kayıtları silinsin mi?")
                .setPositiveButton("Evet") { _, _ ->

                    nakit = 0.0
                    banka = 0.0
                    gider = 0.0
                    baslangicKasa = 0.0
                    borc = 0.0
                    borcNot = ""

                    val key = tarihAnahtari()

                    prefs.edit()
                        .remove(key + "_nakit")
                        .remove(key + "_banka")
                        .remove(key + "_gider")
                        .remove(key + "_baslangic")
                        .remove(key + "_borc")
                        .remove(key + "_borcNot")
                        .apply()

                    guncelle()
                }
                .setNegativeButton("İptal", null)
                .show()
        }

        layout.addView(sifirlaButton)

        setContentView(layout)

        yukle()
    }

    private fun bilgi(metin: String): TextView {
        val text = TextView(this)
        text.text = metin
        text.textSize = 18f
        text.setTextColor(Color.BLACK)
        text.setPadding(5, 10, 5, 10)
        return text
    }

    private fun paraGir(
        baslik: String,
        sonuc: (Double) -> Unit
    ) {

        val input = EditText(this)
        input.hint = "Tutar (€)"

        AlertDialog.Builder(this)
            .setTitle(baslik)
            .setView(input)
            .setPositiveButton("Ekle") { _, _ ->

                val miktar = input.text.toString()
                    .replace(",", ".")
                    .toDoubleOrNull()

                if (miktar != null) {
                    sonuc(miktar)
                } else {
                    Toast.makeText(
                        this,
                        "Geçerli bir tutar girin",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun borcGir() {

        val borcLayout = LinearLayout(this)
        borcLayout.orientation = LinearLayout.VERTICAL

        val tutarInput = EditText(this)
        tutarInput.hint = "Tutar (€)"

        val notInput = EditText(this)
        notInput.hint = "Kime / Not"

        borcLayout.addView(tutarInput)
        borcLayout.addView(notInput)

        AlertDialog.Builder(this)
            .setTitle("Borç / Veresiye")
            .setView(borcLayout)
            .setPositiveButton("Kaydet") { _, _ ->

                val miktar = tutarInput.text.toString()
                    .replace(",", ".")
                    .toDoubleOrNull()

                val not = notInput.text.toString().trim()

                if (miktar != null) {

                    borc += miktar

                    if (not.isNotEmpty()) {
                        if (borcNot.isNotEmpty()) {
                            borcNot += "\n"
                        }

                        borcNot += not + " - " + format(miktar) + " €"
                    }

                    kaydet()
                    guncelle()

                } else {

                    Toast.makeText(
                        this,
                        "Geçerli bir tutar girin",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    private fun tarihSec() {

        DatePickerDialog(
            this,
            { _, year, month, day ->

                kaydet()

                calendar.set(
                    year,
                    month,
                    day
                )

                yukle()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun tarihAnahtari(): String {
        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(calendar.time)
    }

    private fun tarihYazisi(): String {
        return SimpleDateFormat(
            "dd.MM.yyyy",
            Locale.getDefault()
        ).format(calendar.time)
    }

    private fun kaydet() {

        val key = tarihAnahtari()

        prefs.edit()
            .putFloat(
                key + "_nakit",
                nakit.toFloat()
            )
            .putFloat(
                key + "_banka",
                banka.toFloat()
            )
            .putFloat(
                key + "_gider",
                gider.toFloat()
            )
            .putFloat(
                key + "_baslangic",
                baslangicKasa.toFloat()
            )
            .putFloat(
                key + "_borc",
                borc.toFloat()
            )
            .putString(
                key + "_borcNot",
                borcNot
            )
            .apply()
    }

    private fun yukle() {

        val key = tarihAnahtari()

        nakit = prefs.getFloat(
            key + "_nakit",
            0f
        ).toDouble()

        banka = prefs.getFloat(
            key + "_banka",
            0f
        ).toDouble()

        gider = prefs.getFloat(
            key + "_gider",
            0f
        ).toDouble()

        baslangicKasa = prefs.getFloat(
            key + "_baslangic",
            0f
        ).toDouble()

        borc = prefs.getFloat(
            key + "_borc",
            0f
        ).toDouble()

        borcNot = prefs.getString(
            key + "_borcNot",
            ""
        ) ?: ""

        guncelle()
    }

    private fun guncelle() {

        val kasa = baslangicKasa + nakit - gider

        val toplam = nakit + banka + borc

        tarihText.text =
            "Tarih: ${tarihYazisi()}"

        nakitText.text =
            "Elden Alınan: ${format(nakit)} €"

        bankaText.text =
            "Banka Havalesi: ${format(banka)} €"

        giderText.text =
            "Giderler: ${format(gider)} €"

        kasaText.text =
            "Kasada Olması Gereken: ${format(kasa)} €"

        borcText.text =
            "Borç / Veresiye: ${format(borc)} €"

        if (borcNot.isNotEmpty()) {
            borcText.text = borcText.text.toString() + "\nNot: " + borcNot

        toplamText.text =
            "Toplam Para: ${format(toplam)} €"
    }

    private fun format(tutar: Double): String {

        return String.format(
            Locale.GERMANY,
            "%.2f",
            tutar
        )
    }
}
