package com.example.medisync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medisync.adapter.DosisAdapter
import com.example.medisync.data.AppDataBase
import com.example.medisync.data.entity.BddDosis
import com.example.medisync.repository.repo_dosis.DosisRepository
import com.example.medisync.viewmodel.vm_dosis.DosisViewModel
import com.example.medisync.viewmodel.vm_dosis.DosisViewModelFactory
import kotlinx.coroutines.launch


class AdministrarDosisActivity : AppCompatActivity() {

    private lateinit var viewModel: DosisViewModel
    private lateinit var adapter: DosisAdapter
    private var listaCompleta: List<BddDosis> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administrar_dosis)

        // 1. Referencias de la interfaz
        val rvDosis = findViewById<RecyclerView>(R.id.rvDosis)
        val searchView = findViewById<SearchView>(R.id.searchViewDosis)
        val btnPausar = findViewById<Button>(R.id.btnPausarDosis)
        val btnEditar = findViewById<Button>(R.id.btnEditarDosis)
        val btnEliminar = findViewById<Button>(R.id.btnEliminarDosis)

        // 2. Configuración de Arquitectura (ViewModel y Repositorio)
        val database = AppDataBase.getDatabase(this)
        val repository = DosisRepository(database.daoDosis())
        val factory = DosisViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DosisViewModel::class.java]

        // 3. Configuración del RecyclerView y Adaptador
        adapter = DosisAdapter(mutableListOf())
        rvDosis.layoutManager = LinearLayoutManager(this)
        rvDosis.adapter = adapter

        // 4. Observar cambios en la base de datos y actualizar UI
        lifecycleScope.launch {
            viewModel.dosis.collect { lista ->
                listaCompleta = lista
                adapter.actualizar(lista)
            }
        }

        // 5. Lógica de Búsqueda/Filtrado
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarTratamientos(newText)
                return true
            }
        })

        // 6. Botón Pausar/Reanudar
        btnPausar.setOnClickListener {
            adapter.selected?.let { dosis ->
                val nuevoEstado = !dosis.activo
                val dosisActualizada = dosis.copy(activo = nuevoEstado)
                
                viewModel.actualizar(dosisActualizada)
                
                val mensaje = if (nuevoEstado) "Tratamiento reanudado" else "Tratamiento pausado"
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(this, "Seleccione un tratamiento de la lista", Toast.LENGTH_SHORT).show()
        }

        // 7. Botón Editar
        btnEditar.setOnClickListener {
            adapter.selected?.let { dosis ->
                val intent = Intent(this, CrearTratamientoActivity::class.java).apply {
                    putExtra("id", dosis.id)
                    putExtra("medicamento", dosis.medicamento)
                    putExtra("cantidad", dosis.cantidad)
                    putExtra("intervalo", dosis.intervaloHoras)
                    putExtra("fechaHora", dosis.fechaHoraInicio)
                    putExtra("duracion", dosis.duracionDias)
                }
                startActivity(intent)
            } ?: Toast.makeText(this, "Seleccione un tratamiento de la lista", Toast.LENGTH_SHORT).show()
        }

        // 8. Botón Eliminar con confirmación
        btnEliminar.setOnClickListener {
            adapter.selected?.let { dosis ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar tratamiento")
                    .setMessage("¿Desea eliminar permanentemente el tratamiento '${dosis.id}'?")
                    .setPositiveButton("Sí") { _, _ ->
                        viewModel.eliminar(dosis)
                        adapter.selected = null
                        Toast.makeText(this, "Tratamiento eliminado", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No", null)
                    .show()
            } ?: Toast.makeText(this, "Seleccione un tratamiento de la lista", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Filtra la lista de tratamientos mostrada según el texto ingresado en la búsqueda.
     */
    private fun filtrarTratamientos(query: String?) {
        val listaFiltrada = if (query.isNullOrBlank()) {
            listaCompleta
        } else {
            listaCompleta.filter {
                it.id.contains(query, ignoreCase = true)
            }
        }
        adapter.actualizar(listaFiltrada)
    }
}
