package com.example.medisync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.medisync.data.AppDataBase
import com.example.medisync.repository.repo_citas.CitasRepository
import com.example.medisync.viewmodel.vm_citas.CitasViewModel
import com.example.medisync.viewmodel.vm_citas.CitasViewModelFactory
import kotlinx.coroutines.launch
import android.content.Intent
import android.widget.Button

import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import com.example.medisync.data.entity.BddCitas

class MisCitasActivity : AppCompatActivity() {

    private var citaSeleccionada: BddCitas? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_citas)

        val tvProximaCita = findViewById<TextView>(R.id.tvProximaCita)
        val tvTitleProximaCita = findViewById<TextView>(R.id.tvTitleProximaCita)
        val btnEditar = findViewById<Button>(R.id.btnEditar)
        val btnBorrar = findViewById<Button>(R.id.btnBorrar)
        val calendario = findViewById<MaterialCalendarView>(R.id.calendarView)

        // inicializar el ViewModel
        val database = AppDataBase.getDatabase(this)
        val dao = database.daoCitas()
        val repository = CitasRepository(dao)
        val factory = CitasViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory).get(CitasViewModel::class.java)


        // cargar las citas y mostrarlas en el calendario
        lifecycleScope.launch {
            viewModel.citas.collect { lista ->
                val ahora = System.currentTimeMillis()
                val proxima = lista.filter { it.fechaHora > ahora }.minByOrNull { it.fechaHora }

                if (proxima != null) {
                    val fecha = Date(proxima.fechaHora)
                    val formatoFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    tvProximaCita.text = "${proxima.citaNombre}\n${formatoFecha.format(fecha)}"
                } else {
                    tvProximaCita.text = "Sin citas próximas"
                }

                val diasConCitas = lista.map { cita ->
                    val cal = Calendar.getInstance().apply { timeInMillis = cita.fechaHora }
                    CalendarDay.from(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
                }.toSet()

                calendario.removeDecorators()
                calendario.addDecorator(CitaDecorator(diasConCitas))
            }
        }


        // Configuración del botón Editar
        btnEditar.setOnClickListener {
            citaSeleccionada?.let { cita ->
                val intent = Intent(this, AgendarCitaActivity::class.java).apply {
                    putExtra("fechaHora", cita.fechaHora)
                    putExtra("nombre", cita.citaNombre)
                    putExtra("doctor", cita.doctor)
                    putExtra("especialidad", cita.especialidad)
                    putExtra("nota", cita.nota)
                }
                startActivity(intent)
            } ?: Toast.makeText(this, "Seleccione una cita primero", Toast.LENGTH_SHORT).show()
        }


        // Configuración del botón Borrar
        btnBorrar.setOnClickListener {
            citaSeleccionada?.let { cita ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar cita")
                    .setMessage("¿Desea eliminar la cita '${cita.citaNombre}'?")
                    .setPositiveButton("Sí") { _, _ ->
                        viewModel.eliminar(cita)
                        tvProximaCita.text = "Cita eliminada"
                        citaSeleccionada = null
                        Toast.makeText(this, "Cita eliminada con éxito", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No", null)
                    .show()
            } ?: Toast.makeText(this, "Seleccione una cita primero", Toast.LENGTH_SHORT).show()
        }


        // Configuración del calendario
        calendario.setOnDateChangedListener { _, date, _ ->
            tvTitleProximaCita.text = "Cita seleccionada"
            lifecycleScope.launch {
                viewModel.citas.collect { lista ->
                    val seleccionadas = lista.filter { cita ->
                        val cal = Calendar.getInstance().apply { timeInMillis = cita.fechaHora }
                        cal.get(Calendar.YEAR) == date.year &&
                        cal.get(Calendar.MONTH) + 1 == date.month &&
                        cal.get(Calendar.DAY_OF_MONTH) == date.day
                    }

                    if (seleccionadas.isNotEmpty()) {
                        citaSeleccionada = seleccionadas[0]
                        val cita = citaSeleccionada!!
                        val fecha = Date(cita.fechaHora)
                        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        
                        val pendiente = if (cita.fechaHora > System.currentTimeMillis()) "Cita pendiente ⏳" else "Cita realizada ✅"
                        tvProximaCita.text = "${cita.citaNombre}\n${formato.format(fecha)}\n$pendiente"
                    } else {
                        citaSeleccionada = null
                        tvProximaCita.text = "No hay citas este día"
                    }
                }
            }
        }
    }

    class CitaDecorator(private val dates: Set<CalendarDay>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean = dates.contains(day)
        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(5f, android.graphics.Color.RED))
        }
    }
}
