package com.wolcano.musicplayer.music.widgets

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import com.miguelcatalan.materialsearchview.utils.AnimationUtil
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.mvp.listener.SetSearchQuery
import com.wolcano.musicplayer.music.ui.adapter.SearchAdapter

class MaterialSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(
    context, attrs
), Filter.FilterListener {
    private var menuItem: MenuItem? = null

    /**
     * Return true if search is open
     *
     * @return
     */
    var isSearchOpen = false
        private set
    private var animationDuration = 0
    private var clearingFocus = false

    //Views
    private var searchLayout: View? = null
    private var tintView: View? = null
    private var suggestionListView: ListView? = null
    var searchSrcTextView: EditText? = null
    private var backBtn: ImageButton? = null
    private var emptyBtn: ImageButton? = null
    private var searchTopBar: RelativeLayout? = null
    private var oldQueryText: CharSequence? = null
    private var userQuery: CharSequence? = null
    private var onQueryChangeListener: OnQueryTextListener? = null
    private var searchViewListener: SearchViewListener? = null
    private var searchAdapter: SearchAdapter? = null
    private var mSavedState: SavedState? = null
    private var submit = false
    private var ellipsize = false
    private var allowVoiceSearch = false
    private var suggestionIcon: Drawable? = null
    private var suggestionRemove: Drawable? = null
    private var suggestionSend: Drawable? = null

    private fun initStyle(attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(
            attrs,
            com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView,
            defStyleAttr,
            0
        )
        if (a != null) {
            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchBackground)) {
                background =
                    a.getDrawable(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchBackground)
            }
            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_android_textColor)) {
                setTextColor(
                    a.getColor(
                        com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_android_textColor,
                        0
                    )
                )
            }
            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_android_textColorHint)) {
                setHintTextColor(
                    a.getColor(
                        com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_android_textColorHint,
                        0
                    )
                )
            }
            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchCloseIcon)) {
                setCloseIcon(a.getDrawable(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchCloseIcon))
            }
            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchBackIcon)) {
                setBackIcon(a.getDrawable(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchBackIcon))
            }
            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchSuggestionBackground)) {
                setSuggestionBackground(a.getDrawable(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchSuggestionBackground))
            }
            if (a.hasValue(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchSuggestionIcon)) {
                setSuggestionIcon(a.getDrawable(com.miguelcatalan.materialsearchview.R.styleable.MaterialSearchView_searchSuggestionIcon))
            }
            if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionClose)) {
                setSuggestionRemove(a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionClose))
            }
            if (a.hasValue(R.styleable.MaterialSearchView_searchSuggestionSend)) {
                setSuggestionSend(a.getDrawable(R.styleable.MaterialSearchView_searchSuggestionSend))
            }
        }
    }

    private fun initiateView() {
        LayoutInflater.from(context).inflate(R.layout.searchview_layout, this, true)
        searchLayout = findViewById(R.id.search_layout)
        searchTopBar = searchLayout?.findViewById<View>(R.id.search_top_bar) as RelativeLayout
        suggestionListView = searchLayout?.findViewById<View>(R.id.suggestion_list) as ListView
        searchSrcTextView = searchLayout?.findViewById<View>(R.id.searchTextView) as EditText
        backBtn = searchLayout?.findViewById<View>(R.id.action_up_btn) as ImageButton
        emptyBtn = searchLayout?.findViewById<View>(R.id.action_empty_btn) as ImageButton
        tintView = searchLayout?.findViewById(R.id.transparent_view)
        searchSrcTextView?.setOnClickListener(mOnClickListener)
        backBtn?.setOnClickListener(mOnClickListener)
        emptyBtn?.setOnClickListener(mOnClickListener)
        tintView?.setOnClickListener(mOnClickListener)
        allowVoiceSearch = false
        initSearchView()
        suggestionListView?.visibility = GONE
        setAnimationDuration(AnimationUtil.ANIMATION_DURATION_MEDIUM)
    }

    private fun initSearchView() {
        searchSrcTextView?.setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
            onSubmitQuery()
            true
        }
        searchSrcTextView?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                userQuery = s
                startFilter(s)
                this@MaterialSearchView.onTextChanged(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        searchSrcTextView?.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showKeyboard()
                showSuggestions()
            }
        }
    }

    fun getListPosSend(position: Int): String {
        return suggestionListView!!.adapter.getItem(position).toString()
    }

    fun startFilter(s: CharSequence?) {
        if (searchAdapter != null && searchAdapter is Filterable) {
            (searchAdapter as Filterable).filter.filter(s, this)
        }
    }

    private val mOnClickListener = OnClickListener { v ->
        if (v === backBtn) {
            closeSearch()
        } else if (v === emptyBtn) {
            searchSrcTextView?.setText(null)
        } else if (v === searchSrcTextView) {
            showSuggestions()
        } else if (v === tintView) {
            closeSearch()
        } else if (v === suggestionListView) {
            removeFromList(v)
        }
    }

    private fun onTextChanged(newText: CharSequence) {
        val text: CharSequence = searchSrcTextView!!.text
        userQuery = text
        val hasText = !TextUtils.isEmpty(text)
        if (hasText) {
            emptyBtn?.visibility = VISIBLE
            onQueryChangeListener?.onQueryTextChange(newText.toString())
            //SearchAdapter.isFirst = false;
        } else {
            emptyBtn?.visibility = GONE
            onQueryChangeListener?.onQueryTextChange("")
            return
        }
        if (onQueryChangeListener != null && !TextUtils.equals(newText, oldQueryText)) {
        }
        oldQueryText = newText.toString()
    }

    private fun onSubmitQuery() {
        val query: CharSequence? = searchSrcTextView!!.text
        if (query != null && TextUtils.getTrimmedLength(query) > 0) {
            if (onQueryChangeListener == null || !onQueryChangeListener!!.onQueryTextSubmit(query.toString())) {
                closeSearch()
                searchSrcTextView?.text = null
            }
        }
    }

    private val isVoiceAvailable: Boolean
        private get() {
            if (isInEditMode) {
                return true
            }
            val pm = context.packageManager
            val activities = pm.queryIntentActivities(
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0
            )
            return activities.size != 0
        }

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard() {
        searchSrcTextView?.requestFocus()
        val imm =
            searchSrcTextView?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchSrcTextView, 0)
    }

    //Public Attributes
    override fun setBackground(background: Drawable) {
        searchTopBar?.background = background
    }

    override fun setBackgroundColor(color: Int) {
        searchTopBar?.setBackgroundColor(color)
    }

    fun setTextColor(color: Int) {
        searchSrcTextView?.setTextColor(color)
    }

    fun setHintTextColor(color: Int) {
        searchSrcTextView?.setHintTextColor(color)
    }

    fun setHint(hint: CharSequence?) {
        searchSrcTextView?.hint = hint
    }

    fun setCloseIcon(drawable: Drawable?) {
        emptyBtn?.setImageDrawable(drawable)
    }

    fun setCloseIconTint(color: Int) {
        emptyBtn?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    fun setBackIcon(drawable: Drawable?) {
        backBtn?.setImageDrawable(drawable)
    }

    fun setBackIconTint(color: Int) {
        backBtn?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    fun setSuggestionIcon(drawable: Drawable?) {
        suggestionIcon = drawable
    }

    fun setSuggestionRemove(drawable: Drawable?) {
        suggestionRemove = drawable
    }

    fun setSuggestionSend(drawable: Drawable?) {
        suggestionSend = drawable
    }

    fun setSuggestionIconTint(color: Int) {
        suggestionIcon?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    fun setSuggestionRemoveTint(color: Int) {
        suggestionRemove?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    fun setSuggestionSendTint(color: Int) {
        suggestionSend?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    fun setSuggestionBackground(background: Drawable?) {
        suggestionListView?.background = background
    }

    fun setSuggestionBackgroundColor(color: Int) {
        suggestionListView!!.setBackgroundColor(color)
    }

    fun setCursorDrawable(drawable: Int) {
        try {
            // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
            val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
            f.isAccessible = true
            f[searchSrcTextView] = drawable
        } catch (ignored: Exception) {
        }
    }

    fun setVoiceSearch(voiceSearch: Boolean) {
        allowVoiceSearch = voiceSearch
    }

    fun showSuggestions() {
        if (searchAdapter != null && searchAdapter!!.count > 0 && suggestionListView!!.visibility == GONE) {
            suggestionListView?.visibility = VISIBLE
        }
    }

    /**
     * Submit the query as soon as the user clicks the item.
     *
     * @param submit submit state
     */
    fun setSubmitOnClick(submit: Boolean) {
        this.submit = submit
    }

    /**
     * Set Suggest List OnItemClickListener
     *
     * @param listener
     */
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        suggestionListView?.onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: OnItemLongClickListener?) {
        suggestionListView?.onItemLongClickListener = listener
    }

    /**
     * Set Adapter for suggestions list. Should implement Filterable.
     *
     * @param adapter
     */
    fun setAdapter(adapter: SearchAdapter?) {
        searchAdapter = adapter
        suggestionListView?.adapter = adapter
        startFilter(searchSrcTextView!!.text)
    }

    /**
     * Set Adapter for suggestions list with the given suggestion array
     *
     * @param suggestions array of suggestions
     */
    fun setSuggestions(
        suggestions: Array<String>?,
        lastSearches: Array<String>?,
        isFirst: Boolean,
        callback: SetSearchQuery?,
        textColor: Int
    ) {
        if (suggestions != null && suggestions.isNotEmpty()) {
            tintView?.visibility = VISIBLE
            val adapter = SearchAdapter(
                context,
                suggestions,
                suggestionIcon,
                suggestionSend,
                ellipsize,
                lastSearches,
                isFirst,
                callback,
                textColor,
                this
            )
            setAdapter(adapter)
            tintView?.visibility = GONE
        } else {
            tintView?.visibility = GONE
        }
    }

    /**
     * Dismiss the suggestions list.
     */
    fun dismissSuggestions() {
        if (suggestionListView!!.visibility == VISIBLE) {
            suggestionListView!!.visibility = GONE
        }
    }

    /**
     * Calling this will set the query to search text box. if submit is true, it'll submit the query.
     *
     * @param query
     * @param submit
     */
    fun setQuery(query: CharSequence?, submit: Boolean) {
        searchSrcTextView!!.setText(query)
        if (query != null) {
            searchSrcTextView!!.setSelection(searchSrcTextView!!.length())
            userQuery = query
        }
        if (submit && !TextUtils.isEmpty(query)) {
            onSubmitQuery()
        }
    }

    /**
     * Call this method and pass the menu item so this class can handle click events for the Menu Item.
     *
     * @param menuItem
     */
    fun setMenuItem(menuItem: MenuItem) {
        this.menuItem = menuItem
        menuItem.setOnMenuItemClickListener {
            showSearch()
            true
        }
    }

    /**
     * Sets animation duration. ONLY FOR PRE-LOLLIPOP!!
     *
     * @param duration duration of the animation
     */
    fun setAnimationDuration(duration: Int) {
        animationDuration = duration
    }
    /**
     * Open Search View. If animate is true, Animate the showing of the view.
     *
     * @param animate true for animate
     */
    /**
     * Open Search View. This will animate the showing of the view.
     */
    @JvmOverloads
    fun showSearch(animate: Boolean = true) {
        if (isSearchOpen) {
            return
        }

        //Request Focus
        searchSrcTextView?.text = null
        searchSrcTextView?.requestFocus()
        if (animate) {
            setVisibleWithAnimation()
        }
        searchViewListener?.onSearchViewShown()
        isSearchOpen = true
    }

    fun removeFromList(v: View?) {}

    private fun setVisibleWithAnimation() {
        val animationListener: AnimationUtil.AnimationListener =
            object : AnimationUtil.AnimationListener {
                override fun onAnimationStart(view: View): Boolean {
                    return false
                }

                override fun onAnimationEnd(view: View): Boolean {
                    // if (searchViewListener != null) {
                    // searchViewListener.onSearchViewShown();
                    // }
                    return false
                }

                override fun onAnimationCancel(view: View): Boolean {
                    return false
                }
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchLayout?.visibility = VISIBLE
            AnimationUtil.reveal(searchTopBar, animationListener)
        } else {
            AnimationUtil.fadeInView(searchLayout, animationDuration, animationListener)
        }
    }

    /**
     * Close search view.
     */
    fun closeSearch() {
        if (!isSearchOpen) {
            return
        }
        searchSrcTextView?.text = null
        dismissSuggestions()
        clearFocus()
        searchLayout?.visibility = GONE
        searchViewListener?.onSearchViewClosed()
        isSearchOpen = false
    }

    /**
     * Set this listener to listen to Query Change events.
     *
     * @param listener
     */
    fun setOnQueryTextListener(listener: OnQueryTextListener?) {
        onQueryChangeListener = listener
    }

    /**
     * Set this listener to listen to Search View open and close events
     *
     * @param listener
     */
    fun setOnSearchViewListener(listener: SearchViewListener?) {
        searchViewListener = listener
    }

    /**
     * Ellipsize suggestions longer than one line.
     *
     * @param ellipsize
     */
    fun setEllipsize(ellipsize: Boolean) {
        this.ellipsize = ellipsize
    }

    override fun onFilterComplete(count: Int) {
        if (count > 0) {
            showSuggestions()
        } else {
            dismissSuggestions()
        }
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect): Boolean {
        // Don't accept focus if in the middle of clearing focus
        if (clearingFocus) return false
        // Check if SearchView is focusable.
        return if (!isFocusable) false else searchSrcTextView!!.requestFocus(
            direction,
            previouslyFocusedRect
        )
    }

    override fun clearFocus() {
        clearingFocus = true
        hideKeyboard(this)
        super.clearFocus()
        searchSrcTextView?.clearFocus()
        clearingFocus = false
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        mSavedState = SavedState(superState)
        mSavedState?.query = if (userQuery != null) userQuery.toString() else null
        mSavedState?.isSearchOpen = isSearchOpen
        return mSavedState
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        mSavedState = state
        if (mSavedState!!.isSearchOpen) {
            showSearch(false)
            setQuery(mSavedState!!.query, false)
        }
        super.onRestoreInstanceState(mSavedState!!.superState)
    }

    internal class SavedState : BaseSavedState {
        var query: String? = null
        var isSearchOpen = false

        constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            query = `in`.readString()
            isSearchOpen = `in`.readInt() == 1
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(query)
            out.writeInt(if (isSearchOpen) 1 else 0)
        }

        companion object {
            //required field that makes Parcelables from a Parcel
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState?> =
                object : Parcelable.Creator<SavedState?> {
                    override fun createFromParcel(`in`: Parcel): SavedState {
                        return SavedState(`in`)
                    }

                    override fun newArray(size: Int): Array<SavedState?> {
                        return arrayOfNulls(size)
                    }
                }
        }
    }

    interface OnQueryTextListener {
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
        fun onQueryTextSubmit(query: String): Boolean

        /**
         * Called when the query text is changed by the user.
         *
         * @param newText the new content of the query text field.
         * @return false if the SearchView should perform the default action of showing any
         * suggestions if available, true if the action was handled by the listener.
         */
        fun onQueryTextChange(newText: String): Boolean
    }

    interface SearchViewListener {
        fun onSearchViewShown()
        fun onSearchViewClosed()
    }

    companion object {
        var isRemoved = false
        var isEmpty = false
    }

    init {
        initiateView()
        initStyle(attrs, defStyleAttr)
    }
}