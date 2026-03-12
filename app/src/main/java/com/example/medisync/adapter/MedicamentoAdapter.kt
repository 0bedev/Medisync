package com.example.medisync.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medisync.R
import com.example.medisync.data.entity.BddMedicamentos
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adaptador para mostrar la lista de medicamentos en un RecyclerView.
 */
class MedicamentoAdapter(
    private var listaMedicamentos: List<BddMedicamentos>,
    private val onItemSelected: (BddMedicamentos) -> Unit
) : RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder>() {

    // Variable para rastrear la posición del elemento seleccionado
    private var selectedPosition = RecyclerView.NO_POSITION

    /**
     * ViewHolder que contiene las vistas de cada elemento de la lista.
     */
    inner class MedicamentoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre = view.findViewById<TextView>(R.id.tvNombreMedicamento)
        val tvFecha = view.findViewById<TextView>(R.id.tvFechaCaducidad)
        val tvCantidad = view.findViewById<TextView>(R.id.tvCantidad)
        val tvEstado = view.findViewById<TextView>(R.id.tvEstado)

        fun bind(medicamento: BddMedicamentos, isSelected: Boolean) {
            tvNombre.text = medicamento.nombreMedicamento
            
            // Formatear la fecha de caducidad
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            tvFecha.text = "Caduca: ${sdf.format(Date(medicamento.fechaCaducidad))}"
            
            // Mostrar cantidad con su unidad de medida
            tvCantidad.text = "Cantidad: ${medicamento.cantidad} ${medicamento.unidadMedida}"

            // Lógica para determinar si el medicamento está vigente o caducado
            val hoy = System.currentTimeMillis()
            if (medicamento.fechaCaducidad < hoy) {
                tvEstado.text = "CADUCADO"
                tvEstado.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            } else {
                tvEstado.text = "VIGENTE"
                tvEstado.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            }

            // Aplicar el fondo visual si el elemento está seleccionado
            itemView.isSelected = isSelected
            // Usamos el recurso que creamos antes
            itemView.setBackgroundResource(if (isSelected) R.drawable.item_medicamento_background else 0)

            // Manejo del clic en el elemento
            itemView.setOnClickListener {
                val previousSelected = selectedPosition
                selectedPosition = adapterPosition
                
                // Notificar cambios para actualizar el fondo visual
                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedPosition)
                
                // Ejecutar el callback con el medicamento seleccionado
                onItemSelected(medicamento)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicamentoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_medicamento, parent, false)
        return MedicamentoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MedicamentoViewHolder, position: Int) {
        holder.bind(listaMedicamentos[position], selectedPosition == position)
    }

    override fun getItemCount(): Int = listaMedicamentos.size

    /**
     * Actualiza los datos del adaptador cuando cambia la base de datos o se realiza una búsqueda.
     */
    fun actualizarLista(nuevaLista: List<BddMedicamentos>) {
        listaMedicamentos = nuevaLista
        // Reiniciamos la selección al actualizar la lista
        selectedPosition = RecyclerView.NO_POSITION 
        notifyDataSetChanged()
    }
}
