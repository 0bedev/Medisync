package com.example.medisync

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.medisync.data.AppDataBase
import com.example.medisync.data.entity.BddDosis
import com.example.medisync.repository.repo_dosis.DosisRepository
import com.example.medisync.repository.repo_medicamentos.MedicamentosRepository
import com.example.medisync.utils.AlarmHelper
import com.example.medisync.viewmodel.vm_dosis.DosisViewModel
import com.example.medisync.viewmodel.vm_dosis.DosisViewModelFactory
import com.example.medisync.viewmodel.vm_medicamentos.MedicamentosViewModel
import com.example.medisync.viewmodel.vm_medicamentos.MedicamentosViewModelFactory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.*
import java.text.SimpleDateFormat

/**
 * Actividad para configurar y guardar un nuevo tratamiento médico.
 * Programa la primera alarma del tratamiento para iniciar el ciclo de recordatorios.
 */
class CrearTratamientoActivity : AppCompatActivity() {

    private lateinit var calendario: Calendar
    private var medicamentoSeleccionado: String = ""
    private var unidadMedidaSeleccionada: String = ""
    private var fechaSeleccionada = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_tratamiento)

        calendario = Calendar.getInstance()

        val spMedicamento = findViewById<Spinner>(R.id.spMedicamento)
        val etTratamiento = findViewById<EditText>(R.id.etTratamiento)
        val etDosisCantidad = findViewById<EditText>(R.id.etDosisCantidad)
        val etIntervaloHoras = findViewById<EditText>(R.id.etIntervaloHoras)
        val etFechaInicio = findViewById<EditText>(R.id.etFechaInicio)
        val etHoraInicio = findViewById<EditText>(R.id.etHoraInicio)
        val etDuracion = findViewById<EditText>(R.id.etDuracion)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarTratamiento)

        // Capturar datos del Intent para modo edición
        val idExtra = intent.getStringExtra("id")
        val medicamentoExtra = intent.getStringExtra("medicamento")
        val cantidadExtra = intent.getDoubleExtra("cantidad", -1.0)
        val intervaloExtra = intent.getIntExtra("intervalo", -1)
        val fechaHoraExtra = intent.getLongExtra("fechaHora", -1L)
        val duracionExtra = intent.getIntExtra("duracion", -1)

        // Modo Edición: Rellenar campos
        if (idExtra != null) {
            etTratamiento.setText(idExtra)
            etTratamiento.isEnabled = false // El nombre del tratamiento es el ID
            
            if (cantidadExtra != -1.0) etDosisCantidad.setText(cantidadExtra.toString())
            if (intervaloExtra != -1) etIntervaloHoras.setText(intervaloExtra.toString())
            if (duracionExtra != -1) etDuracion.setText(duracionExtra.toString())
            
            if (fechaHoraExtra != -1L) {
                calendario.timeInMillis = fechaHoraExtra
                fechaSeleccionada = true
                val sdfFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val sdfHora = SimpleDateFormat("HH:mm", Locale.getDefault())
                etFechaInicio.setText(sdfFecha.format(Date(fechaHoraExtra)))
                etHoraInicio.setText(sdfHora.format(Date(fechaHoraExtra)))
            }
        }

        val database = AppDataBase.getDatabase(this)
        val vmMed = ViewModelProvider(this, MedicamentosViewModelFactory(MedicamentosRepository(database.daoMedicamentos())))[MedicamentosViewModel::class.java]
        val vmDosis = ViewModelProvider(this, DosisViewModelFactory(DosisRepository(database.daoDosis())))[DosisViewModel::class.java]

        lifecycleScope.launch {
            vmMed.medicamentos.collect { lista ->
                val nombres = lista.map { it.nombreMedicamento }
                val adapter = ArrayAdapter(this@CrearTratamientoActivity, android.R.layout.simple_spinner_item, nombres)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spMedicamento.adapter = adapter

                if (medicamentoExtra != null) {
                    val pos = nombres.indexOf(medicamentoExtra)
                    if (pos != -1) spMedicamento.setSelection(pos)
                }

                spMedicamento.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        if (lista.isNotEmpty()) {
                            val med = lista[position]
                            medicamentoSeleccionado = med.nombreMedicamento
                            unidadMedidaSeleccionada = med.unidadMedida
                        }
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
            }
        }

        etFechaInicio.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                calendario.set(Calendar.YEAR, y)
                calendario.set(Calendar.MONTH, m)
                calendario.set(Calendar.DAY_OF_MONTH, d)
                fechaSeleccionada = true
                etFechaInicio.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y))
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH)).show()
        }

        etHoraInicio.setOnClickListener {
            TimePickerDialog(this, { _, h, min ->
                calendario.set(Calendar.HOUR_OF_DAY, h)
                calendario.set(Calendar.MINUTE, min)
                etHoraInicio.setText(String.format(Locale.getDefault(), "%02d:%02d", h, min))
            }, calendario.get(Calendar.HOUR_OF_DAY), calendario.get(Calendar.MINUTE), true).show()
        }

        btnGuardar.setOnClickListener {
            val cantidad = etDosisCantidad.text.toString().toDoubleOrNull()
            val intervalo = etIntervaloHoras.text.toString().toIntOrNull()
            val duracion = etDuracion.text.toString().toIntOrNull()
            val nombreTrat = etTratamiento.text.toString().trim()

            if (nombreTrat.isEmpty() || medicamentoSeleccionado.isEmpty() || cantidad == null || intervalo == null || duracion == null || !fechaSeleccionada) {
                Toast.makeText(this, "Complete todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dosis = BddDosis(
                id = nombreTrat,
                tratamiento = nombreTrat,
                medicamento = medicamentoSeleccionado,
                cantidad = cantidad,
                unidadMedida = unidadMedidaSeleccionada,
                intervaloHoras = intervalo,
                fechaHoraInicio = calendario.timeInMillis,
                duracionDias = duracion,
                activo = true
            )

            lifecycleScope.launch {
                if (idExtra == null) {
                    val existe = vmDosis.obtenerPorId(nombreTrat).firstOrNull()
                    if (existe != null) {
                        AlertDialog.Builder(this@CrearTratamientoActivity)
                            .setTitle("Tratamiento Existente")
                            .setMessage("Ya existe un tratamiento con este nombre. ¿Deseas actualizarlo?")
                            .setPositiveButton("Sí") { _, _ -> guardarFinal(vmDosis, dosis) }
                            .setNegativeButton("No", null)
                            .show()
                    } else {
                        guardarFinal(vmDosis, dosis)
                    }
                } else {
                    guardarFinal(vmDosis, dosis)
                }
            }
        }
    }

    /**
     * Guarda el tratamiento en la base de datos y programa la primera alarma.
     */
    private fun guardarFinal(vmDosis: DosisViewModel, dosis: BddDosis) {
        lifecycleScope.launch {
            vmDosis.insertar(dosis)
            
            // PROGRAMAR LA PRIMERA ALARMA DEL TRATAMIENTO
            AlarmHelper.setExactAlarm(
                context = this@CrearTratamientoActivity,
                id = dosis.id.hashCode(),
                timeInMillis = dosis.fechaHoraInicio,
                title = "¡HORA DE TU MEDICAMENTO!",
                message = "Debes tomar: ${dosis.medicamento} (${dosis.cantidad} ${dosis.unidadMedida})",
                idTratamiento = dosis.id
            )

            Toast.makeText(this@CrearTratamientoActivity, "Tratamiento y alarma programados", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
