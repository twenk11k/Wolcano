
package com.wolcano.musicplayer.music.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.TintHelper;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.di.component.ApplicationComponent;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.listener.RecyclerViewScrollListener;
import com.wolcano.musicplayer.music.mvp.models.SongOnline;
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.ui.adapter.MainAdapter;
import com.wolcano.musicplayer.music.mvp.listener.SetSearchQuery;
import com.wolcano.musicplayer.music.widgets.MaterialSearchLast;
import com.wolcano.musicplayer.music.utils.Utils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import static com.wolcano.musicplayer.music.constants.Constants.MAIN_BASE_URL;
import static com.wolcano.musicplayer.music.constants.Constants.MAIN_BASE_URL_2;



public class FragmentOnline extends BaseFragment implements SetSearchQuery {


    //  Butterknife bindings...
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.statusBarCustom)
    StatusBarView statusBarView;
    @BindView(R.id.progressBar)
    MaterialProgressBar progressBar;
    @BindView(R.id.emptyText)
    TextView empty;
    @BindView(R.id.toolbar_container)
    FrameLayout toolbarContainer;
    @BindView(R.id.view_empty)
    View emptyView;
    @BindView(R.id.recyclerview)
    FastScrollRecyclerView recyclerView;


    private Activity activity;
    private Context context;
    private int posMore = 0;
    private String txtSearch;
    private boolean control = false;
    private int lCounter = 0;

    boolean moreBlk = false;

    private String strErr;
    private int[] array;
    private int srcCnt = 0;
    private boolean isApproved = false;
    private ArrayList<String> arrayList;
    private ArrayList<String> arrayTitleList;
    private ArrayList<String> arrayArtistList;
    private ArrayList<String> arraySeaList;
    private ArrayList<String> lastSearches;
    private ArrayList<String> arrayList2;
    private ArrayList<String> arrayDuraList;
    private ArrayList<String> suggestionList;
    private ArrayList<SongOnline> arraySongOnlineList;

    private int cInt = 1;
    private boolean lMore = false;

    private ArrayAdapter<String> adt;
    private ArrayAdapter<String> adtTitle;
    private ArrayAdapter<String> adtDuration;
    private ArrayAdapter<String> adtArtist;
    private ArrayAdapter<String> adtSearchList;
    private ArrayAdapter<String> adt2;
    private ArrayAdapter<SongOnline> adtModel1List;

    private Disposable disposable;

    private MainAdapter mAdapter;

    private MaterialSearchLast materialSearchView;
    private String[] suggestionListStringsFromRemove;
    private String[] lastSearchesStringsFromRemove;
    private String duraQuery = "em.cplayer-data-sound-time";

    private View footerView;
    private List<String> lastSearch;


    private int color;
    private boolean isitInitial;
    private boolean isitRemoved = false;
    private String titQuery = "b.cplayer-data-sound-title";
    private String baseQuery = "li[data-sound-url]";
    private String artQuery = "i.cplayer-data-sound-author";
    private String singleQuery = "data-sound-url";
    private String val = "shine";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview;
        if (Build.VERSION.SDK_INT >= 21) {
            rootview = inflater.inflate(R.layout.fragment_online, container, false);
        } else {
            rootview = inflater.inflate(R.layout.fragment_online_19, container, false);

        }
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootview);

        activity = getActivity();
        context = getContext();
        color = Utils.getPrimaryColor(context);
        toolbarContainer.bringToFront();


        setStatusbarColorAuto(statusBarView, color);
        Utils.setUpFastScrollRecyclerViewColor(recyclerView, Utils.getAccentColor(context));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));


        recyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
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


        if (Build.VERSION.SDK_INT < 21 && rootview.findViewById(R.id.statusBarCustom) != null) {
            rootview.findViewById(R.id.statusBarCustom).setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 19) {
                int statusBarHeight = Utils.getStatHeight(getContext());
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rootview.findViewById(R.id.toolbar_container).getLayoutParams();
                layoutParams.setMargins(0, statusBarHeight, 0, 0);
                rootview.findViewById(R.id.toolbar_container).setLayoutParams(layoutParams);
            }
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(color);
        if (Utils.isColorLight(color)) {
            toolbar.setTitleTextColor(Color.BLACK);
        } else {
            toolbar.setTitleTextColor(Color.WHITE);
        }
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(R.string.onlineplayer);

        if (toolbar.getNavigationIcon() != null) {
            toolbar.setNavigationIcon(TintHelper.createTintedDrawable(toolbar.getNavigationIcon(), ToolbarContentTintHelper.toolbarContentColor(getContext(), color)));
        }

        footerView = inflater.inflate(R.layout.thefooter_view, container, false);
        arrayList = new ArrayList<>();
        arrayArtistList = new ArrayList<>();
        arraySeaList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        arrayDuraList = new ArrayList<>();
        arraySongOnlineList = new ArrayList<>();
        arrayTitleList = new ArrayList<>();


        materialSearchView = rootview.findViewById(R.id.material_search_last);

        emptyView.setVisibility(View.VISIBLE);
        setSearchView();
        empty.setText(context.getResources().getString(R.string.search_info));
        recyclerView.setVisibility(View.GONE);


        return rootview;
    }

    private void loadMoreData() {
        if (!lMore) {
            if (cInt <= 5 && arrayList.size() >= 50 && !isApproved) {
                if (!lMore) {
                    lMore = true;
                    cInt++;
                    posMore = recyclerView.getAdapter().getItemCount() - 1;
                    if (!control) {
                        isApproved = false;
                        ExecuteTask();
                    } else {
                        lMore = false;
                    }
                }
            } else {
                lMore = false;
            }
        }
    }

    private void setSearchView() {

        materialSearchView.setSubmitOnClick(true);
        materialSearchView.setVoiceSearch(true);
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(Utils.getAccentColor(context)));
        materialSearchView.setHint(getString(R.string.searchhint));
        materialSearchView.setSuggestionIcon(getResources().getDrawable(R.drawable.ic_history_white_24dp));
        materialSearchView.setSuggestionRemove(getResources().getDrawable(R.drawable.baseline_close_black_36));
        materialSearchView.setSuggestionSend(getResources().getDrawable(R.drawable.ic_call_made_white_24dp));
        int colorPrimary = Utils.getPrimaryColor(context);
        int colorTint;
        if (Utils.isColorLight(colorPrimary)) {
            colorTint = Color.BLACK;
            materialSearchView.setSuggestionIconTint(colorTint);
            materialSearchView.setSuggestionRemoveTint(colorTint);
            materialSearchView.setSuggestionSendTint(colorTint);
            materialSearchView.setCloseIconTint(colorTint);
            materialSearchView.setBackIconTint(colorTint);
            materialSearchView.setTextColor(colorTint);
            materialSearchView.setHintTextColor(ContextCompat.getColor(context, R.color.black_u6));

        } else {
            colorTint = Color.WHITE;
            materialSearchView.setSuggestionIconTint(colorTint);
            materialSearchView.setSuggestionRemoveTint(colorTint);
            materialSearchView.setSuggestionSendTint(colorTint);
            materialSearchView.setCloseIconTint(colorTint);
            materialSearchView.setBackIconTint(colorTint);
            materialSearchView.setTextColor(colorTint);
            materialSearchView.setHintTextColor(ContextCompat.getColor(context, R.color.grey0));

        }
        materialSearchView.setBackgroundColor(colorPrimary);
        ColorDrawable colorDrawable = new ColorDrawable(colorPrimary);
        materialSearchView.setSuggestionBackground(colorDrawable);
        suggestionList = new ArrayList<String>();
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
        recyclerView.addOnScrollListener(new RecyclerViewScrollListener() {
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

        materialSearchView.setOnQueryTextListener(new MaterialSearchLast.OnQueryTextListener() {
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

                materialSearchView.setSuggestions(arrf, arrf2, false, FragmentOnline.this, colorTint);
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
                        toolbar.setTitle(query.trim());
                    }
                    emptyView.setVisibility(View.GONE);
                    empty.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    int delay = (2 + new Random().nextInt(3)) * 1000;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            if (empty != null) {
                                empty.setText(R.string.no_result);
                                empty.setVisibility(View.VISIBLE);
                            }
                        }
                    }, delay);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    isitInitial = true;
                    String[] arr;
                    arr = suggestionList.toArray(new String[suggestionList.size()]);
                    MaterialSearchLast.isEmpty = true;

                    String str = Utils.getLastSearch(context);
                    if (str != null) {
                        lastSearch = Arrays.asList(str.split(","));
                        String[] lastSearches;
                        if (str.isEmpty()) {
                            lastSearches = null;

                        } else {
                            lastSearches = lastSearch.toArray(new String[lastSearch.size()]);

                        }

                        materialSearchView.setSuggestions(arr, lastSearches, true, FragmentOnline.this, colorTint);
                    }
                    MaterialSearchLast.isEmpty = false;

                } else {

                    isitInitial = false;
                    materialSearchView.setSuggestions(suggestionListStringsFromRemove, lastSearchesStringsFromRemove, false, FragmentOnline.this, colorTint);

                    if (suggestionListStringsFromRemove != null && lastSearchesStringsFromRemove != null) {
                        materialSearchView.setSuggestions(suggestionListStringsFromRemove, lastSearchesStringsFromRemove, false, FragmentOnline.this, colorTint);
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
                    toolbar.setTitle(last_single_search.trim());
                }
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);
                empty.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                int delay = (2 + new Random().nextInt(3)) * 1000;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (empty != null) {
                            empty.setText(R.string.no_result);
                            empty.setVisibility(View.VISIBLE);
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

        emptyView.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        toolbar.setTitle(query);

        RecyclerViewScrollListener.previousTotal = 0;
        isApproved = true;
        Utils.setGetSearch(context, query);
        if (!lMore) {
            if (arrayList != null && adt != null) {
                arrayList.clear();
                adt.notifyDataSetChanged();
            }
            if (arrayList2 != null && adt2 != null) {
                arrayList2.clear();
                adt2.notifyDataSetChanged();
            }
            if (arraySeaList != null && adtSearchList != null) {
                arraySeaList.clear();
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
            if (arraySongOnlineList != null && adtModel1List != null) {
                arraySongOnlineList.clear();
                adtModel1List.notifyDataSetChanged();
            }
            if (mAdapter != null) {
                mAdapter.clear();
            }

            if (disposable != null)
                disposable.dispose();
            lCounter = 0;
            srcCnt++;
            posMore = 0;
            if (mAdapter != null) {
                mAdapter.showLoading(false);

            }
            txtSearch = query;
            cInt = 1;
            materialSearchView.clearFocus();
            ExecuteTask();
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
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar));
        if (Utils.isColorLight(color)) {
            materialSearchView.setBackgroundColor(com.kabouzeid.appthemehelper.util.ColorUtil.shiftColor(color, 0.7F));
            materialSearchView.setSuggestionBackgroundColor(com.kabouzeid.appthemehelper.util.ColorUtil.shiftColor(color, 0.7F));
        } else {
            materialSearchView.setBackgroundColor(color);
            materialSearchView.setSuggestionBackgroundColor(color);
        }

        MenuItem searchItem = menu.findItem(R.id.action_searchM);
        materialSearchView.setMenuItem(searchItem);
        super.onCreateOptionsMenu(menu, inflater);

    }




    @Override
    public void onSearchQuery(int position, boolean isFirst) {
        materialSearchView.setQuery(materialSearchView.getListPosSend(position), isFirst);
    }

    @Override
    public void onRemoveSuggestion(int position, int whichList) {

        removeSuggestion(materialSearchView.getListPosSend(position));

    }

    private void addToArrayAdapter() {
        adt = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arrayList);
        adt2 = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arrayList2);
        adtArtist = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arrayArtistList);
        adtTitle = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arrayTitleList);
        adtDuration = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arrayDuraList);
        adtSearchList = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arraySeaList);

    }

    public void closeSearch() {
        if (materialSearchView != null) {
            materialSearchView.closeSearch();
        }
    }

    public boolean isSearchOpen() {
        if (materialSearchView != null) {
            return materialSearchView.isSearchOpen();
        }
        return false;
    }


    private void addToArrayList(String str1, String str2, String str3, String str4, String str5) {
        arrayList.add(str1);
        arrayList2.add(str2);
        arraySeaList.add(str2);
        arrayArtistList.add(str3);
        arrayTitleList.add(str4);
        arrayDuraList.add(str5);
    }

    private void ExecuteTask() {

        moreBlk = true;
        if (!lMore) {

            progressBar.setVisibility(View.VISIBLE);
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
                            Elements baseEl = doc.select(baseQuery);
                            Elements artEl = doc.select(artQuery);
                            Elements titEl = doc.select(titQuery);
                            Elements duraEl = doc.select(duraQuery);
                            for (int j = 0; j < baseEl.size(); j++) {
                                String title = titEl.get(j).text();
                                String artist = artEl.get(j).text();
                                String dura = duraEl.get(j).text();
                                String baseUrl = baseEl.get(j).attr(singleQuery);
                                addToArrayList(title + "\n " + artist + "\n", baseUrl, artist, title, dura);
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
        if (arraySongOnlineList != null && adtModel1List != null) {
            arraySongOnlineList.clear();
            adtModel1List.notifyDataSetChanged();
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
                    if (adtModel1List != null) {
                        adtModel1List.notifyDataSetInvalidated();
                    }
                    adtModel1List = new ArrayAdapter<SongOnline>(context, android.R.layout.simple_list_item_1, arraySongOnlineList);

                }
            }

        }
        recyclerView.requestLayout();
        recyclerView.invalidate();
        lMore = false;
        footerView.setVisibility(View.GONE);
        moreBlk = false;
        if (!isConnected()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(context.getApplicationContext(), R.string.cnn_err, Toast.LENGTH_SHORT).show();
        } else {
            if (arrayList.size() == 0) {
                isitRemoved = true;
                if (strErr == getString(R.string.http_error)) {
                    Toast.makeText(context.getApplicationContext(), R.string.cnn_err, Toast.LENGTH_SHORT).show();
                    strErr = "";
                } else {
                    controlIfEmpty();
                }
            } else {
                isitRemoved = false;
            }
            if (!isitRemoved) {
                empty.setVisibility(View.GONE);
                if (posMore == 0 && cInt == 1) {
                    if (recyclerView.getAdapter() == null) {
                        createMainAdapter();
                        if (arraySongOnlineList.size() >= 15) {
                            recyclerView.setThumbEnabled(true);
                        } else {
                            recyclerView.setThumbEnabled(false);
                        }

                        mAdapter.notifyDataSetChanged();
                        runLayoutAnimation(recyclerView);

                    }
                } else if (cInt > 1 && cInt <= 5) {
                    if (arraySongOnlineList.size() >= 15) {
                        recyclerView.setThumbEnabled(true);
                    } else {
                        recyclerView.setThumbEnabled(false);
                    }
                    mAdapter.notifyDataSetChanged();

                }
                if (lCounter == adtSearchList.getCount()) {
                    control = true;
                }
                lCounter = adtSearchList.getCount();

                if (!control && cInt <= 5) {

                    mAdapter.showLoading(true);
                    if (arraySongOnlineList.size() >= 15) {
                        recyclerView.setThumbEnabled(true);
                    } else {
                        recyclerView.setThumbEnabled(false);
                    }
                    mAdapter.notifyDataSetChanged();

                } else {

                    mAdapter.showLoading(false);
                    if (arraySongOnlineList.size() >= 15) {
                        recyclerView.setThumbEnabled(true);
                    } else {
                        recyclerView.setThumbEnabled(false);
                    }

                    mAdapter.notifyDataSetChanged();
                    runLayoutAnimation(recyclerView);

                }
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            } else {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

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
        if (empty != null) {
            empty.setText(R.string.no_result);
            empty.setVisibility(mAdapter == null || mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void setMainAdapter() {
        mAdapter = new MainAdapter((AppCompatActivity) getActivity(), arraySongOnlineList);
        recyclerView.setAdapter(mAdapter);

    }


    private void createMainAdapter() {
        setMainAdapter();
    }


    @Override
    public void onDestroyView() {

        if (materialSearchView.isSearchOpen()) {
            materialSearchView.closeSearch();
        }

        if (disposable != null) {
            disposable.dispose();
        }
        DisposableManager.dispose();

        super.onDestroyView();
    }

}