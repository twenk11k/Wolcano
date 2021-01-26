
package com.wolcano.musicplayer.music.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.TintHelper;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.FragmentOnlineBinding;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.listener.RecyclerViewScrollListener;
import com.wolcano.musicplayer.music.mvp.listener.SetSearchQuery;
import com.wolcano.musicplayer.music.mvp.models.SongOnline;
import com.wolcano.musicplayer.music.ui.adapter.OnlineAdapter;
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment;
import com.wolcano.musicplayer.music.utils.Utils;
import com.wolcano.musicplayer.music.widgets.MaterialSearchLast;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.wolcano.musicplayer.music.constants.Constants.MAIN_BASE_URL;
import static com.wolcano.musicplayer.music.constants.Constants.MAIN_BASE_URL_2;

public class FragmentOnline extends BaseFragment implements SetSearchQuery {

    private Activity activity;
    private Context context;
    private int positionMore = 0;
    private String txtSearch;
    private boolean control = false;
    private int lCounter = 0;
    private String errorStr;
    private int[] array;
    private boolean isApproved = false;
    private ArrayList<String> arrayList;
    private ArrayList<String> arrayTitleList;
    private ArrayList<String> arrayArtistList;
    private ArrayList<String> arraySearchList;
    private ArrayList<String> lastSearches;
    private ArrayList<String> arrayList2;
    private ArrayList<String> arrayDuraList;
    private ArrayList<String> suggestionList;
    private ArrayList<SongOnline> arraySongOnlineList;
    private int cInt = 1;
    private boolean loadMore = false;
    private ArrayAdapter<String> adt;
    private ArrayAdapter<String> adtTitle;
    private ArrayAdapter<String> adtDuration;
    private ArrayAdapter<String> adtArtist;
    private ArrayAdapter<String> adtSearchList;
    private ArrayAdapter<String> adt2;
    private ArrayAdapter<SongOnline> adtSongOnline;
    private Disposable disposable;
    private OnlineAdapter onlineAdapter;
    private String[] suggestionListStringsFromRemove;
    private String[] lastSearchesStringsFromRemove;
    private String durationQuery = "em.cplayer-data-sound-time";
    private View footerView;
    private List<String> lastSearchList;
    private int color;
    private boolean isitRemoved = false;
    private String titleQuery = "b.cplayer-data-sound-title";
    private String baseQuery = "li[data-sound-url]";
    private String artistQuery = "i.cplayer-data-sound-author";
    private String singleQuery = "data-sound-url";
    private String val = "shine";

    private FragmentOnlineBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOnlineBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);

        activity = getActivity();
        context = getContext();
        color = Utils.getPrimaryColor(context);
        binding.toolbarContainer.bringToFront();

        setStatusbarColorAuto(binding.statusBarCustom, color);
        Utils.setUpFastScrollRecyclerViewColor(binding.recyclerView, Utils.getAccentColor(context));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));


        binding.recyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        binding.toolbar.setBackgroundColor(color);
        if (Utils.isColorLight(color)) {
            binding.toolbar.setTitleTextColor(Color.BLACK);
        } else {
            binding.toolbar.setTitleTextColor(Color.WHITE);
        }
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(R.string.onlineplayer);

        if (binding.toolbar.getNavigationIcon() != null) {
            binding.toolbar.setNavigationIcon(TintHelper.createTintedDrawable(binding.toolbar.getNavigationIcon(), ToolbarContentTintHelper.toolbarContentColor(getContext(), color)));
        }

        footerView = inflater.inflate(R.layout.thefooter_view, container, false);
        arrayList = new ArrayList<>();
        arrayArtistList = new ArrayList<>();
        arraySearchList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        arrayDuraList = new ArrayList<>();
        arraySongOnlineList = new ArrayList<>();
        arrayTitleList = new ArrayList<>();

        binding.viewEmpty.setVisibility(View.VISIBLE);
        setSearchView();
        binding.emptyText.setText(context.getResources().getString(R.string.search_info));
        binding.recyclerView.setVisibility(View.GONE);

        return binding.getRoot();
    }

    private void loadMoreData() {
        if (!loadMore) {
            if (cInt <= 5 && arrayList.size() >= 50 && !isApproved) {
                if (!loadMore) {
                    loadMore = true;
                    cInt++;
                    positionMore = binding.recyclerView.getAdapter().getItemCount() - 1;
                    if (!control) {
                        isApproved = false;
                        executeTask();
                    } else {
                        loadMore = false;
                    }
                }
            } else {
                loadMore = false;
            }
        }
    }

    private void setSearchView() {

        binding.materialSearchLast.setSubmitOnClick(true);
        binding.materialSearchLast.setVoiceSearch(true);
        binding.progressBar.setIndeterminateTintList(ColorStateList.valueOf(Utils.getAccentColor(context)));
        binding.materialSearchLast.setHint(getString(R.string.searchhint));
        binding.materialSearchLast.setSuggestionIcon(getResources().getDrawable(R.drawable.ic_history_white_24dp));
        binding.materialSearchLast.setSuggestionRemove(getResources().getDrawable(R.drawable.baseline_close_black_36));
        binding.materialSearchLast.setSuggestionSend(getResources().getDrawable(R.drawable.ic_call_made_white_24dp));
        int colorPrimary = Utils.getPrimaryColor(context);
        int colorTint;
        if (Utils.isColorLight(colorPrimary)) {
            colorTint = Color.BLACK;
            binding.materialSearchLast.setSuggestionIconTint(colorTint);
            binding.materialSearchLast.setSuggestionRemoveTint(colorTint);
            binding.materialSearchLast.setSuggestionSendTint(colorTint);
            binding.materialSearchLast.setCloseIconTint(colorTint);
            binding.materialSearchLast.setBackIconTint(colorTint);
            binding.materialSearchLast.setTextColor(colorTint);
            binding.materialSearchLast.setHintTextColor(ContextCompat.getColor(context, R.color.black_u6));

        } else {
            colorTint = Color.WHITE;
            binding.materialSearchLast.setSuggestionIconTint(colorTint);
            binding.materialSearchLast.setSuggestionRemoveTint(colorTint);
            binding.materialSearchLast.setSuggestionSendTint(colorTint);
            binding.materialSearchLast.setCloseIconTint(colorTint);
            binding.materialSearchLast.setBackIconTint(colorTint);
            binding.materialSearchLast.setTextColor(colorTint);
            binding.materialSearchLast.setHintTextColor(ContextCompat.getColor(context, R.color.grey0));

        }
        binding.materialSearchLast.setBackgroundColor(colorPrimary);
        ColorDrawable colorDrawable = new ColorDrawable(colorPrimary);
        binding.materialSearchLast.setSuggestionBackground(colorDrawable);
        suggestionList = new ArrayList<>();
        lastSearches = new ArrayList<>();
        String str = Utils.getSearchQuery(context);
        String getLast = Utils.getLastSearch(context);

        if (str != null) {
            List<String> list = Arrays.asList(str.split(","));
            suggestionList.addAll(list);
        }
        if (getLast != null) {
            List<String> list = Arrays.asList(getLast.split(","));
            lastSearches.addAll(list);
        }
        if (!Utils.getFirstFrag(context)) {
            handleAutoSearch();

        }
        binding.recyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
            @Override
            public void onScrollUp() {

            }

            @Override
            public void onScrollDown() {

            }

            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });

        binding.materialSearchLast.setOnQueryTextListener(new MaterialSearchLast.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String getLast = Utils.getLastSearch(context);
                List<String> alist = new ArrayList<>(Arrays.asList(getLast.split(",")));
                Collections.reverse(alist);
                int k = 0;

                for (int z = 0; z < alist.size(); z++) {

                    if (query.trim().equals(alist.get(z))) {
                        alist.remove(z);
                    }
                    if (z >= 10 && z <= (alist.size() - 1)) {
                        k++;
                    }

                }
                int sy;
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.addAll(alist);

                if (k > 0) {
                    for (sy = 0; sy < k; sy++) {
                        arrayList.remove(sy);
                    }
                }

                alist.removeAll(alist);
                alist.addAll(arrayList);
                alist.add(query.trim());
                Collections.reverse(alist);
                String[] arr = alist.toArray(new String[alist.size()]);
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < arr.length; i++) {

                    sb.append(arr[i]).append(",");

                }
                Utils.setLastSearch(context, sb.toString());
                if (!suggestionList.contains(query.trim())) {
                    suggestionList.add(query.trim());

                }
                String[] arrf = suggestionList.toArray(new String[suggestionList.size()]);
                String[] arrf2 = lastSearches.toArray(new String[lastSearches.size()]);

                binding.materialSearchLast.setSuggestions(arrf, arrf2, false, FragmentOnline.this, colorTint);
                StringBuilder sbf = new StringBuilder();

                for (int i = 0; i < arrf.length; i++) {

                    sbf.append(arrf[i]).append(",");

                }

                Utils.setSearchQuery(context, sbf.toString());
                Utils.setLastSingleSearch(context, query.trim());
                if (!query.trim().contains(val)) {
                    setSearchQuery(query.trim());
                } else if (query.trim().contains(val)) {
                    if (!query.trim().isEmpty()) {
                        binding.toolbar.setTitle(query.trim());
                    }
                    binding.viewEmpty.setVisibility(View.GONE);
                    binding.emptyText.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                    int delay = (2 + new Random().nextInt(3)) * 1000;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.progressBar.setVisibility(View.GONE);
                            if (binding.emptyText != null) {
                                binding.emptyText.setText(R.string.no_result);
                                binding.emptyText.setVisibility(View.VISIBLE);
                            }
                        }
                    }, delay);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    String[] arr;
                    arr = suggestionList.toArray(new String[suggestionList.size()]);
                    MaterialSearchLast.isEmpty = true;

                    String str = Utils.getLastSearch(context);
                    if (str != null) {
                        lastSearchList = Arrays.asList(str.split(","));
                        String[] lastSearches;
                        if (str.isEmpty()) {
                            lastSearches = null;
                        } else {
                            lastSearches = lastSearchList.toArray(new String[lastSearchList.size()]);
                        }
                        binding.materialSearchLast.setSuggestions(arr, lastSearches, true, FragmentOnline.this, colorTint);
                    }
                    MaterialSearchLast.isEmpty = false;

                } else {

                    binding.materialSearchLast.setSuggestions(suggestionListStringsFromRemove, lastSearchesStringsFromRemove, false, FragmentOnline.this, colorTint);

                    if (suggestionListStringsFromRemove != null && lastSearchesStringsFromRemove != null) {
                        binding.materialSearchLast.setSuggestions(suggestionListStringsFromRemove, lastSearchesStringsFromRemove, false, FragmentOnline.this, colorTint);
                        suggestionListStringsFromRemove = null;
                        lastSearchesStringsFromRemove = null;
                    }
                }

                return false;
            }
        });

    }

    private void handleAutoSearch() {

        boolean getAuto = Utils.getAutoSearch(context);
        if (getAuto) {
            String last_single_search = Utils.getLastSingleSearch(context);
            if (!last_single_search.isEmpty() && !last_single_search.trim().contains(val)) {
                setSearchQuery(last_single_search.trim());
            } else if (last_single_search.trim().contains(val)) {
                if (!last_single_search.isEmpty()) {
                    binding.toolbar.setTitle(last_single_search.trim());
                }
                binding.recyclerView.setVisibility(View.GONE);
                binding.viewEmpty.setVisibility(View.GONE);
                binding.emptyText.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                int delay = (2 + new Random().nextInt(3)) * 1000;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.progressBar.setVisibility(View.GONE);
                        if (binding.emptyText != null) {
                            binding.emptyText.setText(R.string.no_result);
                            binding.emptyText.setVisibility(View.VISIBLE);
                        }
                    }
                }, delay);
            }
        }
    }

    private void removeSuggestion(String text) {

        String str = Utils.getSearchQuery(context);
        String getLast = Utils.getLastSearch(context);
        String getLastSingle = Utils.getLastSingleSearch(context);
        suggestionList.clear();
        if (getLastSingle.equals(text)) {
            Utils.setLastSingleSearch(context, "");
        }
        lastSearches.clear();
        if (str != null) {
            List<String> list = Arrays.asList(str.split(","));

            suggestionList.addAll(list);
        }
        if (getLast != null) {
            List<String> list = Arrays.asList(getLast.split(","));

            lastSearches.addAll(list);
        }
        if (lastSearches.contains(text.trim())) {
            for (int i = 0; i < lastSearches.size(); i++) {
                if (lastSearches.get(i).contains(text.trim())) {
                    lastSearches.remove(i);
                }
            }
        }
        if (suggestionList.contains(text.trim())) {
            for (int i = 0; i < suggestionList.size(); i++) {
                if (suggestionList.get(i).contains(text.trim())) {
                    suggestionList.remove(i);
                }
            }
        }
        suggestionListStringsFromRemove = suggestionList.toArray(new String[suggestionList.size()]);
        lastSearchesStringsFromRemove = lastSearches.toArray(new String[lastSearches.size()]);
        MaterialSearchLast.isRemoved = true;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < suggestionListStringsFromRemove.length; i++) {

            sb.append(suggestionListStringsFromRemove[i]).append(",");

        }
        StringBuilder sb2 = new StringBuilder();

        for (int i = 0; i < lastSearchesStringsFromRemove.length; i++) {
            sb2.append(lastSearchesStringsFromRemove[i]).append(",");
        }

        Utils.setSearchQuery(context, sb.toString());
        Utils.setLastSearch(context, sb2.toString());
        MaterialSearchLast.isRemoved = false;

    }

    private void setSearchQuery(String query) {

        binding.viewEmpty.setVisibility(View.GONE);
        binding.emptyText.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);

        binding.toolbar.setTitle(query);

        RecyclerViewScrollListener.previousTotal = 0;
        isApproved = true;
        Utils.setGetSearch(context, query);
        if (!loadMore) {
            if (arrayList != null && adt != null) {
                arrayList.clear();
                adt.notifyDataSetChanged();
            }
            if (arrayList2 != null && adt2 != null) {
                arrayList2.clear();
                adt2.notifyDataSetChanged();
            }
            if (arraySearchList != null && adtSearchList != null) {
                arraySearchList.clear();
                adtSearchList.notifyDataSetChanged();
            }
            if (arrayArtistList != null && adtArtist != null) {
                arrayArtistList.clear();
                adtArtist.notifyDataSetChanged();
            }
            if (arrayTitleList != null && adtTitle != null) {
                arrayTitleList.clear();
                adtTitle.notifyDataSetChanged();
            }
            if (arrayDuraList != null && adtDuration != null) {
                arrayDuraList.clear();
                adtDuration.notifyDataSetChanged();
            }
            if (arraySongOnlineList != null && adtSongOnline != null) {
                arraySongOnlineList.clear();
                adtSongOnline.notifyDataSetChanged();
            }
            if (onlineAdapter != null) {
                onlineAdapter.clear();
            }

            if (disposable != null)
                disposable.dispose();
            lCounter = 0;
            positionMore = 0;
            if (onlineAdapter != null) {
                onlineAdapter.showLoading(false);
            }
            txtSearch = query;
            cInt = 1;
            binding.materialSearchLast.clearFocus();
            executeTask();
            isApproved = false;

        }

    }

    private boolean isConnected() {

        NetworkInfo networkInfo;
        Boolean check = false;
        ConnectivityManager connectivityManager;

        if (activity != null) {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getActiveNetworkInfo();
            check = networkInfo != null && networkInfo.isConnectedOrConnecting();
        }

        return check;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        inflater.inflate(R.menu.menu_online, menu);
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), binding.toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(binding.toolbar));
        if (Utils.isColorLight(color)) {
            binding.materialSearchLast.setBackgroundColor(com.kabouzeid.appthemehelper.util.ColorUtil.shiftColor(color, 0.7F));
            binding.materialSearchLast.setSuggestionBackgroundColor(com.kabouzeid.appthemehelper.util.ColorUtil.shiftColor(color, 0.7F));
        } else {
            binding.materialSearchLast.setBackgroundColor(color);
            binding.materialSearchLast.setSuggestionBackgroundColor(color);
        }

        MenuItem searchItem = menu.findItem(R.id.action_searchM);
        binding.materialSearchLast.setMenuItem(searchItem);
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public void onSearchQuery(int position, boolean isFirst) {
        binding.materialSearchLast.setQuery(binding.materialSearchLast.getListPosSend(position), isFirst);
    }

    @Override
    public void onRemoveSuggestion(int position, int whichList) {

        removeSuggestion(binding.materialSearchLast.getListPosSend(position));

    }

    private void addToArrayAdapter() {
        adt = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arrayList);
        adt2 = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arrayList2);
        adtArtist = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arrayArtistList);
        adtTitle = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arrayTitleList);
        adtDuration = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arrayDuraList);
        adtSearchList = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arraySearchList);

    }

    public void closeSearch() {
        if (binding.materialSearchLast != null) {
            binding.materialSearchLast.closeSearch();
        }
    }

    public boolean isSearchOpen() {
        if (binding.materialSearchLast != null) {
            return binding.materialSearchLast.isSearchOpen();
        }
        return false;
    }


    private void addToArrayList(String str1, String str2, String str3, String str4, String str5) {
        arrayList.add(str1);
        arrayList2.add(str2);
        arraySearchList.add(str2);
        arrayArtistList.add(str3);
        arrayTitleList.add(str4);
        arrayDuraList.add(str5);
    }

    private void executeTask() {

        if (!loadMore) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        disposable = Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {

                    if (isConnected()) {
                        Document doc;
                        if (cInt == 1) {
                            doc = Jsoup.connect(MAIN_BASE_URL + txtSearch + "/").timeout(10000).ignoreHttpErrors(true).get();
                        } else {
                            doc = Jsoup.connect(MAIN_BASE_URL + txtSearch + "/" + MAIN_BASE_URL_2 + Integer.toString(cInt) + "/").timeout(10000).ignoreHttpErrors(true).get();
                        }
                        if (doc != null) {
                            Elements baseElements = doc.select(baseQuery);
                            Elements artistElements = doc.select(artistQuery);
                            Elements titleElements = doc.select(titleQuery);
                            Elements durationElements = doc.select(durationQuery);
                            for (int j = 0; j < baseElements.size(); j++) {
                                String title = titleElements.get(j).text();
                                String artist = artistElements.get(j).text();
                                String duration = durationElements.get(j).text();
                                String baseUrl = baseElements.get(j).attr(singleQuery);
                                addToArrayList(title + "\n " + artist + "\n", baseUrl, artist, title, duration);
                            }
                            addToArrayAdapter();
                            checkAdapter();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        }).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(o -> postMain(), Throwable::printStackTrace);
    }


    private void checkAdapter() {
        if (adtSearchList.getCount() >= 49) {
            control = false;
        } else {
            control = true;
        }
    }

    private void postMain() {
        if (arraySongOnlineList != null && adtSongOnline != null) {
            arraySongOnlineList.clear();
            adtSongOnline.notifyDataSetChanged();
        }
        if (arrayList != null) {
            array = new int[arrayList.size()];

            for (int k = 0; k < arrayList.size(); k++) {
                if (adtDuration != null) {
                    StringBuilder dura = new StringBuilder(adtDuration.getItem(k));
                    for (int j = 0; j < dura.length(); j++) {
                        char car = dura.charAt(j);
                        if (j == 0 && car == '0') {
                            dura.deleteCharAt(j);
                        }
                        if (car == ':') {
                            dura.deleteCharAt(j);
                        }
                    }
                    int duraAsInt = Integer.parseInt(dura.toString());

                    int strStage1 = duraAsInt / 100;
                    int strStage2 = duraAsInt % 100;
                    int finalStage = strStage1 * 60 + strStage2;
                    array[k] = k;
                    SongOnline songOnline = new SongOnline(adtSearchList.getItem(k), adtArtist.getItem(k), adtTitle.getItem(k), finalStage);
                    arraySongOnlineList.add(songOnline);
                    if (adtSongOnline != null) {
                        adtSongOnline.notifyDataSetInvalidated();
                    }
                    adtSongOnline = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, arraySongOnlineList);

                }
            }

        }
        binding.recyclerView.requestLayout();
        binding.recyclerView.invalidate();
        loadMore = false;
        footerView.setVisibility(View.GONE);
        if (!isConnected()) {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(context.getApplicationContext(), R.string.cnn_err, Toast.LENGTH_SHORT).show();
        } else {
            if (arrayList.size() == 0) {
                isitRemoved = true;
                if (errorStr.equals(getString(R.string.http_error))) {
                    Toast.makeText(context.getApplicationContext(), R.string.cnn_err, Toast.LENGTH_SHORT).show();
                    errorStr = "";
                } else {
                    controlIfEmpty();
                }
            } else {
                isitRemoved = false;
            }
            if (!isitRemoved) {
                binding.emptyText.setVisibility(View.GONE);
                if (positionMore == 0 && cInt == 1) {
                    if (binding.recyclerView.getAdapter() == null) {
                        createOnlineAdapter();
                        if (arraySongOnlineList.size() >= 15) {
                            binding.recyclerView.setThumbEnabled(true);
                        } else {
                            binding.recyclerView.setThumbEnabled(false);
                        }

                        onlineAdapter.notifyDataSetChanged();
                        runLayoutAnimation(binding.recyclerView);

                    }
                } else if (cInt > 1 && cInt <= 5) {
                    if (arraySongOnlineList.size() >= 15) {
                        binding.recyclerView.setThumbEnabled(true);
                    } else {
                        binding.recyclerView.setThumbEnabled(false);
                    }
                    onlineAdapter.notifyDataSetChanged();

                }
                if (lCounter == adtSearchList.getCount()) {
                    control = true;
                }
                lCounter = adtSearchList.getCount();

                if (!control && cInt <= 5) {

                    onlineAdapter.showLoading(true);
                    if (arraySongOnlineList.size() >= 15) {
                        binding.recyclerView.setThumbEnabled(true);
                    } else {
                        binding.recyclerView.setThumbEnabled(false);
                    }
                    onlineAdapter.notifyDataSetChanged();

                } else {

                    onlineAdapter.showLoading(false);
                    if (arraySongOnlineList.size() >= 15) {
                        binding.recyclerView.setThumbEnabled(true);
                    } else {
                        binding.recyclerView.setThumbEnabled(false);
                    }

                    onlineAdapter.notifyDataSetChanged();
                    runLayoutAnimation(binding.recyclerView);

                }
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);

            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);

            }
        }
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void controlIfEmpty() {
        if (binding.emptyText != null) {
            binding.emptyText.setText(R.string.no_result);
            binding.emptyText.setVisibility(onlineAdapter == null || onlineAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void setOnlineAdapter() {
        onlineAdapter = new OnlineAdapter((AppCompatActivity) getActivity(), arraySongOnlineList);
        binding.recyclerView.setAdapter(onlineAdapter);
    }

    private void createOnlineAdapter() {
        setOnlineAdapter();
    }

    @Override
    public void onDestroyView() {

        if (binding.materialSearchLast.isSearchOpen()) {
            binding.materialSearchLast.closeSearch();
        }

        if (disposable != null) {
            disposable.dispose();
        }
        DisposableManager.dispose();

        super.onDestroyView();
    }

}