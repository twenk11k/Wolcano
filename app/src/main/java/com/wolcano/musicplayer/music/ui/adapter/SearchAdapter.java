package com.wolcano.musicplayer.music.ui.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.listener.SetSearchQuery;
import com.wolcano.musicplayer.music.widgets.MaterialSearchLast;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchAdapter extends BaseAdapter implements Filterable {


    private ArrayList<String> searchList;
    private String[] suggestions,lastSearches;
    private Drawable suggestionIcon,suggestionSend;
    private LayoutInflater inflater;
    private boolean ellipsize;
    private Context context;
    private boolean isFirst;
    private int whichList = 0;
    private int textColor;
    private MaterialSearchLast materialSearchLast;
    private SetSearchQuery callback;


    public SearchAdapter(Context context, String[] suggestions, Drawable suggestionIcon, Drawable suggestionSend, boolean ellipsize, String[] lastSearches, boolean isFirst, SetSearchQuery callback, int textColor, MaterialSearchLast materialSearchLast) {

        this.inflater = LayoutInflater.from(context);
        this.searchList = new ArrayList<>();
        this.suggestions = suggestions;
        this.suggestionIcon = suggestionIcon;
        this.ellipsize = ellipsize;
        this.suggestionSend = suggestionSend;
        this.context = context;
        this.lastSearches = lastSearches;
        this.isFirst = isFirst;
        this.callback = callback;
        this.textColor = textColor;
        this.materialSearchLast = materialSearchLast;

    }

    public boolean getIsFirst(){
        return isFirst;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (TextUtils.isEmpty(constraint) && isFirst) {
                    whichList = 1;
                    List<String> searchData = new ArrayList<>();
                    for (int i = 0; i < lastSearches.length; i++) {
                        searchData.add(lastSearches[i]);
                    }
                    filterResults.values = searchData;
                    filterResults.count = searchData.size();
                } else {
                    whichList = 2;
                        // Retrieve the autocomplete results.
                        List<String> searchData = new ArrayList<>();
                        for (String string : suggestions) {
                            if (string.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                                searchData.add(string);
                            }
                        }
                        // Assign the data to the FilterResults
                        filterResults.values = searchData;
                        filterResults.count = searchData.size();

                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    searchList = (ArrayList<String>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }

    @Override
    public int getCount() {
        return searchList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private void removeItem(int position){
        if(position<searchList.size()){
            searchList.remove(position);
            notifyDataSetChanged();
        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SuggestionsViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.searchview_suggestion_item, parent, false);
            viewHolder = new SuggestionsViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SuggestionsViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchList.size()>0)
                callback.onSearchQuery(position,true);

            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int accentColor = Utils.getAccentColor(context);

                MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                        .title(viewHolder.textView.getText().toString())
                        .content(context.getString(R.string.question_remove_history))
                        .negativeText(context.getString(R.string.cancelU))
                        .negativeColor(accentColor)
                        .positiveText(context.getString(R.string.removeU))
                        .positiveColor(accentColor)
                        .theme(Theme.DARK)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                callback.onRemoveSuggestion(position,whichList);
                                removeItem(position);
                            }
                        });
                MaterialDialog dialog = builder.build();
                dialog.show();
                return false;
            }
        });
        String currentListData = (String) getItem(position);

            viewHolder.textView.setText(currentListData);
        viewHolder.textView.setTextColor(textColor);
        if (ellipsize) {
            viewHolder.textView.setSingleLine();
            viewHolder.textView.setEllipsize(TextUtils.TruncateAt.END);
        }
        viewHolder.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isFirst = false;

                callback.onSearchQuery(position,false);
                materialSearchLast.showKeyboard();

            }
        });

        return convertView;
    }

    public class SuggestionsViewHolder {

        private TextView textView;
        private ImageView imageView1,imageView2;

        private SuggestionsViewHolder(View itemView) {
            textView =  itemView.findViewById(R.id.suggestion_text);
            if (suggestionIcon != null) {
                imageView1 =  itemView.findViewById(R.id.suggestion_icon);
                imageView1.setImageDrawable(suggestionIcon);
            }
            if(suggestionSend != null){
                imageView2 = itemView.findViewById(R.id.suggestion_send);
                imageView2.setImageDrawable(suggestionSend);
            }
        }
    }
}
