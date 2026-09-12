package com.kasimkoc.kasatakip

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : Activity() {

    private var nakit = 0.0
    private var banka = 0.0
    private var gider = 0.0
    private var baslangicKasa = 0.0

    private lateinit var nakitText: TextView
    private lateinit var bankaText: TextView
    private lateinit var giderText: TextView
    private lateinit var kasaText: TextView
    private lateinit var toplamText: TextView
    private lateinit var tarihText: TextView

    private lateinit var prefs: android.content.SharedPreferences

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("KasaTakip", MODE_PRIVATE)

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

        val tarihButon = Button(this)
        tarihButon.text = "📅 TARİH SEÇ"
        tarihButon.setOnClickListener {
            tarihSec()
        }
        layout.addView(tarihButon)

        val tarihNavi = LinearLayout(this)
        tarihNavi.orientation = LinearLayout.HORIZONTAL
        tarihNavi.gravity = Gravity.CENTER

        val oncekiButton = Button(this)
        oncekiButton.text = "◀️ ÖNCEKİ GÜN"
        oncekiButton.setOnClickListener {
            kaydet()
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            yukle()
        }

        val sonrakiButton = Button(this)
        sonrakiButton.text = "SONRAKİ GÜN ▶️"
        sonrakiButton.setOnClickListener {
            kaydet()
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            yukle()
        }

        tarihNavi.addView(oncekiButton)
        tarihNavi.addView(sonrakiButton)
        layout.addView(tarihNavi)

        nakitText = bilgi("💵 Elden Alınan: 0,00 €")
        bankaText = bilgi("🏦 Banka Havalesi: 0,00 €")
        giderText = bilgi("💸 Giderler: 0,00 €")
        kasaText = bilgi("💰 Kasada Olması Gereken: 0,00 €")
        toplamText = bilgi("📊 Toplam Para: 0,00 €")

        layout.addView(nakitText)
        layout.addView(bankaText)
        layout.addView(giderText)
        layout.addView(kasaText)
        layout.addView(toplamText)

        val nakitButton = Button(this)
        nakitButton.text = "+ ELDEN ALINAN"
        nakitButton.setOnClickListener {
            paraGir("Elden Alınan") {
                nakit += it
            }
        }
        layout.addView(nakitButton)

        val bankaButton = Button(this)
        bankaButton.text = "+ BANKA HAVALESİ"
        bankaButton.setOnClickListener {
            paraGir("Banka Havalesi") {
                banka += it
            }
        }
        layout.addView(bankaButton)

        val giderButton = Button(this)
        giderButton.text = "+ GİDER"
        giderButton.setOnClickListener {
            paraGir("Gider") {
                gider += it
            }
        }
        layout.addView(giderButton)

        val baslangicButton = Button(this)
        baslangicButton.text = "BAŞLANGIÇ KASASI"
        baslangicButton.setOnClickListener {
            paraGir("Başlangıç Kasası") {
                baslangicKasa = it
            }
        }
        layout.addView(baslangicButton)

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

                    kaydet()
                    guncelle()
                }
                .setNegativeButton("İptal", null)
                .show()
        }
        layout.addView(sifirlaButton)

        setContentView(layout)

        yukle()
    }
    private fun tarihSec() {
    DatePickerDialog(
        this,
        { _, year, month, dayOfMonth ->
            kaydet()
            calendar.set(year, month, dayOfMonth)
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
           
