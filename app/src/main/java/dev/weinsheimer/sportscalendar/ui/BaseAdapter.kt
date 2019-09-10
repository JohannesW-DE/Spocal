package dev.weinsheimer.sportscalendar.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.databinding.DataBindingUtil
import dev.weinsheimer.sportscalendar.R
import dev.weinsheimer.sportscalendar.databinding.BaseAdapterRowBinding

class BaseAdapter(context: Context, list: List<Any>) :
    ArrayAdapter<Any>(context, 0, list) {

    private lateinit var binding: BaseAdapterRowBinding

    var completeList: ArrayList<Any> = ArrayList(list)

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val suggestions = mutableListOf<Any>()

                if (constraint.isNullOrBlank()) {
                    suggestions.addAll(completeList)
                } else {
                    val pattern = constraint.trim()
                    for (obj in completeList) {
                        if (obj.toString().contains(pattern, ignoreCase = true)) {
                            suggestions.add(obj)
                        }
                    }
                }
                return FilterResults().apply {
                    values = suggestions
                    count = suggestions.size
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                results?.let {
                    addAll(results.values as List<Any>)
                }
                notifyDataSetChanged()
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return resultValue.toString()
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.base_adapter_row,
            parent,
            false)

        getItem(position)?.let {obj ->
            binding.text = obj.toString()
        }

        return binding.root
    }
}
