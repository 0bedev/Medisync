package com.example.medisync

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import android.app.DatePickerDialog
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.medisync.data.AppDataBase
import com.example.medisync.data.entity.BddMedicamentos
import com.example.medisync.repository.repo_medicamentos.MedicamentosRepository
import com.example.medisync.viewmodel.vm_medicamentos.MedicamentosViewModel
import com.example.medisync.viewmodel.vm_medicamentos.MedicamentosViewModelFactory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class AgregarMedicamentoActivity : AppCompatActivity() {
    // Variables globales para control de fecha y calendario
    private var fechaSeleccionada = false
    private lateinit var calendario : Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agg_medicamento)

        // Inicializamos el calendario con la fecha y hora actual
        calendario = Calendar.getInstance()

        // Referencias a los componentes de la interfaz (UI)
        val etNombreMedicamento = findViewById<EditText>(R.id.etMedicamentoNombre)
        val etFechaCaducidad = findViewById<EditText>(R.id.etFechaCaducidad)
        val etContenido = findViewById<EditText>(R.id.etContenido)
        val rgUnidadMedida = findViewById<RadioGroup>(R.id.rgUnidadMedida)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarMedicamento)

        // Configuración del diálogo para seleccionar la fecha de caducidad
        etFechaCaducidad.setOnClickListener {
            val year = calendario.get(Calendar.YEAR)
            val month = calendario.get(Calendar.MONTH)
            val day = calendario.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, y, m, d ->
                calendario.set(Calendar.YEAR, y)
                calendario.set(Calendar.MONTH, m)
                calendario.set(Calendar.DAY_OF_MONTH, d)
                fechaSeleccionada = true // Marcamos que ya se eligió una fecha
                etFechaCaducidad.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y))
            }, year, month, day).show()
        }

        // Configuración de la base de datos y arquitectura Room (ViewModel)
        val database = AppDataBase.getDatabase(this)
        val dao = database.daoMedicamentos()
        val repository = MedicamentosRepository(dao)
        val factory = MedicamentosViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[MedicamentosViewModel::class.java]

        // Lógica para el botón de Guardar
        btnGuardar.setOnClickListener {
            // Captura de datos ingresados por el usuario
            val nombreMedicamento = etNombreMedicamento.text.toString().trim()
            val contenidoTexto = etContenido.text.toString()
            val selectedId = rgUnidadMedida.checkedRadioButtonId

            // Determinamos la unidad de medida (ml o gr)
            val unidadMedida = if (selectedId != -1) {
                findViewById<RadioButton>(selectedId)?.text.toString()
            } else ""

            val cantidad = contenidoTexto.toDoubleOrNull()

            // Validación de campos obligatorios
            if (nombreMedicamento.isEmpty() || unidadMedida.isEmpty() || cantidad == null || !fechaSeleccionada) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Creamos el objeto de datos para la base de datos
            val medicamento = BddMedicamentos(
                id = nombreMedicamento,
                nombreMedicamento = nombreMedicamento,
                fechaCaducidad = calendario.timeInMillis,
                cantidad = cantidad,
                unidadMedida = unidadMedida
            )

            // Operación asíncrona en la base de datos usando Corrutinas
            lifecycleScope.launch {
                try {
                    // Verificamos si el medicamento ya existe por su nombre (ID)
                    val medicamentoExist = viewModel.obtenerPorId(nombreMedicamento).firstOrNull()

                    if (medicamentoExist != null) {
                        // Si existe, pedimos confirmación para sobrescribir
                        AlertDialog.Builder(this@AgregarMedicamentoActivity)
                            .setTitle("Medicamento existente")
                            .setMessage("Ya existe un medicamento con este nombre. ¿Desea actualizarlo?")
                            .setPositiveButton("Sí") { _, _ ->
                                viewModel.insertar(medicamento)
                                Toast.makeText(this@AgregarMedicamentoActivity, "Actualizado", Toast.LENGTH_SHORT).show()
                                finish() // Cerramos la pantalla al terminar
                            }
                            .setNegativeButton("No", null)
                            .show()
                    } else {
                        // Si no existe, lo guardamos directamente
                        viewModel.insertar(medicamento)
                        Toast.makeText(this@AgregarMedicamentoActivity, "Guardado", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    // Manejo de errores inesperados
                    Toast.makeText(this@AgregarMedicamentoActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}