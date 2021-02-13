package com.wolcano.musicplayer.music.ui.fragment.online

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Constants.MAIN_BASE_URL
import com.wolcano.musicplayer.music.constants.Constants.MAIN_BASE_URL_2
import com.wolcano.musicplayer.music.databinding.FragmentOnlineBinding
import com.wolcano.musicplayer.music.model.SongOnline
import com.wolcano.musicplayer.music.mvp.DisposableManager
import com.wolcano.musicplayer.music.mvp.listener.RecyclerViewScrollListener
import com.wolcano.musicplayer.music.mvp.listener.SetSearchQuery
import com.wolcano.musicplayer.music.ui.adapter.OnlineAdapter
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.ui.helper.TintHelper
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.Utils.getAccentColor
import com.wolcano.musicplayer.music.utils.Utils.getAutoSearch
import com.wolcano.musicplayer.music.utils.Utils.getFirstFrag
import com.wolcano.musicplayer.music.utils.Utils.getLastSearch
import com.wolcano.musicplayer.music.utils.Utils.getLastSingleSearch
import com.wolcano.musicplayer.music.utils.Utils.getPrimaryColor
import com.wolcano.musicplayer.music.utils.Utils.getSearchQuery
import com.wolcano.musicplayer.music.utils.Utils.isColorLight
import com.wolcano.musicplayer.music.utils.Utils.setGetSearch
import com.wolcano.musicplayer.music.utils.Utils.setLastSearch
import com.wolcano.musicplayer.music.utils.Utils.setLastSingleSearch
import com.wolcano.musicplayer.music.utils.Utils.setSearchQuery
import com.wolcano.musicplayer.music.utils.Utils.setUpFastScrollRecyclerViewColor
import com.wolcano.musicplayer.music.widgets.MaterialSearchView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*

class OnlineFragment : BaseFragment(), SetSearchQuery {

    private var positionMore = 0
    private var txtSearch: String? = null
    private var control = false
    private var lCounter = 0
    private var errorStr: String? = null
    private var array: IntArray? = null
    private var isApproved = false
    private var arrayList: ArrayList<String>? = null
    private var arrayTitleList: ArrayList<String>? = null
    private var arrayArtistList: ArrayList<String>? = null
    private var arraySearchList: ArrayList<String>? = null
    private var lastSearches: ArrayList<String>? = null
    private var arrayList2: ArrayList<String>? = null
    private var arrayDuraList: ArrayList<String>? = null
    private var suggestionList: ArrayList<String>? = null
    private var arraySongOnlineList: ArrayList<SongOnline>? = null
    private var cInt = 1
    private var loadMore = false
    private var adt: ArrayAdapter<String>? = null
    private var adtTitle: ArrayAdapter<String>? = null
    private var adtDuration: ArrayAdapter<String>? = null
    private var adtArtist: ArrayAdapter<String>? = null
    private var adtSearchList: ArrayAdapter<String>? = null
    private var adt2: ArrayAdapter<String>? = null
    private var adtSongOnline: ArrayAdapter<SongOnline>? = null
    private var disposable: Disposable? = null
    private var onlineAdapter: OnlineAdapter? = null
    private var suggestionListStringsFromRemove: Array<String>? = null
    private var lastSearchesStringsFromRemove: Array<String>? = null
    private val durationQuery = "em.cplayer-data-sound-time"
    private var footerView: View? = null
    private var lastSearchList: List<String>? = null
    private var color = 0
    private var isitRemoved = false
    private val titleQuery = "b.cplayer-data-sound-title"
    private val baseQuery = "li[data-sound-url]"
    private val artistQuery = "i.cplayer-data-sound-author"
    private val singleQuery = "data-sound-url"
    private val `val` = "shine"

    private lateinit var binding: FragmentOnlineBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnlineBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        color = getPrimaryColor(requireContext())
        binding.toolbarContainer.bringToFront()

        setStatusbarColor(color, binding.statusBarCustom)
        setUpFastScrollRecyclerViewColor(
            binding.recyclerView, getAccentColor(
                requireContext()
            )
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        binding.recyclerView.addOnScrollListener(object : RecyclerViewScrollListener() {
            override fun onScrollUp() {}
            override fun onScrollDown() {}
            override fun onLoadMore() {
                loadMoreData()
            }
        })

        (requireActivity() as AppCompatActivity?)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setBackgroundColor(color)
        if (isColorLight(color)) {
            binding.toolbar.setTitleTextColor(Color.BLACK)
        } else {
            binding.toolbar.setTitleTextColor(Color.WHITE)
        }
        val ab = (requireActivity() as AppCompatActivity?)?.supportActionBar
        ab?.setTitle(R.string.onlineplayer)

        if (binding.toolbar.navigationIcon != null) {
            binding.toolbar.navigationIcon = TintHelper.createTintedDrawable(
                binding.toolbar.navigationIcon, ToolbarContentTintHelper.toolbarContentColor(
                    requireContext(), color
                )
            )
        }

        footerView = inflater.inflate(R.layout.thefooter_view, container, false)
        arrayList = ArrayList()
        arrayArtistList = ArrayList()
        arraySearchList = ArrayList()
        arrayList2 = ArrayList()
        arrayDuraList = ArrayList()
        arraySongOnlineList = ArrayList()
        arrayTitleList = ArrayList()

        binding.viewEmpty.visibility = View.VISIBLE
        setSearchView()
        binding.emptyText.text = requireContext().resources.getString(R.string.search_info)
        binding.recyclerView.visibility = View.GONE

        return binding.root
    }

    private fun loadMoreData() {
        if (!loadMore) {
            if (cInt <= 5 && arrayList!!.size >= 50 && !isApproved) {
                if (!loadMore) {
                    loadMore = true
                    cInt++
                    positionMore = binding.recyclerView.adapter!!.itemCount - 1
                    if (!control) {
                        isApproved = false
                        executeTask()
                    } else {
                        loadMore = false
                    }
                }
            } else {
                loadMore = false
            }
        }
    }

    private fun setSearchView() {
        binding.materialSearchLast.setSubmitOnClick(true)
        binding.materialSearchLast.setVoiceSearch(true)
        binding.progressBar.indeterminateTintList = ColorStateList.valueOf(
            getAccentColor(
                requireContext()
            )
        )
        binding.materialSearchLast.setHint(getString(R.string.searchhint))

        binding.materialSearchLast.setSuggestionIcon(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_history_white_24dp
            )
        )
        binding.materialSearchLast.setSuggestionRemove(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.baseline_close_black_36
            )
        )
        binding.materialSearchLast.setSuggestionSend(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_call_made_white_24dp
            )
        )
        val colorPrimary = getPrimaryColor(requireContext())
        val colorTint: Int
        if (isColorLight(colorPrimary)) {
            colorTint = Color.BLACK
            binding.materialSearchLast.setSuggestionIconTint(colorTint)
            binding.materialSearchLast.setSuggestionRemoveTint(colorTint)
            binding.materialSearchLast.setSuggestionSendTint(colorTint)
            binding.materialSearchLast.setCloseIconTint(colorTint)
            binding.materialSearchLast.setBackIconTint(colorTint)
            binding.materialSearchLast.setTextColor(colorTint)
            binding.materialSearchLast.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black_u6
                )
            )
        } else {
            colorTint = Color.WHITE
            binding.materialSearchLast.setSuggestionIconTint(colorTint)
            binding.materialSearchLast.setSuggestionRemoveTint(colorTint)
            binding.materialSearchLast.setSuggestionSendTint(colorTint)
            binding.materialSearchLast.setCloseIconTint(colorTint)
            binding.materialSearchLast.setBackIconTint(colorTint)
            binding.materialSearchLast.setTextColor(colorTint)
            binding.materialSearchLast.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey0
                )
            )
        }
        binding.materialSearchLast.setBackgroundColor(colorPrimary)
        val colorDrawable = ColorDrawable(colorPrimary)
        binding.materialSearchLast.setSuggestionBackground(colorDrawable)
        suggestionList = ArrayList()
        lastSearches = ArrayList()
        val str = getSearchQuery(requireContext())
        val getLast = getLastSearch(requireContext())
        if (str != null) {
            val list = str.split(",").toTypedArray().toList()
            suggestionList?.addAll(list)
        }
        if (getLast != null) {
            val list = getLast.split(",").toTypedArray().toList()
            lastSearches?.addAll(list)
        }
        if (!getFirstFrag(requireContext())) {
            handleAutoSearch()
        }
        binding.recyclerView.addOnScrollListener(object : RecyclerViewScrollListener() {
            override fun onScrollUp() {}
            override fun onScrollDown() {}
            override fun onLoadMore() {
                loadMoreData()
            }
        })
        binding.materialSearchLast.setOnQueryTextListener(object :
            MaterialSearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                val getLast = getLastSearch(
                    requireContext()
                )
                val alist: MutableList<String?> =
                    getLast!!.split(",").toTypedArray().toMutableList()
                alist.reverse()
                var k = 0
                val iterator = alist.iterator()
                var z = 0
                while (iterator.hasNext()) {
                    if (query.trim() == iterator.next()) {
                        iterator.remove()
                    }
                    if (z >= 10 && z <= (alist.size - 1)) {
                        k++
                    }
                    z++
                }
                var sy: Int
                val arrayList = ArrayList<String?>()
                arrayList.addAll(alist)
                if (k > 0) {
                    sy = 0
                    while (sy < k) {
                        arrayList.removeAt(sy)
                        sy++
                    }
                }
                alist.removeAll(alist)
                alist.addAll(arrayList)
                alist.add(query.trim())
                alist.reverse()
                val arr = alist.toTypedArray()
                val sb = StringBuilder()
                for (i in arr.indices) {
                    sb.append(arr[i]).append(",")
                }
                setLastSearch(requireContext(), sb.toString())
                if (!suggestionList!!.contains(query.trim())) {
                    suggestionList?.add(query.trim())
                }
                val arrf = suggestionList?.toTypedArray()
                val arrf2 = lastSearches?.toTypedArray()
                binding.materialSearchLast.setSuggestions(
                    arrf,
                    arrf2,
                    false,
                    this@OnlineFragment,
                    colorTint
                )
                val sbf = StringBuilder()
                for (i in arrf!!.indices) {
                    sbf.append(arrf[i]).append(",")
                }
                setSearchQuery(requireContext(), sbf.toString())
                setLastSingleSearch(requireContext(), query.trim())
                if (!query.trim().contains(`val`)) {
                    setSearchQuery(query.trim())
                } else if (query.trim().contains(`val`)) {
                    if (query.trim().isNotEmpty()) {
                        binding.toolbar.title = query.trim()
                    }
                    binding.viewEmpty.visibility = View.GONE
                    binding.emptyText.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                    val delay = (2 + Random().nextInt(3)) * 1000
                    val handler = Handler()
                    handler.postDelayed({
                        binding.progressBar.visibility = View.GONE
                        binding.emptyText.setText(R.string.no_result)
                        binding.emptyText.visibility = View.VISIBLE
                    }, delay.toLong())
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText == "") {
                    val arr: Array<String> = suggestionList!!.toTypedArray()
                    MaterialSearchView.isEmpty = true
                    val str = getLastSearch(
                        requireContext()
                    )
                    if (str != null) {
                        lastSearchList = listOf(*str.split(",").toTypedArray())
                        val lastSearches: Array<String>? = if (str.isEmpty()) {
                            null
                        } else {
                            lastSearchList?.toTypedArray()
                        }
                        binding.materialSearchLast.setSuggestions(
                            arr,
                            lastSearches,
                            true,
                            this@OnlineFragment,
                            colorTint
                        )
                    }
                    MaterialSearchView.isEmpty = false
                } else {
                    binding.materialSearchLast.setSuggestions(
                        suggestionListStringsFromRemove,
                        lastSearchesStringsFromRemove,
                        false,
                        this@OnlineFragment,
                        colorTint
                    )
                    if (suggestionListStringsFromRemove != null && lastSearchesStringsFromRemove != null) {
                        binding.materialSearchLast.setSuggestions(
                            suggestionListStringsFromRemove,
                            lastSearchesStringsFromRemove,
                            false,
                            this@OnlineFragment,
                            colorTint
                        )
                        suggestionListStringsFromRemove = null
                        lastSearchesStringsFromRemove = null
                    }
                }
                return false
            }


        })
    }

    private fun handleAutoSearch() {
        val getAuto = getAutoSearch(requireContext())
        if (getAuto) {
            val lastSingleSearch = getLastSingleSearch(
                requireContext()
            )
            if (lastSingleSearch!!.isNotEmpty() && !lastSingleSearch.trim()
                    .contains(`val`)
            ) {
                setSearchQuery(lastSingleSearch.trim())
            } else if (lastSingleSearch.trim().contains(`val`)) {
                if (lastSingleSearch.isNotEmpty()) {
                    binding.toolbar.title = lastSingleSearch.trim()
                }
                binding.recyclerView.visibility = View.GONE
                binding.viewEmpty.visibility = View.GONE
                binding.emptyText.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                val delay = (2 + Random().nextInt(3)) * 1000
                val handler = Handler()
                handler.postDelayed({
                    binding.progressBar.visibility = View.GONE
                    binding.emptyText.setText(R.string.no_result)
                    binding.emptyText.visibility = View.VISIBLE
                }, delay.toLong())
            }
        }
    }

    private fun removeSuggestion(text: String) {
        val str = getSearchQuery(requireContext())
        val getLast = getLastSearch(requireContext())
        val getLastSingle = getLastSingleSearch(
            requireContext()
        )
        suggestionList?.clear()
        if (getLastSingle == text) {
            setLastSingleSearch(requireContext(), "")
        }
        lastSearches?.clear()
        if (str != null) {
            val list = listOf(*str.split(",").toTypedArray())
            suggestionList?.addAll(list)
        }
        if (getLast != null) {
            val list = listOf(*getLast.split(",").toTypedArray())
            lastSearches?.addAll(list)
        }
        if (lastSearches!!.contains(text.trim())) {
            while (lastSearches!!.iterator().hasNext()) {
                if (lastSearches!!.iterator().next().contains(text.trim())) {
                    lastSearches!!.iterator().remove()
                }
            }
        }
        if (suggestionList!!.contains(text.trim())) {
            while (suggestionList!!.iterator().hasNext()) {
                if (suggestionList!!.iterator().next().contains(text.trim())) {
                    suggestionList!!.iterator().remove()
                }
            }
        }
        suggestionListStringsFromRemove = suggestionList!!.toTypedArray()
        lastSearchesStringsFromRemove = lastSearches!!.toTypedArray()
        MaterialSearchView.isRemoved = true
        val sb = java.lang.StringBuilder()
        for (i in suggestionListStringsFromRemove!!.indices) {
            sb.append(suggestionListStringsFromRemove!![i]).append(",")
        }
        val sb2 = java.lang.StringBuilder()
        for (i in lastSearchesStringsFromRemove!!.indices) {
            sb2.append(lastSearchesStringsFromRemove!![i]).append(",")
        }
        setSearchQuery(requireContext(), sb.toString())
        setLastSearch(requireContext(), sb2.toString())
        MaterialSearchView.isRemoved = false
    }


    private fun setSearchQuery(query: String) {
        binding.viewEmpty.visibility = View.GONE
        binding.emptyText.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
        binding.toolbar.title = query
        RecyclerViewScrollListener.previousTotal = 0
        isApproved = true
        setGetSearch(requireContext(), query)
        if (!loadMore) {
            if (arrayList != null && adt != null) {
                arrayList?.clear()
                adt?.notifyDataSetChanged()
            }
            if (arrayList2 != null && adt2 != null) {
                arrayList2?.clear()
                adt2?.notifyDataSetChanged()
            }
            if (arraySearchList != null && adtSearchList != null) {
                arraySearchList?.clear()
                adtSearchList?.notifyDataSetChanged()
            }
            if (arrayArtistList != null && adtArtist != null) {
                arrayArtistList?.clear()
                adtArtist?.notifyDataSetChanged()
            }
            if (arrayTitleList != null && adtTitle != null) {
                arrayTitleList?.clear()
                adtTitle?.notifyDataSetChanged()
            }
            if (arrayDuraList != null && adtDuration != null) {
                arrayDuraList?.clear()
                adtDuration?.notifyDataSetChanged()
            }
            if (arraySongOnlineList != null && adtSongOnline != null) {
                arraySongOnlineList?.clear()
                adtSongOnline?.notifyDataSetChanged()
            }
            onlineAdapter?.clear()
            disposable?.dispose()
            lCounter = 0
            positionMore = 0
            onlineAdapter?.showLoading(false)
            txtSearch = query
            cInt = 1
            binding.materialSearchLast.clearFocus()
            executeTask()
            isApproved = false
        }
    }

    private fun isConnected(): Boolean {
        val networkInfo: NetworkInfo
        var check = false
        val connectivityManager: ConnectivityManager
        if (activity != null) {
            connectivityManager =
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            networkInfo = connectivityManager.activeNetworkInfo
            check = networkInfo != null && networkInfo.isConnectedOrConnecting
        }
        return check
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_online, menu)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireActivity(), binding.toolbar, menu, ColorUtils.getToolbarBackgroundColor(
                binding.toolbar
            )
        )
        if (isColorLight(color)) {
            binding.materialSearchLast.setBackgroundColor(ColorUtils.shiftColor(color, 0.7f))
            binding.materialSearchLast.setSuggestionBackgroundColor(
                ColorUtils.shiftColor(
                    color,
                    0.7f
                )
            )
        } else {
            binding.materialSearchLast.setBackgroundColor(color)
            binding.materialSearchLast.setSuggestionBackgroundColor(color)
        }
        val searchItem = menu.findItem(R.id.action_searchM)
        binding.materialSearchLast.setMenuItem(searchItem)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onSearchQuery(position: Int, isFirst: Boolean) {
        binding.materialSearchLast.setQuery(
            binding.materialSearchLast.getListPosSend(position),
            isFirst
        )
    }

    override fun onRemoveSuggestion(position: Int, whichList: Int) {
        removeSuggestion(binding.materialSearchLast.getListPosSend(position))
    }


    private fun addToArrayAdapter() {
        adt = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, arrayList)
        adt2 = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, arrayList2)
        adtArtist =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, arrayArtistList)
        adtTitle =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, arrayTitleList)
        adtDuration =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, arrayDuraList)
        adtSearchList =
            ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, arraySearchList)
    }

    fun closeSearch() {
        binding.materialSearchLast.closeSearch()
    }

    fun isSearchOpen(): Boolean {
        return binding.materialSearchLast.isSearchOpen
    }

    private fun addToArrayList(
        str1: String,
        str2: String,
        str3: String,
        str4: String,
        str5: String
    ) {
        arrayList?.add(str1)
        arrayList2?.add(str2)
        arraySearchList?.add(str2)
        arrayArtistList?.add(str3)
        arrayTitleList?.add(str4)
        arrayDuraList?.add(str5)
    }

    private fun executeTask() {
        if (!loadMore) {
            binding.progressBar.visibility = View.VISIBLE
        }
        disposable = Observable.fromCallable {
            try {
                if (isConnected()) {
                    val doc: Document? = if (cInt == 1) {
                        Jsoup.connect("$MAIN_BASE_URL$txtSearch/").timeout(10000)
                            .ignoreHttpErrors(true).get()
                    } else {
                        Jsoup.connect(
                            "$MAIN_BASE_URL$txtSearch/$MAIN_BASE_URL_2$cInt/"
                        ).timeout(10000).ignoreHttpErrors(true).get()
                    }
                    if (doc != null) {
                        val baseElements = doc.select(baseQuery)
                        val artistElements = doc.select(artistQuery)
                        val titleElements = doc.select(titleQuery)
                        val durationElements = doc.select(durationQuery)
                        for (j in baseElements.indices) {
                            val title = titleElements[j].text()
                            val artist = artistElements[j].text()
                            val duration = durationElements[j].text()
                            val baseUrl = baseElements[j].attr(singleQuery)
                            addToArrayList("$title\n $artist\n", baseUrl, artist, title, duration)
                        }
                        addToArrayAdapter()
                        checkAdapter()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            false
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            { o: Boolean? -> postMain() }
        ) { obj: Throwable -> obj.printStackTrace() }
    }

    private fun checkAdapter() {
        control = adtSearchList!!.count < 49
    }

    private fun postMain() {
        if (arraySongOnlineList != null && adtSongOnline != null) {
            arraySongOnlineList?.clear()
            adtSongOnline?.notifyDataSetChanged()
        }
        if (arrayList != null) {
            array = IntArray(arrayList!!.size)
            for (k in arrayList!!.indices) {
                if (adtDuration != null) {
                    val duration = java.lang.StringBuilder(adtDuration?.getItem(k))
                    var i = 0
                    while (i < duration.length - 1) {
                        val current = duration[i]
                        if (i == 0 && current == '0') {
                            duration.deleteCharAt(i)
                        }
                        if (current == ':') {
                            duration.deleteCharAt(i)
                        } else {
                            i++
                        }
                    }
                    val durationAsInt = duration.toString().toInt()
                    val strStage1 = durationAsInt / 100
                    val strStage2 = durationAsInt % 100
                    val finalStage = strStage1 * 60 + strStage2
                    array!![k] = k
                    val songOnline = SongOnline(
                        adtSearchList!!.getItem(k), adtArtist!!.getItem(k), adtTitle!!.getItem(k),
                        finalStage.toLong()
                    )
                    arraySongOnlineList?.add(songOnline)
                    adtSongOnline?.notifyDataSetInvalidated()
                    adtSongOnline =
                        ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            arraySongOnlineList
                        )
                }
            }
        }
        binding.recyclerView.requestLayout()
        binding.recyclerView.invalidate()
        loadMore = false
        footerView?.visibility = View.GONE
        if (!isConnected()) {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(
                requireContext().applicationContext,
                R.string.cnn_err,
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            if (arrayList?.size == 0) {
                isitRemoved = true
                if (errorStr == getString(R.string.http_error)) {
                    Toast.makeText(
                        requireContext().applicationContext,
                        R.string.cnn_err,
                        Toast.LENGTH_SHORT
                    ).show()
                    errorStr = ""
                } else {
                    controlIfEmpty()
                }
            } else {
                isitRemoved = false
            }
            if (!isitRemoved) {
                binding.emptyText.visibility = View.GONE
                if (positionMore == 0 && cInt == 1) {
                    if (binding.recyclerView.adapter == null) {
                        createOnlineAdapter()
                        if (arraySongOnlineList!!.size >= 15) {
                            binding.recyclerView.setThumbEnabled(true)
                        } else {
                            binding.recyclerView.setThumbEnabled(false)
                        }
                        onlineAdapter?.notifyDataSetChanged()
                        runLayoutAnimation(binding.recyclerView)
                    }
                } else if (cInt in 2..5) {
                    if (arraySongOnlineList!!.size >= 15) {
                        binding.recyclerView.setThumbEnabled(true)
                    } else {
                        binding.recyclerView.setThumbEnabled(false)
                    }
                    onlineAdapter?.notifyDataSetChanged()
                }
                if (lCounter == adtSearchList!!.count) {
                    control = true
                }
                lCounter = adtSearchList!!.count
                if (!control && cInt <= 5) {
                    onlineAdapter?.showLoading(true)
                    if (arraySongOnlineList!!.size >= 15) {
                        binding.recyclerView.setThumbEnabled(true)
                    } else {
                        binding.recyclerView.setThumbEnabled(false)
                    }
                    onlineAdapter?.notifyDataSetChanged()
                } else {
                    onlineAdapter?.showLoading(false)
                    if (arraySongOnlineList!!.size >= 15) {
                        binding.recyclerView.setThumbEnabled(true)
                    } else {
                        binding.recyclerView.setThumbEnabled(false)
                    }
                    onlineAdapter?.notifyDataSetChanged()
                    runLayoutAnimation(binding.recyclerView)
                }
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
    }


    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    private fun controlIfEmpty() {
        binding.emptyText.setText(R.string.no_result)
        binding.emptyText.visibility =
            if (onlineAdapter == null || onlineAdapter?.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun setOnlineAdapter() {
        onlineAdapter =
            OnlineAdapter((requireActivity() as AppCompatActivity?)!!, arraySongOnlineList)
        binding.recyclerView.adapter = onlineAdapter
    }

    private fun createOnlineAdapter() {
        setOnlineAdapter()
    }

    override fun onDestroyView() {
        if (binding.materialSearchLast.isSearchOpen) {
            binding.materialSearchLast.closeSearch()
        }
        disposable?.dispose()
        DisposableManager.dispose()
        super.onDestroyView()
    }

}