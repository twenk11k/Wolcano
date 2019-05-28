package com.wolcano.musicplayer.music.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wolcano.musicplayer.music.R;

public class RatingDialogV2 extends AppCompatDialog implements RatingBar.OnRatingBarChangeListener, View.OnClickListener {

    private static final String SESSION_COUNT = "session_count";
    private static final String SHOW_NEVER = "show_never";
    private String MyPrefs = "RatingDialog";
    private SharedPreferences sharedpreferences;

    private Context context;
    private RatingDialogV2.Builder builder;
    private TextView tvTitle, tvPositive,tvNegative, tvFeedback, tvSubmit, tvCancel;
    private RatingBar ratingBar;
    private ImageView ivIcon;
    private EditText etFeedback;
    private LinearLayout ratingButtons, feedbackButtons;

    private float threshold;
    private int session;
    private boolean thresholdPassed = true;
    public RatingDialogV2(Context context, RatingDialogV2.Builder builder) {
        super(context);
        this.context = context;
        this.builder = builder;

        this.session = builder.session;
        this.threshold = builder.threshold;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_rating);

        tvTitle = (TextView) findViewById(R.id.dialog_rating_title);
        tvPositive = (TextView) findViewById(R.id.dialog_rating_button_positive);
        tvNegative = (TextView) findViewById(R.id.dialog_rating_button_negative);

        tvFeedback = (TextView) findViewById(R.id.dialog_rating_feedback_title);
        tvSubmit = (TextView) findViewById(R.id.dialog_rating_button_feedback_submit);
        tvCancel = (TextView) findViewById(R.id.dialog_rating_button_feedback_cancel);
        ratingBar = (RatingBar) findViewById(R.id.dialog_rating_rating_bar);
        ivIcon = (ImageView) findViewById(R.id.dialog_rating_icon);
        etFeedback = (EditText) findViewById(R.id.dialog_rating_feedback);
        ratingButtons = (LinearLayout) findViewById(R.id.dialog_rating_buttons);
        feedbackButtons = (LinearLayout) findViewById(R.id.dialog_rating_feedback_buttons);

        init();
        setCanceledOnTouchOutside(false);

    }

    private void init() {

        tvTitle.setText(builder.title);
        tvPositive.setText(builder.positiveText);
        tvNegative.setText(builder.negativeText);

        tvFeedback.setText(builder.formTitle);
        tvSubmit.setText(builder.submitText);
        tvCancel.setText(builder.cancelText);
        etFeedback.setHint(builder.feedbackFormHint);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        int color = typedValue.data;

        tvTitle.setTextColor(builder.titleTextColor != 0 ? ContextCompat.getColor(context, builder.titleTextColor) : ContextCompat.getColor(context, R.color.white));
        tvPositive.setTextColor(builder.positiveTextColor != 0 ? ContextCompat.getColor(context, builder.positiveTextColor) : color);
        tvNegative.setTextColor(builder.negativeTextColor != 0 ? ContextCompat.getColor(context, builder.negativeTextColor) : color);

        tvFeedback.setTextColor(builder.titleTextColor != 0 ? ContextCompat.getColor(context, builder.titleTextColor) : ContextCompat.getColor(context, R.color.white));
        tvSubmit.setTextColor(builder.positiveTextColor != 0 ? ContextCompat.getColor(context, builder.positiveTextColor) : color);
        tvCancel.setTextColor(builder.negativeTextColor != 0 ? ContextCompat.getColor(context, builder.negativeTextColor) : ContextCompat.getColor(context, R.color.grey_u1));
        etFeedback.setTextColor(ContextCompat.getColor(context,R.color.black_u6));
        if (builder.feedBackTextColor != 0) {
            etFeedback.setTextColor(ContextCompat.getColor(context, builder.feedBackTextColor));
        }
        if(builder.formHintTextColor != 0){
            tvFeedback.setTextColor(ContextCompat.getColor(context,builder.formHintTextColor));
        }
        if (builder.positiveBackgroundColor != 0) {
            tvPositive.setBackgroundResource(builder.positiveBackgroundColor);
            tvSubmit.setBackgroundResource(builder.positiveBackgroundColor);

        }
        if (builder.negativeBackgroundColor != 0) {
            tvNegative.setBackgroundResource(builder.negativeBackgroundColor);
            tvCancel.setBackgroundResource(builder.negativeBackgroundColor);
        }

        if (builder.ratingBarColor != 0) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
                stars.getDrawable(2).setColorFilter(ContextCompat.getColor(context, builder.ratingBarColor), PorterDuff.Mode.SRC_ATOP);
                stars.getDrawable(1).setColorFilter(ContextCompat.getColor(context, builder.ratingBarColor), PorterDuff.Mode.SRC_ATOP);
                int ratingBarBackgroundColor = builder.ratingBarBackgroundColor != 0 ? builder.ratingBarBackgroundColor : R.color.grey_u11;
                stars.getDrawable(0).setColorFilter(ContextCompat.getColor(context, ratingBarBackgroundColor), PorterDuff.Mode.SRC_ATOP);
            } else {
                Drawable stars = ratingBar.getProgressDrawable();
                DrawableCompat.setTint(stars, ContextCompat.getColor(context, builder.ratingBarColor));
            }
        }
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(0).setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_ATOP);

        Drawable d = context.getPackageManager().getApplicationIcon(context.getApplicationInfo());
        ivIcon.setImageDrawable(builder.drawable != null ? builder.drawable : d);


        ratingBar.setOnRatingBarChangeListener(this);
        tvPositive.setOnClickListener(this);
        tvNegative.setOnClickListener(this);

        tvSubmit.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dialog_rating_button_negative) {

            dismiss();
            showNever();

        }else if (view.getId() == R.id.dialog_rating_button_positive) {

            dismiss();

        } else if (view.getId() == R.id.dialog_rating_button_feedback_submit) {

            String feedback = etFeedback.getText().toString().trim();
            if (TextUtils.isEmpty(feedback)) {

                Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
                etFeedback.startAnimation(shake);
                return;
            }

            if (builder.ratingDialogFormListener != null) {
                builder.ratingDialogFormListener.onFormSubmitted(feedback);
            }
             Toast.makeText(context,context.getString(R.string.feedback_submit),Toast.LENGTH_SHORT).show();

            dismiss();
            showNever();

        } else if (view.getId() == R.id.dialog_rating_button_feedback_cancel) {

            dismiss();

        }

    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

        if (ratingBar.getRating() >= threshold) {
            thresholdPassed = true;

            if (builder.ratingThresholdClearedListener == null) {
                setRatingThresholdClearedListener();
            }
            builder.ratingThresholdClearedListener.onThresholdCleared(this, ratingBar.getRating(), thresholdPassed);

        } else {
            thresholdPassed = false;

            if (builder.ratingThresholdFailedListener == null) {
                setRatingThresholdFailedListener();
            }
            builder.ratingThresholdFailedListener.onThresholdFailed(this, ratingBar.getRating(), thresholdPassed);
        }

        if (builder.ratingDialogListener != null) {
            builder.ratingDialogListener.onRatingSelected(ratingBar.getRating(), thresholdPassed);
        }
        showNever();
    }

    private void setRatingThresholdClearedListener() {
        builder.ratingThresholdClearedListener = new RatingDialogV2.Builder.RatingThresholdClearedListener() {
            @Override
            public void onThresholdCleared(RatingDialogV2 ratingDialog, float rating, boolean thresholdCleared) {
                openPlaystore(context);
                dismiss();
            }
        };
    }

    private void setRatingThresholdFailedListener() {
        builder.ratingThresholdFailedListener = new RatingDialogV2.Builder.RatingThresholdFailedListener() {
            @Override
            public void onThresholdFailed(RatingDialogV2 ratingDialog, float rating, boolean thresholdCleared) {
                openForm();
            }
        };
    }

    private void openForm() {
        tvFeedback.setVisibility(View.VISIBLE);
        etFeedback.setVisibility(View.VISIBLE);
        feedbackButtons.setVisibility(View.VISIBLE);
        ratingButtons.setVisibility(View.GONE);
        ivIcon.setVisibility(View.GONE);
        tvTitle.setVisibility(View.GONE);
        ratingBar.setVisibility(View.GONE);
    }

    private void openPlaystore(Context context) {
        final Uri marketUri = Uri.parse(builder.playstoreUrl);
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public TextView getTitleTextView() {
        return tvTitle;
    }

    public TextView getPositiveButtonTextView() {
        return tvPositive;
    }

    public TextView getFormTitleTextView() {
        return tvFeedback;
    }

    public TextView getFormSumbitTextView() {
        return tvSubmit;
    }

    public TextView getFormCancelTextView() {
        return tvCancel;
    }

    public ImageView getIconImageView() {
        return ivIcon;
    }

    public RatingBar getRatingBarView() {
        return ratingBar;
    }

    @Override
    public void show() {

        if (checkIfSessionMatches(session)) {
            super.show();
        }
    }

    private boolean checkIfSessionMatches(int session) {

        if (session == 1) {
            return true;
        }

        sharedpreferences = context.getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);

        if (sharedpreferences.getBoolean(SHOW_NEVER, false)) {
            return false;
        }

        int count = sharedpreferences.getInt(SESSION_COUNT, 1);

        if (session == count) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(SESSION_COUNT, 1);
            editor.commit();
            return true;
        } else if (session > count) {
            count++;
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(SESSION_COUNT, count);
            editor.commit();
            return false;
        } else {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(SESSION_COUNT, 2);
            editor.commit();
            return false;
        }
    }

    private void showNever() {
        sharedpreferences = context.getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(SHOW_NEVER, true);
        editor.commit();
    }

    public static class Builder {

        private final Context context;
        private String title, positiveText, negativeText, playstoreUrl;
        private String formTitle, submitText, cancelText, feedbackFormHint;
        private int positiveTextColor, negativeTextColor, titleTextColor, ratingBarColor, ratingBarBackgroundColor, feedBackTextColor,formHintTextColor;
        private int positiveBackgroundColor, negativeBackgroundColor;
        private RatingDialogV2.Builder.RatingThresholdClearedListener ratingThresholdClearedListener;
        private RatingDialogV2.Builder.RatingThresholdFailedListener ratingThresholdFailedListener;
        private RatingDialogV2.Builder.RatingDialogFormListener ratingDialogFormListener;
        private RatingDialogV2.Builder.RatingDialogListener ratingDialogListener;
        private Drawable drawable;

        private int session = 1;
        private float threshold = 1;

        public interface RatingThresholdClearedListener {
            void onThresholdCleared(RatingDialogV2 ratingDialog, float rating, boolean thresholdCleared);
        }

        public interface RatingThresholdFailedListener {
            void onThresholdFailed(RatingDialogV2 ratingDialog, float rating, boolean thresholdCleared);
        }

        public interface RatingDialogFormListener {
            void onFormSubmitted(String feedback);
        }

        public interface RatingDialogListener {
            void onRatingSelected(float rating, boolean thresholdCleared);
        }

        public Builder(Context context) {
            this.context = context;
            // Set default PlayStore URL
            this.playstoreUrl = "market://details?id=" + context.getPackageName();
            initText();
        }

        private void initText() {
            title = context.getString(R.string.rating_title);
            positiveText = context.getString(R.string.form_later);
            negativeText = context.getString(R.string.form_never);
            formTitle = context.getString(R.string.form_title);
            submitText = context.getString(R.string.rating_submit);
            cancelText = context.getString(R.string.rating_cancel);
            feedbackFormHint = context.getString(R.string.form_hint);
        }

        public RatingDialogV2.Builder session(int session) {
            this.session = session;
            return this;
        }

        public RatingDialogV2.Builder threshold(float threshold) {
            this.threshold = threshold;
            return this;
        }

        public RatingDialogV2.Builder title(String title) {
            this.title = title;
            return this;
        }

        public RatingDialogV2.Builder icon(Drawable drawable) {
            this.drawable = drawable;
            return this;
        }

        public RatingDialogV2.Builder positiveButtonText(String positiveText) {
            this.positiveText = positiveText;
            return this;
        }

        public RatingDialogV2.Builder negativeButtonText(String negativeText) {
            this.negativeText = negativeText;
            return this;
        }

        public RatingDialogV2.Builder titleTextColor(int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        public RatingDialogV2.Builder positiveButtonTextColor(int positiveTextColor) {
            this.positiveTextColor = positiveTextColor;
            return this;
        }

        public RatingDialogV2.Builder negativeButtonTextColor(int negativeTextColor) {
            this.negativeTextColor = negativeTextColor;
            return this;
        }

        public RatingDialogV2.Builder positiveButtonBackgroundColor(int positiveBackgroundColor) {
            this.positiveBackgroundColor = positiveBackgroundColor;
            return this;
        }

        public RatingDialogV2.Builder negativeButtonBackgroundColor(int negativeBackgroundColor) {
            this.negativeBackgroundColor = negativeBackgroundColor;
            return this;
        }

        public RatingDialogV2.Builder onThresholdCleared(RatingDialogV2.Builder.RatingThresholdClearedListener ratingThresholdClearedListener) {
            this.ratingThresholdClearedListener = ratingThresholdClearedListener;
            return this;
        }

        public RatingDialogV2.Builder onThresholdFailed(RatingDialogV2.Builder.RatingThresholdFailedListener ratingThresholdFailedListener) {
            this.ratingThresholdFailedListener = ratingThresholdFailedListener;
            return this;
        }

        public RatingDialogV2.Builder onRatingChanged(RatingDialogV2.Builder.RatingDialogListener ratingDialogListener) {
            this.ratingDialogListener = ratingDialogListener;
            return this;
        }

        public RatingDialogV2.Builder onRatingBarFormSumbit(RatingDialogV2.Builder.RatingDialogFormListener ratingDialogFormListener) {
            this.ratingDialogFormListener = ratingDialogFormListener;
            return this;
        }

        public RatingDialogV2.Builder formTitle(String formTitle) {
            this.formTitle = formTitle;
            return this;
        }

        public RatingDialogV2.Builder formHint(String formHint) {
            this.feedbackFormHint = formHint;
            return this;
        }
        public RatingDialogV2.Builder formHintTextColor(int color){
            this.formHintTextColor = color;
            return this;
        }

        public RatingDialogV2.Builder formSubmitText(String submitText) {
            this.submitText = submitText;
            return this;
        }

        public RatingDialogV2.Builder formCancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public RatingDialogV2.Builder ratingBarColor(int ratingBarColor) {
            this.ratingBarColor = ratingBarColor;
            return this;
        }

        public RatingDialogV2.Builder ratingBarBackgroundColor(int ratingBarBackgroundColor) {
            this.ratingBarBackgroundColor = ratingBarBackgroundColor;
            return this;
        }

        public RatingDialogV2.Builder feedbackTextColor(int feedBackTextColor) {
            this.feedBackTextColor = feedBackTextColor;
            return this;
        }

        public RatingDialogV2.Builder playstoreUrl(String playstoreUrl) {
            this.playstoreUrl = playstoreUrl;
            return this;
        }

        public RatingDialogV2 build() {
            return new RatingDialogV2(context, this);
        }
    }
}
