package com.example.medisync.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medisync.R
import com.example.medisync.data.entity.BddDosis

/**
 * Adaptador para mostrar la lista de dosis/tratamientos en un RecyclerView.
 */
class DosisAdapter(
    private var lista: MutableList<BddDosis>
) : RecyclerView.Adapter<DosisAdapter.ViewHolder>() {

    var selected: BddDosis? = null
    private var selectedPosition = RecyclerView.NO_POSITION

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.findViewById(R.id.containerItemDosis)
        val tvId: TextView = view.findViewById(R.id.tvIdDosis)
        val medicamento: TextView = view.findViewById(R.id.tvMedicamento)
        val dosis: TextView = view.findViewById(R.id.tvDosis)
        val estado: TextView = view.findViewById(R.id.tvEstado)

        fun bind(item: BddDosis, isSelected: Boolean) {
            tvId.text = "${item.id}"
            medicamento.text = item.medicamento
            
            // Aquí mostramos la cantidad junto con su unidad de medida (ml/gr)
            dosis.text = "Cantidad: ${item.cantidad} ${item.unidadMedida} cada ${item.intervaloHoras}h"
            
            if (item.activo) {
                estado.text = "ACTIVO"
                estado.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            } else {
                estado.text = "PAUSADO"
                estado.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
            }

            container.isSelected = isSelected
            
            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                selected = item
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_dosis, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position], selectedPosition == position)
    }

    override fun getItemCount(): Int = lista.size

    fun actualizar(nueva: List<BddDosis>) {
        lista.clear()
        lista.addAll(nueva)
        selected = null
        selectedPosition = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }
}
