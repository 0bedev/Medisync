package com.example.medisync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.EditText
import java.util.Calendar

import android.widget.Button

import androidx.lifecycle.ViewModelProvider
import com.example.medisync.data.AppDataBase
import com.example.medisync.repository.repo_citas.CitasRepository
import com.example.medisync.viewmodel.vm_citas.CitasViewModel
import com.example.medisync.viewmodel.vm_citas.CitasViewModelFactory
import com.example.medisync.data.entity.BddCitas

import android.widget.Toast

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import androidx.appcompat.app.AlertDialog

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale





class AgendarCitaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState) // inicializar la actividad
        setContentView(R.layout.activity_agendar_cita) // establecer el diseño de la actividad



        val etCitaNombre = findViewById<EditText>(R.id.etCitaNombre)
        val etDoctor = findViewById<EditText>(R.id.etDoctor)
        val etEspecialidad = findViewById<EditText>(R.id.etEspecialidad)
        val etNota = findViewById<EditText>(R.id.etNota)
        val etFecha = findViewById<EditText>(R.id.etFecha)
        val etHora = findViewById<EditText>(R.id.etHora)
        val calendario = Calendar.getInstance()

        val btnGuardar = findViewById<Button>(R.id.btnGuardarCita)

        val fechaHora = intent.getLongExtra("fechaHora", -1L)
        val nombre = intent.getStringExtra("nombre")
        val doctorExtra = intent.getStringExtra("doctor")
        val especialidadExtra = intent.getStringExtra("especialidad")
        val notaExtra = intent.getStringExtra("nota")


        // seleccionar la hora con el timePicker
        etHora.setOnClickListener {

            val hour = calendario.get(Calendar.HOUR_OF_DAY)
            val minute = calendario.get(Calendar.MINUTE)

            TimePickerDialog(this, { _, h, m ->

                calendario.set(Calendar.HOUR_OF_DAY, h)
                calendario.set(Calendar.MINUTE, m)

                etHora.setText(String.format("%02d:%02d", h, m))

            }, hour, minute, true).show()
        }

        // seleccionar la fecha con el datePicker
        etFecha.setOnClickListener {

            val year = calendario.get(Calendar.YEAR)
            val month = calendario.get(Calendar.MONTH)
            val day = calendario.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->

                calendario.set(Calendar.YEAR, y)
                calendario.set(Calendar.MONTH, m)
                calendario.set(Calendar.DAY_OF_MONTH, d)

                etFecha.setText("$d/${m+1}/$y")

            }, year, month, day).show()
        }

        // inicializar el ViewModel
        val database = AppDataBase.getDatabase(this)
        val dao = database.daoCitas()
        val repository = CitasRepository(dao)
        val factory = CitasViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)
            .get(CitasViewModel::class.java)


        // Si recibimos datos de una cita existente, llenamos los campos
        if(fechaHora != -1L){
            calendario.timeInMillis = fechaHora // Sincronizar el objeto calendario con la fecha recibida

            val fecha = Date(fechaHora)
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())

            etCitaNombre.setText(nombre)
            etDoctor.setText(doctorExtra)
            etEspecialidad.setText(especialidadExtra)
            etNota.setText(notaExtra)
            etFecha.setText(formatoFecha.format(fecha))
            etHora.setText(formatoHora.format(fecha))
        }

        // guardar la cita
        btnGuardar.setOnClickListener {

            val citaNombre = etCitaNombre.text.toString()
            val doctor = etDoctor.text.toString()
            val especialidad = etEspecialidad.text.toString()
            val nota = etNota.text.toString()
            val fechaHoraFinal = calendario.timeInMillis

            // validar que los campos no estén vacíos
            if(doctor.isEmpty() || citaNombre.isEmpty() || especialidad.isEmpty() || nota.isEmpty() || etFecha.text.isEmpty() || etHora.text.isEmpty()){
                Toast.makeText(this,"Por favor, complete todos los campos",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val cita = BddCitas(
                id = citaNombre,
                citaNombre = citaNombre,
                doctor = doctor,
                especialidad = especialidad,
                nota = nota,
                fechaHora = fechaHoraFinal
            )

            // verificar si la cita ya existe
            lifecycleScope.launch {

                val citaExistente =
                    viewModel.obtenerPorId(citaNombre).first()

                if(citaExistente != null){

                    AlertDialog.Builder(this@AgendarCitaActivity)
                        .setTitle("Cita existente")
                        .setMessage("Ya existe una cita con ese nombre ¿Desea editarla?")
                        .setPositiveButton("Sí"){ _, _ ->

                            viewModel.insertar(cita)

                            etCitaNombre.setText("")
                            etDoctor.setText("")
                            etEspecialidad.setText("")
                            etNota.setText("")
                            etFecha.setText("")
                            etHora.setText("")

                            Toast.makeText(
                                this@AgendarCitaActivity,
                                "Cita actualizada",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish() // Cerramos la actividad después de actualizar

                        }
                        .setNegativeButton("No", null)
                        .show()

                }else{

                    viewModel.insertar(cita)

                    etCitaNombre.setText("")
                    etDoctor.setText("")
                    etEspecialidad.setText("")
                    etNota.setText("")
                    etFecha.setText("")
                    etHora.setText("")

                    Toast.makeText(
                        this@AgendarCitaActivity,
                        "Cita guardada",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish() // Cerramos la actividad después de guardar
                }
            }
        }
    }
}
