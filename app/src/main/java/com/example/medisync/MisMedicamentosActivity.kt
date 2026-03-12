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
import com.example.medisync.adapter.MedicamentoAdapter
import com.example.medisync.data.AppDataBase
import com.example.medisync.data.entity.BddMedicamentos
import com.example.medisync.repository.repo_medicamentos.MedicamentosRepository
import com.example.medisync.viewmodel.vm_medicamentos.MedicamentosViewModel
import com.example.medisync.viewmodel.vm_medicamentos.MedicamentosViewModelFactory
import kotlinx.coroutines.launch

class MisMedicamentosActivity : AppCompatActivity() {

    private lateinit var viewModel: MedicamentosViewModel
    private lateinit var adapter: MedicamentoAdapter
    private var medicamentoSeleccionado: BddMedicamentos? = null
    private var listaCompleta: List<BddMedicamentos> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_medicamentos)

        // 1. Referencias de UI
        val rvMedicamentos = findViewById<RecyclerView>(R.id.rvMedicamentos)
        val searchView = findViewById<SearchView>(R.id.searchViewMedicamento)
        val btnEditar = findViewById<Button>(R.id.btnEditarMedicamento)
        val btnBorrar = findViewById<Button>(R.id.btnBorrarMedicamento)

        // 2. Configuración de Arquitectura (ViewModel)
        val database = AppDataBase.getDatabase(this)
        val repository = MedicamentosRepository(database.daoMedicamentos())
        val factory = MedicamentosViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MedicamentosViewModel::class.java]

        // 3. Configuración del RecyclerView y Adapter
        adapter = MedicamentoAdapter(emptyList()) { medicamento ->
            medicamentoSeleccionado = medicamento
        }
        rvMedicamentos.layoutManager = LinearLayoutManager(this)
        rvMedicamentos.adapter = adapter

        // 4. Observar cambios en la base de datos
        lifecycleScope.launch {
            viewModel.medicamentos.collect { lista ->
                listaCompleta = lista
                adapter.actualizarLista(lista)
            }
        }

        // 5. Lógica de Búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarLista(newText)
                return true
            }
        })

        // 6. Botón Editar
        btnEditar.setOnClickListener {
            medicamentoSeleccionado?.let { med ->
                val intent = Intent(this, AgregarMedicamentoActivity::class.java).apply {
                    putExtra("nombre", med.nombreMedicamento)
                    putExtra("fechaCaducidad", med.fechaCaducidad)
                    putExtra("cantidad", med.cantidad)
                    putExtra("unidad", med.unidadMedida)
                }
                startActivity(intent)
            } ?: Toast.makeText(this, "Seleccione un medicamento de la lista", Toast.LENGTH_SHORT).show()
        }

        // 7. Botón Borrar
        btnBorrar.setOnClickListener {
            medicamentoSeleccionado?.let { med ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar Medicamento")
                    .setMessage("¿Desea eliminar '${med.nombreMedicamento}'?")
                    .setPositiveButton("Sí") { _, _ ->
                        viewModel.eliminar(med)
                        medicamentoSeleccionado = null
                        Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No", null)
                    .show()
            } ?: Toast.makeText(this, "Seleccione un medicamento de la lista", Toast.LENGTH_SHORT).show()
        }
    }


    // 8. Método para filtrar la lista de medicamentos
    private fun filtrarLista(query: String?) {
        val filtrada = if (query.isNullOrBlank()) {
            listaCompleta
        } else {
            listaCompleta.filter {
                it.nombreMedicamento.contains(query, ignoreCase = true)
            }
        }
        adapter.actualizarLista(filtrada)
    }
}
