package com.wolcano.musicplayer.music.widgets;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.utils.AnimationUtil;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.listener.SetSearchQuery;
import com.wolcano.musicplayer.music.ui.adapter.SearchAdapter;

import java.lang.reflect.Field;
import java.util.List;

public class MaterialSearchLast extends FrameLayout implements Filter.FilterListener {

    private MenuItem menuItem;
    private boolean isSearchOpen = false;
    private int animationDuration;
    private boolean clearingFocus;

    //Views
    private View searchLayout;
    private View tintView;
    private ListView suggestionListView;
    public EditText searchSrcTextView;
    private ImageButton backBtn;
    private ImageButton emptyBtn;
    private RelativeLayout searchTopBar;

    private CharSequence oldQueryText;
    private CharSequence userQuery;

    private OnQueryTextListener onQueryChangeListener;
    private SearchViewListener searchViewListener;

    private SearchAdapter searchAdapter;

    private SavedState mSavedState;
    private boolean submit = false;

    private boolean ellipsize = false;

    private boolean allowVoiceSearch;
    private Drawable suggestionIcon, suggestionRemove, suggestionSend;

    private Context context;
    public static boolean isRemoved = false;
    public static boolean isEmpty = false;

    public MaterialSearchLast(Context context) {
        this(context, null);
    }

    public MaterialSearchLast(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialSearchLast(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        this.context = context;

        initiateView();
        initStyle(attrs, defStyleAttr);
    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView, defStyleAttr, 0);

        if (a != null) {
            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchBackground)) {
                setBackground(a.getDrawable(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchBackground));
            }

            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_android_textColor)) {
                setTextColor(a.getColor(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_android_textColor, 0));
            }

            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_android_textColorHint)) {
                setHintTextColor(a.getColor(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_android_textColorHint, 0));
            }


            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchCloseIcon)) {
                setCloseIcon(a.getDrawable(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchCloseIcon));
            }

            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchBackIcon)) {
                setBackIcon(a.getDrawable(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchBackIcon));
            }

            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchSuggestionBackground)) {
                setSuggestionBackground(a.getDrawable(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchSuggestionBackground));
            }

            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchSuggestionIcon)) {
                setSuggestionIcon(a.getDrawable(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchSuggestionIcon));
            }
            if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionClose)) {
                setSuggestionRemove(a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionClose));
            }
            if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionSend)) {
                setSuggestionSend(a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionSend));
            }
        }
    }

    private void initiateView() {
        LayoutInflater.from(context).inflate(R.layout.searchview_layout, this, true);
        searchLayout = findViewById(R.id.search_layout);

        searchTopBar = (RelativeLayout) searchLayout.findViewById(R.id.search_top_bar);
        suggestionListView = (ListView) searchLayout.findViewById(R.id.suggestion_list);
        searchSrcTextView = (EditText) searchLayout.findViewById(R.id.searchTextView);
        backBtn = (ImageButton) searchLayout.findViewById(R.id.action_up_btn);
        emptyBtn = (ImageButton) searchLayout.findViewById(R.id.action_empty_btn);
        tintView = searchLayout.findViewById(R.id.transparent_view);

        searchSrcTextView.setOnClickListener(mOnClickListener);
        backBtn.setOnClickListener(mOnClickListener);
        emptyBtn.setOnClickListener(mOnClickListener);
        tintView.setOnClickListener(mOnClickListener);
        allowVoiceSearch = false;

        initSearchView();

        suggestionListView.setVisibility(GONE);

        setAnimationDuration(AnimationUtil.ANIMATION_DURATION_MEDIUM);
    }

    private void initSearchView() {
        searchSrcTextView.setOnEditorActionListener((v, actionId, event) -> {
            onSubmitQuery();
            return true;
        });

        searchSrcTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userQuery = s;
                startFilter(s);
                MaterialSearchLast.this.onTextChanged(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchSrcTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyboard();
                    showSuggestions();
                }
            }
        });
    }

    public String getListPosSend(int position) {
        return suggestionListView.getAdapter().getItem(position).toString();
    }

    public void startFilter(CharSequence s) {
        if (searchAdapter != null && searchAdapter instanceof Filterable) {
            ((Filterable) searchAdapter).getFilter().filter(s, this);
        }
    }

    private final OnClickListener mOnClickListener = new OnClickListener() {

        public void onClick(View v) {
            if (v == backBtn) {
                closeSearch();
            } else if (v == emptyBtn) {
                searchSrcTextView.setText(null);
            } else if (v == searchSrcTextView) {
                showSuggestions();
            } else if (v == tintView) {
                closeSearch();
            } else if (v == suggestionListView) {
                removeFromList(v);
            }
        }
    };

    private void onTextChanged(CharSequence newText) {
        CharSequence text = searchSrcTextView.getText();
        userQuery = text;
        boolean hasText = !TextUtils.isEmpty(text);
        if (hasText) {
            emptyBtn.setVisibility(VISIBLE);
            onQueryChangeListener.onQueryTextChange(newText.toString());
            //SearchAdapter.isFirst = false;

        } else {
            emptyBtn.setVisibility(GONE);
            if (onQueryChangeListener != null) {
                onQueryChangeListener.onQueryTextChange("");
            }
            return;
        }
        if (onQueryChangeListener != null && !TextUtils.equals(newText, oldQueryText)) {

        }

        oldQueryText = newText.toString();
    }

    private void onSubmitQuery() {
        CharSequence query = searchSrcTextView.getText();
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (onQueryChangeListener == null || !onQueryChangeListener.onQueryTextSubmit(query.toString())) {
                closeSearch();
                searchSrcTextView.setText(null);
            }
        }

    }

    private boolean isVoiceAvailable() {
        if (isInEditMode()) {
            return true;
        }
        PackageManager pm = getContext().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        return activities.size() != 0;
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1 && searchSrcTextView.hasFocus()) {
            searchSrcTextView.clearFocus();
        }
        searchSrcTextView.requestFocus();
        InputMethodManager imm = (InputMethodManager) searchSrcTextView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchSrcTextView, 0);
    }

    //Public Attributes

    @Override
    public void setBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            searchTopBar.setBackground(background);
        } else {
            searchTopBar.setBackgroundDrawable(background);
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        searchTopBar.setBackgroundColor(color);
    }

    public void setTextColor(int color) {
        searchSrcTextView.setTextColor(color);
    }

    public void setHintTextColor(int color) {
        searchSrcTextView.setHintTextColor(color);
    }

    public void setHint(CharSequence hint) {
        searchSrcTextView.setHint(hint);
    }


    public void setCloseIcon(Drawable drawable) {
        emptyBtn.setImageDrawable(drawable);
    }

    public void setCloseIconTint(int color) {
        emptyBtn.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    public void setBackIcon(Drawable drawable) {
        backBtn.setImageDrawable(drawable);
    }

    public void setBackIconTint(int color) {
        backBtn.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    public void setSuggestionIcon(Drawable drawable) {
        suggestionIcon = drawable;
    }

    public void setSuggestionRemove(Drawable drawable) {
        suggestionRemove = drawable;
    }

    public void setSuggestionSend(Drawable drawable) {
        suggestionSend = drawable;
    }

    public void setSuggestionIconTint(int color) {
        suggestionIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    public void setSuggestionRemoveTint(int color) {
        suggestionRemove.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    public void setSuggestionSendTint(int color) {
        suggestionSend.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    public void setSuggestionBackground(Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            suggestionListView.setBackground(background);
        } else {
            suggestionListView.setBackgroundDrawable(background);
        }
    }

    public void setSuggestionBackgroundColor(int color) {
        suggestionListView.setBackgroundColor(color);
    }

    public void setCursorDrawable(int drawable) {
        try {
            // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(searchSrcTextView, drawable);
        } catch (Exception ignored) {
        }
    }

    public void setVoiceSearch(boolean voiceSearch) {
        allowVoiceSearch = voiceSearch;
    }


    public void showSuggestions() {
        if (searchAdapter != null && searchAdapter.getCount() > 0 && suggestionListView.getVisibility() == GONE) {
            suggestionListView.setVisibility(VISIBLE);
        }
    }

    /**
     * Submit the query as soon as the user clicks the item.
     *
     * @param submit submit state
     */
    public void setSubmitOnClick(boolean submit) {
        this.submit = submit;
    }

    /**
     * Set Suggest List OnItemClickListener
     *
     * @param listener
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        suggestionListView.setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        suggestionListView.setOnItemLongClickListener(listener);

    }

    /**
     * Set Adapter for suggestions list. Should implement Filterable.
     *
     * @param adapter
     */
    public void setAdapter(SearchAdapter adapter) {
        searchAdapter = adapter;
        suggestionListView.setAdapter(adapter);
        startFilter(searchSrcTextView.getText());

    }

    /**
     * Set Adapter for suggestions list with the given suggestion array
     *
     * @param suggestions array of suggestions
     */
    public void setSuggestions(String[] suggestions, String[] lastSearches, boolean isFirst, SetSearchQuery callback, int textColor) {
        if (suggestions != null && suggestions.length > 0) {
            tintView.setVisibility(VISIBLE);
            final SearchAdapter adapter = new SearchAdapter(context, suggestions, suggestionIcon, suggestionSend, ellipsize, lastSearches, isFirst, callback, textColor, this);
            setAdapter(adapter);

            tintView.setVisibility(GONE);


        } else {
            tintView.setVisibility(GONE);
        }
    }

    /**
     * Dismiss the suggestions list.
     */
    public void dismissSuggestions() {
        if (suggestionListView.getVisibility() == VISIBLE) {

            suggestionListView.setVisibility(GONE);
        }
    }


    /**
     * Calling this will set the query to search text box. if submit is true, it'll submit the query.
     *
     * @param query
     * @param submit
     */
    public void setQuery(CharSequence query, boolean submit) {
        searchSrcTextView.setText(query);
        if (query != null) {
            searchSrcTextView.setSelection(searchSrcTextView.length());
            userQuery = query;
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery();
        }
    }


    /**
     * Call this method and pass the menu item so this class can handle click events for the Menu Item.
     *
     * @param menuItem
     */
    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSearch();
                return true;
            }
        });
    }

    /**
     * Return true if search is open
     *
     * @return
     */
    public boolean isSearchOpen() {
        return isSearchOpen;
    }

    /**
     * Sets animation duration. ONLY FOR PRE-LOLLIPOP!!
     *
     * @param duration duration of the animation
     */
    public void setAnimationDuration(int duration) {
        animationDuration = duration;
    }

    /**
     * Open Search View. This will animate the showing of the view.
     */
    public void showSearch() {
        showSearch(true);
    }

    /**
     * Open Search View. If animate is true, Animate the showing of the view.
     *
     * @param animate true for animate
     */
    public void showSearch(boolean animate) {
        if (isSearchOpen()) {
            return;
        }

        //Request Focus
        searchSrcTextView.setText(null);
        searchSrcTextView.requestFocus();

        if (animate) {
            setVisibleWithAnimation();

        }
        if (searchViewListener != null) {
            searchViewListener.onSearchViewShown();
        }
        isSearchOpen = true;
    }

    public void removeFromList(View v) {

    }

    private void setVisibleWithAnimation() {
        AnimationUtil.AnimationListener animationListener = new AnimationUtil.AnimationListener() {
            @Override
            public boolean onAnimationStart(View view) {
                return false;
            }

            @Override
            public boolean onAnimationEnd(View view) {
                // if (searchViewListener != null) {
                // searchViewListener.onSearchViewShown();
                // }
                return false;
            }

            @Override
            public boolean onAnimationCancel(View view) {
                return false;
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchLayout.setVisibility(View.VISIBLE);
            AnimationUtil.reveal(searchTopBar, animationListener);

        } else {
            AnimationUtil.fadeInView(searchLayout, animationDuration, animationListener);
        }
    }

    /**
     * Close search view.
     */
    public void closeSearch() {
        if (!isSearchOpen()) {
            return;
        }

        searchSrcTextView.setText(null);
        dismissSuggestions();
        clearFocus();

        searchLayout.setVisibility(GONE);
        if (searchViewListener != null) {
            searchViewListener.onSearchViewClosed();
        }
        isSearchOpen = false;

    }

    /**
     * Set this listener to listen to Query Change events.
     *
     * @param listener
     */
    public void setOnQueryTextListener(OnQueryTextListener listener) {
        onQueryChangeListener = listener;
    }

    /**
     * Set this listener to listen to Search View open and close events
     *
     * @param listener
     */
    public void setOnSearchViewListener(SearchViewListener listener) {
        searchViewListener = listener;
    }

    /**
     * Ellipsize suggestions longer than one line.
     *
     * @param ellipsize
     */
    public void setEllipsize(boolean ellipsize) {
        this.ellipsize = ellipsize;
    }

    @Override
    public void onFilterComplete(int count) {
        if (count > 0) {
            showSuggestions();
        } else {
            dismissSuggestions();
        }
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        // Don't accept focus if in the middle of clearing focus
        if (clearingFocus) return false;
        // Check if SearchView is focusable.
        if (!isFocusable()) return false;
        return searchSrcTextView.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void clearFocus() {
        clearingFocus = true;
        hideKeyboard(this);
        super.clearFocus();
        searchSrcTextView.clearFocus();
        clearingFocus = false;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        mSavedState = new SavedState(superState);
        mSavedState.query = userQuery != null ? userQuery.toString() : null;
        mSavedState.isSearchOpen = this.isSearchOpen;

        return mSavedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        mSavedState = (SavedState) state;

        if (mSavedState.isSearchOpen) {
            showSearch(false);
            setQuery(mSavedState.query, false);
        }

        super.onRestoreInstanceState(mSavedState.getSuperState());
    }

    static class SavedState extends BaseSavedState {
        String query;
        boolean isSearchOpen;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.query = in.readString();
            this.isSearchOpen = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(query);
            out.writeInt(isSearchOpen ? 1 : 0);
        }

        //required field that makes Parcelables from a Parcel
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public interface OnQueryTextListener {

        /**
         * Called when the user submits the query. This could be due to a key press on the
         * keyboard or due to pressing a submit button.
         * The listener can override the standard behavior by returning true
         * to indicate that it has handled the submit request. Otherwise return false to
         * let the SearchView handle the submission by launching any associated intent.
         *
         * @param query the query text that is to be submitted
         * @return true if the query has been handled by the listener, false to let the
         * SearchView perform the default action.
         */
        boolean onQueryTextSubmit(String query);

        /**
         * Called when the query text is changed by the user.
         *
         * @param newText the new content of the query text field.
         * @return false if the SearchView should perform the default action of showing any
         * suggestions if available, true if the action was handled by the listener.
         */
        boolean onQueryTextChange(String newText);
    }

    public interface SearchViewListener {
        void onSearchViewShown();

        void onSearchViewClosed();
    }

}
