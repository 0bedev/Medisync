package com.example.medisync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.EditText
import java.util.Calendar


class AgendarCitaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar_cita)

        val etFecha = findViewById<EditText>(R.id.etFecha)
        val etHora = findViewById<EditText>(R.id.etHora)
        val calendario = Calendar.getInstance()

        etHora.setOnClickListener {

            val hour = calendario.get(Calendar.HOUR_OF_DAY)
            val minute = calendario.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, { _, h, m ->
                etHora.setText(String.format("%02d:%02d", h, m))
            }, hour, minute, true)

            timePicker.show()
        }

        etFecha.setOnClickListener {

            val year = calendario.get(Calendar.YEAR)
            val month = calendario.get(Calendar.MONTH)
            val day = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                etFecha.setText("$d/${m+1}/$y")
            }, year, month, day)

            datePicker.show()
        }

    }
}
