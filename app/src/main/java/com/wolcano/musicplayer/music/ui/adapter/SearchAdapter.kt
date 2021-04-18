package com.wolcano.musicplayer.music.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.listener.SetSearchQuery
import com.wolcano.musicplayer.music.widgets.MaterialSearchView

class SearchAdapter(
    private var context: Context?,
    private var suggestions: ArrayList<String>,
    private var suggestionIcon: Drawable?,
    private var suggestionSend: Drawable?,
    private var ellipsize: Boolean,
    private var isFirst: Boolean,
    private var callback: SetSearchQuery?,
    private var textColor: Int,
    private var materialSearchView: MaterialSearchView?
) : BaseAdapter(), Filterable {

    private var searchList: MutableList<String> = ArrayList()
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var whichList = 0

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterResults = FilterResults()
                if (TextUtils.isEmpty(constraint)) {
                    whichList = 1
                    val lastSearches = suggestions.reversed()
                    val searchData: MutableList<String> = ArrayList()
                    for (i in lastSearches.indices) {
                        searchData.add(lastSearches[i])
                    }
                    filterResults.values = searchData
                    filterResults.count = searchData.size
                } else {
                    whichList = 2
                    // Retrieve the autocomplete results.
                    val searchData: MutableList<String> = ArrayList()
                    for (string in suggestions.reversed()) {
                        if (string.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            searchData.add(string)
                        }
                    }
                    // Assign the data to the FilterResults
                    filterResults.values = searchData
                    filterResults.count = searchData.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                if (results.values != null) {
                    searchList = results.values as ArrayList<String>
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getCount(): Int {
        return searchList.size
    }

    override fun getItem(position: Int): Any {
        return searchList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun removeItem(position: Int) {
        if (position < searchList.size) {
            val item = searchList[position]
            searchList.removeAt(position)
            if (suggestions.contains(item)) {
                suggestions.remove(item)
            }
            notifyDataSetChanged()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        val viewHolder: SearchAdapter.SuggestionsViewHolder
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.searchview_suggestion_item, parent, false)
            viewHolder = SuggestionsViewHolder(convertView)
            convertView?.tag = viewHolder
        } else {
            viewHolder = convertView.tag as SearchAdapter.SuggestionsViewHolder
        }
        convertView?.setOnClickListener {
            if (searchList.size > 0) callback!!.onSearchQuery(
                position,
                true
            )
        }
        convertView?.setOnLongClickListener {
            MaterialDialog(context!!).show {
                title(text = viewHolder.textView?.text.toString())
                message(text = context.getString(R.string.question_remove_history))
                negativeButton(android.R.string.cancel)
                positiveButton(text = context.getString(R.string.removeU)) {
                    callback?.onRemoveSuggestion(position, whichList)
                    removeItem(position)
                }
            }
            false
        }
        val currentListData = getItem(position) as String?
        viewHolder.textView?.text = currentListData
        viewHolder.textView?.setTextColor(textColor)
        if (ellipsize) {
            viewHolder.textView?.setSingleLine()
            viewHolder.textView?.ellipsize = TextUtils.TruncateAt.END
        }
        viewHolder.imageView2?.setOnClickListener {
            isFirst = false
            callback?.onSearchQuery(position, false)
            materialSearchView?.showKeyboard()
        }
        return convertView
    }

    fun addSuggestion(query: String) {
        if (suggestions.contains(query)) {
            suggestions.remove(query)
        }
        suggestions.add(query)
        notifyDataSetChanged()
    }

    inner class SuggestionsViewHolder(itemView: View?) {
        var textView: TextView? = null
        private var imageView1: ImageView? = null
        var imageView2: ImageView? = null

        init {
            this.textView = itemView?.findViewById(R.id.suggestion_text)
            if (suggestionIcon != null) {
                imageView1 = itemView?.findViewById(R.id.suggestion_icon)
                imageView1?.setImageDrawable(suggestionIcon)
            }
            if (suggestionSend != null) {
                imageView2 = itemView?.findViewById(R.id.suggestion_send)
                imageView2?.setImageDrawable(suggestionSend)
            }
        }
    }

}