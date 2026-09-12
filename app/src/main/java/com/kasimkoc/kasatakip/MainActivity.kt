package com.kasimkoc.kasatakip

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*

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

        val tarih = TextView(this)
        tarih.text = "Günlük Kasa"
        tarih.textSize = 18f
        tarih.gravity = Gravity.CENTER
        layout.addView(tarih)

        nakitText = bilgi("💶 Elden Alınan: 0,00 €")
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
        paraGir("Elden Alınan") { nakit += it }
}
layout.addView(nakitButton)

val bankaButton = Button(this)
bankaButton.text = "+ BANKA HAVALESİ"
bankaButton.setOnClickListener {
    paraGir("Banka Havalesi") { banka += it }
}
layout.addView(bankaButton)

val giderButton = Button(this)
giderButton.text = "+ GİDER"
giderButton.setOnClickListener {
    paraGir("Gider") { gider += it }
}
layout.addView(giderButton)

val baslangicButton = Button(this)
baslangicButton.text = "BAŞLANGIÇ KASASI"
baslangicButton.setOnClickListener {
    paraGir("Başlangıç Kasası") { baslangicKasa = it }
}
layout.addView(baslangicButton)

val sifirlaButton = Button(this)
sifirlaButton.text = "GÜNÜ SIFIRLA"
sifirlaButton.setOnClickListener {
    nakit = 0.0
    banka = 0.0
    gider = 0.0
    baslangicKasa = 0.0
    guncelle()
}
layout.addView(sifirlaButton)

setContentView(layout)
guncelle()
}

private fun bilgi(text: String): TextView {
    val view = TextView(this)
    view.text = text
    view.textSize = 20f
    view.setPadding(10, 20, 10, 20)
    return view
}

private fun paraGir(baslik: String, ekle: (Double) -> Unit) {
    val input = EditText(this)
    input.hint = "Tutar (€)"
    input.inputType = 2

    AlertDialog.Builder(this)
        .setTitle(baslik)
        .setView(input)
        .setPositiveButton("Kaydet") { _, _ ->
            val tutar = input.text.toString()
                .replace(",", ".")
                .toDoubleOrNull()

            if (tutar != null) {
                ekle(tutar)
                guncelle()
            }
        }
        .setNegativeButton("İptal", null)
        .show()
}

private fun guncelle() {
    val kasa = baslangicKasa + nakit - gider
    val toplam = kasa + banka

    nakitText.text = "💶 Elden Alınan: %.2f €".format(nakit)
    bankaText.text = "🏦 Banka Havalesi: %.2f €".format(banka)
    giderText.text = "💸 Giderler: %.2f €".format(gider)
    kasaText.text = "💰 Kasada Olması Gereken: %.2f €".format(kasa)
    toplamText.text = "📊 Toplam Para: %.2f €".format(toplam)
}
}
