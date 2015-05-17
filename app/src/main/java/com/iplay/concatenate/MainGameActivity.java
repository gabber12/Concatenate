package com.iplay.concatenate;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.iplay.concatenate.common.BackgroundURLRequest;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class MainGameActivity extends NetworkActivity {

    private static int MAX_MOVES = 10;


    private LockEditText enterWord;
    private Button submitButton;
    private LinearLayout wordsLayout;

    private static MainGameActivity that;

    private String lastWord = "DUMMY";
    private String against;
    private int gameId;
    private String userTurn;
    private Boolean isBot = false;

    private TextView myScore, yourScore;
    public static int currentMyScore = 0, currentYourScore = 0;

    private ProgressBar mpb, ypb;

    private int myMoves, yourMoves;
    private int myTotalTime, yourTotalTime;
    private int myWrongAttempts, yourWrongAttempts;

    Typeface myTypeface = null, myTypefaceLight = null, myTypefaceMedium = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        myTypeface = Typeface.createFromAsset(getAssets(), "BrandonText-Bold.otf");
        myTypefaceLight = Typeface.createFromAsset(getAssets(), "BrandonText-Light.otf");
        myTypefaceMedium = Typeface.createFromAsset(getAssets(), "BrandonText-Medium.otf");

        CommonUtils.waitingFor = null;
        CommonUtils.startGameIntent = null;
        CommonUtils.disableTimer(CommonUtils.startingGameTimer);
        currentMyScore = currentYourScore = 0;
        myMoves = yourMoves = myWrongAttempts = yourWrongAttempts = 0;
        myTotalTime = yourTotalTime = 0;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_game);
        setupActionBar();

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Register all local broadcast receiver here

        that = this;
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceivedBackPress, new IntentFilter("game_back_button"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceivedGameWord, new IntentFilter("gameword_received"));


        mpb = (ProgressBar) findViewById(R.id.myProgressBarTimeout);
        ypb = (ProgressBar) findViewById(R.id.yourProgressBarTimeout);

        myScore = (TextView) findViewById(R.id.myscore);
        yourScore = (TextView) findViewById(R.id.yourscore);
        enterWord = (LockEditText) findViewById(R.id.enter_word);

        enterWord.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here
                String str = enterWord.getText().toString();
                int maxMatch = 0;
                for (int i = 0; i < lastWord.length(); ++i) {
                    if (str.startsWith(lastWord.substring(i))) {
                        maxMatch = lastWord.length() - i;
                        break;
                    }
                }

                for (int i = 0; i < wordsLayout.getChildCount(); ++i) {
                    TextView textView = (TextView) wordsLayout.getChildAt(i);
                    if (i + maxMatch >= wordsLayout.getChildCount()) {
                        setBackgroundBox(textView, R.drawable.enter_word_background_red);
                    } else {
                        setBackgroundBox(textView, R.drawable.enter_word_background_black);
                    }
                }

            }
        });

        enterWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    System.out.println("clicked on done");
                    // send the word now
                    sendTheWrittenWord();
                    return true;
                }
                return false;
            }
        });

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, end, null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                return Character.isUpperCase(c) && CommonUtils.userId.equals(userTurn);
            }
        };
        enterWord.setFilters(new InputFilter[]{new InputFilter.AllCaps(), filter});
        wordsLayout = (LinearLayout) findViewById(R.id.LinearLayoutWords);

        userTurn = getIntent().getStringExtra("user_turn");
        against = getIntent().getStringExtra("against_user");
        gameId = getIntent().getIntExtra("game_id", 0);
        isBot = getIntent().getBooleanExtra("is_bot", false);
        setWords(getIntent().getStringExtra("game_word"));
        changeEnterWordBox();

        System.out.println(CommonUtils.userId + " " + against + " " + userTurn);

        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                sendTheWrittenWord();

            }

        });
    }

    private void sendTheWrittenWord() {

        if (!CommonUtils.userId.equals(userTurn) || mpb.getProgress() == 0)
            return;

        String str = enterWord.getText().toString();
        int maxMatch = getMaxMatch(str, lastWord);

        if (!CommonUtils.words.contains(str) || maxMatch == 0 || str.equals(lastWord)) {
            System.out.println(maxMatch);
            System.out.println(CommonUtils.words.contains(str));
            myWrongAttempts++;
            shakeWord();
        } else {

            myTotalTime += (mpb.getMax() - mpb.getProgress());
            if ( yourMoves < MAX_MOVES ) yourMoves++;

            setBackgroundBox(enterWord, R.drawable.enter_word_background_black);
            updateMyScore(str);
            changeTheWord(str);

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("fromUser", CommonUtils.userId);
                jsonObject.put("toUser", against);
                jsonObject.put("gameId", gameId);
                jsonObject.put("gameWord", str);
                jsonObject.put("typeFlag", 5);
                jsonObject.put("myScore", currentMyScore);

                // send to other player
                userTurn = against;
                changeEnterWordBox();
                if (!isBot)
                    ORTCUtil.getClient().send(CommonUtils.getChannelNameFromUserID(against), jsonObject.toString());

            } catch (Exception e) {
                Log.e("json", "error while generating a json file and sending to server");
            }
            resetCountdownTimer();
        }

    }

    private void resetCountdownTimer() {

        CommonUtils.disableTimer(CommonUtils.mainGameTimer);
        CommonUtils.mainGameTimer = new Timer();
        ProgressBar temp = null;
        if ( userTurn.equals(CommonUtils.userId) ) {
            mpb.setProgress(mpb.getMax());
            ypb.setProgress(0); temp = mpb;

        } else {
            ypb.setProgress(ypb.getMax());
            mpb.setProgress(0); temp = ypb;
        }
        final ProgressBar pb = temp;
        final long startTime = System.currentTimeMillis();
        CommonUtils.mainGameTimer.scheduleAtFixedRate(new TimerTask() {
            boolean word_request_sent = false;
            @Override
            public void run() {
                that.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        long left = (pb.getMax() - System.currentTimeMillis() + startTime);
                        if (left < 15 * 1000 && isBot && !word_request_sent && userTurn.equals(against) ) {

                            new BackgroundURLRequestMainGame().execute("get_random_word/", lastWord);
                            word_request_sent = true;


                        }
                        if (left > 0) pb.setProgress((int) left);
                        if (left <= 0) {
                            pb.setProgress(0);
                            if (userTurn.equals(CommonUtils.userId)) {
                                try {
                                    org.json.JSONObject jsonObject = new org.json.JSONObject();
                                    jsonObject.put("typeFlag", 7);
                                    jsonObject.put("toUser", against);
                                    jsonObject.put("fromUser", CommonUtils.userId);
                                    jsonObject.put("gameId", gameId);
                                    jsonObject.put("userTurn", userTurn);
                                    ORTCUtil.getClient().send(CommonUtils.getChannelNameFromUserID(against), jsonObject.toString());
                                } catch (JSONException je) {
                                    System.out.println("Unable to encode json: " + je.getMessage());
                                }
                                CommonUtils.disableTimer(CommonUtils.mainGameTimer);
                            }
                        }
                        if (left <= -15 * 1000) {
                            if (userTurn.equals(against)) {
                                try {
                                    org.json.JSONObject jsonObject = new org.json.JSONObject();
                                    jsonObject.put("typeFlag", 7);
                                    jsonObject.put("toUser", against);
                                    jsonObject.put("fromUser", CommonUtils.userId);
                                    jsonObject.put("gameId", gameId);
                                    jsonObject.put("userTurn", userTurn);
                                    ORTCUtil.getClient().send(CommonUtils.getChannelNameFromUserID(against), jsonObject.toString());
                                } catch (JSONException je) {
                                    System.out.println("Unable to encode json: " + je.getMessage());
                                }
                            }
                            CommonUtils.disableTimer(CommonUtils.mainGameTimer);
                        }
                    }
                });
            }
        }, new Long(0), new Long(500));

    }

    public int getScoreForThisWord(String str, String lastWord) {
        int maxMatch = getMaxMatch(str, lastWord);
        int earned = str.length();
        earned += maxMatch;
        int bonus = 2;
        while (maxMatch > 0) {
            earned += bonus;
            bonus += 2;
            maxMatch--;
        }
        return earned;
    }

    private void updateMyScore(String str) {
        int gained = getScoreForThisWord(str, lastWord);
        animateScore(myScore, currentMyScore, currentMyScore + gained);
        currentMyScore += gained;
//        myScore.setText("Score: " + points);
    }

    private void animateScore(final TextView textView, final int fromScore, final int toScore) {
//        System.out.println("reached in animation score");
        final String base = "Score: ";
        final Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            int currentScore = fromScore;
            @Override
            public void run() {
                MainGameActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentScore += 1;
                        textView.setText(base + currentScore);
                        if ( currentScore >= toScore ) {
                            t.cancel();
                            t.purge();
                        }

                    }
                });
            }
        }, new Long(0), new Long(30));
    }


    private void setWords(String str) {

        System.out.println(str);

        float layout_margin = 1f;
        int layout_width = getPixelsfromDP(40f);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int parentWidth = size.x;
        int probableWidth = (parentWidth - getPixelsfromDP(10f))/str.length();
        layout_width = Math.min(probableWidth, layout_width);
        int margin = getPixelsfromDP(layout_margin);
        int blockWidth = layout_width - margin - margin;

        System.out.println(parentWidth + " " + blockWidth);

        wordsLayout.removeAllViews();

        for (int i = 0; i < str.length(); ++i) {
            TextView newTextView = new TextView(this);
            String s = Character.toString(str.charAt(i));
            newTextView.setText(s);
            newTextView.setGravity(Gravity.CENTER);
            setBackgroundBox(newTextView, R.drawable.enter_word_background_black);
            newTextView.setHeight(blockWidth);
            newTextView.setWidth(blockWidth);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(getPixelsfromDP(layout_margin), getPixelsfromDP(layout_margin), getPixelsfromDP(layout_margin), getPixelsfromDP(layout_margin));
            newTextView.setLayoutParams(params);
            newTextView.setTextColor(Color.BLUE);
            wordsLayout.addView(newTextView);
        }
        lastWord = str;
        resetCountdownTimer();

    }

    private void changeEnterWordBox() {
        if (CommonUtils.userId.equals(userTurn)) {
            myMoves++;
            enterWord.setCursorVisible(true);
            setBackgroundBox(enterWord, R.drawable.enter_word_background_black);
        } else {
            yourMoves++;
            enterWord.setCursorVisible(false);
            setBackgroundBox(enterWord, R.drawable.enter_word_background_disable);
        }
    }

    private void setBackgroundBox(View view, int background) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(getResources().getDrawable(background));
        } else {
            view.setBackground(getResources().getDrawable(background));
        }
    }

    private static int getMaxMatch(String str, String lastWord) {
        for (int i = 0; i < lastWord.length(); ++i) {
            if (str.startsWith(lastWord.substring(i))) {
                return lastWord.length() - i;
            }
        }
        return 0;
    }

    private void changeTheWord(String str) {
        int maxMatch = getMaxMatch(str, lastWord);

        float layout_margin = 1f;
        int layout_width = getPixelsfromDP(40f);

        int parentWidth = wordsLayout.getWidth();
        int probableWidth = (parentWidth - getPixelsfromDP(10f))/str.length();
        probableWidth = Math.min(probableWidth, layout_width);
        layout_width = probableWidth;
        int margin = getPixelsfromDP(layout_margin);
        int blockWidth = layout_width - margin - margin;
        int viewWidth = layout_width;
        int length = str.length();
        int totalWidth = viewWidth*length;
        int autoMargin = (parentWidth - totalWidth)/2;
        List<Integer> newLeftPosition = new ArrayList<>();
        for ( int i = 0; i < str.length(); ++i ) {
            newLeftPosition.add(autoMargin+i*viewWidth);
        }

        List<Integer> leftPosition = new ArrayList<Integer>();
        for (int i = 0; i < wordsLayout.getChildCount(); ++i) {
//            System.out.println( ((TextView)wordsLayout.getChildAt(i)).getText() + " " + wordsLayout.getChildAt(i).getLeft());
            leftPosition.add(wordsLayout.getChildAt(i).getLeft());
        }

        for (int i = 0; i < wordsLayout.getChildCount(); ++i) {
            TextView textView = (TextView) wordsLayout.getChildAt(i);
            Animation animateToExtremeLeft = new TranslateAnimation(0, -600 - i * 50, 0, 0);
            animateToExtremeLeft.setDuration(800);
            Animation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
            fadeOutAnimation.setDuration(800);
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(animateToExtremeLeft);
            animationSet.addAnimation(fadeOutAnimation);
            textView.startAnimation(animationSet);
        }

        for (int i = wordsLayout.getChildCount() - maxMatch - 1; i >= 0; --i) {
            TextView textView = (TextView) wordsLayout.getChildAt(i);
            wordsLayout.removeView(textView);
        }

        int diff = 0;

        for (int i = 0; i < wordsLayout.getChildCount(); ++i) {
            TextView textView = (TextView) wordsLayout.getChildAt(i);
            Integer curLeftPosition = textView.getLeft();
            AnimationSet animationSet = new AnimationSet(true);
            Animation animateToStart = new TranslateAnimation(leftPosition.get(lastWord.length() - maxMatch + i) - newLeftPosition.get(i), 0, 0, 0);
            animateToStart.setDuration(800);
            animateToStart.setStartOffset(200);
            animateToStart.setInterpolator(new OvershootInterpolator());
            animationSet.addAnimation(animateToStart);
            diff += blockWidth - textView.getWidth();
            float blockWidthf = blockWidth;
            float lastWidthf = textView.getHeight();
            float scale = (blockWidthf/lastWidthf);
            Animation scaleForChange = new ScaleAnimation(1.0f,scale,1.0f,scale,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleForChange.setDuration(800);
            scaleForChange.setStartOffset(200);
            animationSet.addAnimation(scaleForChange);
            animationSet.setFillEnabled(true); animationSet.setFillAfter(true);
            textView.startAnimation(animationSet);
        }

        for (int i = maxMatch; i < str.length(); ++i) {
            TextView newTextView = new TextView(that);
            String s = Character.toString(str.charAt(i));
            newTextView.setText(s);
            newTextView.setGravity(Gravity.CENTER);
            setBackgroundBox(newTextView, R.drawable.enter_word_background_black);
            newTextView.setHeight(blockWidth);
            newTextView.setWidth(blockWidth);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(margin, margin, margin, margin);
            newTextView.setLayoutParams(params);
            newTextView.setTextColor(Color.BLUE);
            wordsLayout.addView(newTextView);

//            System.out.println("new left :" + newTextView.getText().toString() + " " + newTextView.getLeft() );

            Animation animateFromExtremeRight = new TranslateAnimation(600 - i * 50, diff, 0, 0);
            animateFromExtremeRight.setDuration(800);
            Animation fadeOutAnimation = new AlphaAnimation(0.0f, 1.0f);
            fadeOutAnimation.setDuration(800);
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(animateFromExtremeRight);
            animationSet.addAnimation(fadeOutAnimation);
            newTextView.startAnimation(animationSet);
        }
        enterWord.setText("");
        lastWord = str;

        for (int i = 0; i < wordsLayout.getChildCount(); ++i) {
            TextView textView = (TextView) wordsLayout.getChildAt(i);
            Integer curLeftPosition = textView.getLeft();
//            System.out.println("final left :" + textView.getText().toString() + " " + curLeftPosition );
        }
    }

    private int getPixelsfromDP(float dp) {
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        float fpixels = metrics.density * dp;
        return (int) (fpixels + 0.5f);
    }

    // Not required. already in SP.
    private float getSPFromPixels(float px) {
        float scaledDensity = getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    private void shakeWord() {
        setBackgroundBox(enterWord, R.drawable.enter_word_background_red);
        Animation shake = AnimationUtils.loadAnimation(MainGameActivity.this, R.anim.shake);
        enterWord.startAnimation(shake);
    }

    private BroadcastReceiver mReceivedBackPress = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            that.onBackPressed();
        }
    };

    private BroadcastReceiver mReceivedGameWord = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            changeTheWord(intent.getStringExtra("game_word"));
            animateScore(yourScore, currentYourScore, Math.max(currentYourScore, intent.getIntExtra("your_score", 0)));
            currentYourScore = Math.max(currentYourScore, intent.getIntExtra("your_score", 0));
//            String points = String.valueOf(currentYourScore);
//            while (points.length() < 3) points = "0" + points;
//            yourScore.setText("Score: " + points);
            userTurn = CommonUtils.userId;
            changeEnterWordBox();
            // TODO: Update others metrics by getting info from server.
            if ( myMoves < MAX_MOVES ) myMoves++;
            resetCountdownTimer();
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        TextView myNameTextView = (TextView) findViewById(R.id.myname);
        TextView myLevelTextView = (TextView) findViewById(R.id.mylevel);
        TextView myScoreTextView = (TextView) findViewById(R.id.myscore);

        TextView yourNameTextView = (TextView) findViewById(R.id.yourname);
        TextView yourLevelTextView = (TextView) findViewById(R.id.yourlevel);
        TextView yourScoreTextView = (TextView) findViewById(R.id.yourscore);

        myNameTextView.setTypeface(myTypefaceMedium);
        myLevelTextView.setTypeface(myTypefaceLight);
        myScoreTextView.setTypeface(myTypefaceMedium);
        yourNameTextView.setTypeface(myTypefaceMedium);
        yourLevelTextView.setTypeface(myTypefaceLight);
        yourScoreTextView.setTypeface(myTypefaceMedium);


        AnimationSet animationSet1 = new AnimationSet(true);
        Animation fadeIn1 = new AlphaAnimation(0.0f, 1.0f);
        fadeIn1.setDuration(1000);
        Animation comeIn1 = new TranslateAnimation(-200, 0, 0, 0);
        comeIn1.setDuration(1000);
        animationSet1.addAnimation(fadeIn1);
        animationSet1.addAnimation(comeIn1);
        myNameTextView.startAnimation(animationSet1);

        AnimationSet animationSet2 = new AnimationSet(true);
        Animation fadeIn2 = new AlphaAnimation(0.0f, 1.0f);
        fadeIn2.setDuration(1000);
        Animation comeIn2 = new TranslateAnimation(-200, 0, 0, 0);
        comeIn2.setDuration(1000);
        fadeIn2.setStartOffset(300);
        comeIn2.setStartOffset(300);
        animationSet2.addAnimation(fadeIn2);
        animationSet2.addAnimation(comeIn2);
        myLevelTextView.startAnimation(animationSet2);

        AnimationSet animationSet3 = new AnimationSet(true);
        Animation fadeIn3 = new AlphaAnimation(0.0f, 1.0f);
        fadeIn3.setDuration(1000);
        Animation comeIn3 = new TranslateAnimation(-200, 0, 0, 0);
        comeIn3.setDuration(1000);
        fadeIn3.setStartOffset(600);
        comeIn3.setStartOffset(600);
        animationSet3.addAnimation(fadeIn3);
        animationSet3.addAnimation(comeIn3);
        myScoreTextView.startAnimation(animationSet3);


        AnimationSet animationSet1y = new AnimationSet(true);
        Animation fadeIn1y = new AlphaAnimation(0.0f, 1.0f);
        fadeIn1y.setDuration(1000);
        Animation comeIn1y = new TranslateAnimation(200, 0, 0, 0);
        comeIn1y.setDuration(1000);
        animationSet1y.addAnimation(fadeIn1y);
        animationSet1y.addAnimation(comeIn1y);
        yourNameTextView.startAnimation(animationSet1y);

        AnimationSet animationSet2y = new AnimationSet(true);
        Animation fadeIn2y = new AlphaAnimation(0.0f, 1.0f);
        fadeIn2y.setDuration(1000);
        Animation comeIn2y = new TranslateAnimation(200, 0, 0, 0);
        comeIn2y.setDuration(1000);
        fadeIn2y.setStartOffset(300);
        comeIn2y.setStartOffset(300);
        animationSet2y.addAnimation(fadeIn2y);
        animationSet2y.addAnimation(comeIn2y);
        yourLevelTextView.startAnimation(animationSet2y);

        AnimationSet animationSet3y = new AnimationSet(true);
        Animation fadeIn3y = new AlphaAnimation(0.0f, 1.0f);
        fadeIn3y.setDuration(1000);
        Animation comeIn3y = new TranslateAnimation(200, 0, 0, 0);
        comeIn3y.setDuration(1000);
        fadeIn3y.setStartOffset(600);
        comeIn3y.setStartOffset(600);
        animationSet3y.addAnimation(fadeIn3y);
        animationSet3y.addAnimation(comeIn3y);
        yourScoreTextView.startAnimation(animationSet3y);


        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
//            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // TODO: If Settings has multiple levels, Up should navigate up
            // that hierarchy.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public class BackgroundURLRequestMainGame extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            HttpClient httpClient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 10000);
            try {
                String relativeURL = params[0];
                String message = params[1];
                HttpPost post = new HttpPost(CommonUtils.SERVER_BASE + relativeURL);
                StringEntity se = new StringEntity(message);
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                HttpResponse response = httpClient.execute(post);

                InputStream is = response.getEntity().getContent();

                if (is != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();

                    Intent intent = new Intent("gameword_received");
                    intent.putExtra("sender_id", against );
                    intent.putExtra("game_id", gameId );
                    intent.putExtra("game_word", sb.toString());
                    intent.putExtra("your_score", currentYourScore + getScoreForThisWord(sb.toString(), lastWord)); // senders my score = your score.. here.
                    System.out.println(sb.toString());
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    return sb.toString();
                }

            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }
            return "error";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceivedBackPress);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceivedGameWord);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        new MaterialDialog.Builder(this)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        MainGameActivity.super.onBackPressed();
                    }
                })
                .title("Leave Game")
                .titleGravity(GravityEnum.CENTER)
                .content("You will lose if you leave the game. Still continue?")
                .positiveText("YES")
                .negativeText("NO")
                .theme(Theme.LIGHT)
                .negativeColorRes(R.color.material_deep_teal_500)
                .positiveColorRes(R.color.material_red_500)
                .show();
    }
}