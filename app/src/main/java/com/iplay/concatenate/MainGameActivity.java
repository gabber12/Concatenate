package com.iplay.concatenate;

import com.iplay.concatenate.common.BackgroundURLRequest;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
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


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MainGameActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

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

    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        CommonUtils.waitingFor = null;
        if (CommonUtils.startingGameTimer != null) {
            CommonUtils.startingGameTimer.cancel();
            CommonUtils.startingGameTimer.purge();
        }
        currentMyScore = currentYourScore = 0;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_game);
        setupActionBar();

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Register all local broadcast receiver here

        that = this;
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceivedBackPress, new IntentFilter("game_back_button"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceivedGameWord, new IntentFilter("gameword_received"));

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        pb = (ProgressBar) findViewById(R.id.progressBarTimeout);

        myScore = (TextView) findViewById(R.id.myscore);
        yourScore = (TextView) findViewById(R.id.yourscore);
        enterWord = (LockEditText) findViewById(R.id.enter_word);
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

        setWords(getIntent().getStringExtra("game_word"));
        against = getIntent().getStringExtra("against_user");
        userTurn = getIntent().getStringExtra("user_turn");
        gameId = getIntent().getIntExtra("game_id", 0);
        isBot = getIntent().getBooleanExtra("is_bot", false);
        changeEnterWordBox();

        System.out.println(CommonUtils.userId + " " + against + " " + userTurn);

        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!CommonUtils.userId.equals(userTurn) || pb.getProgress() == 0)
                    return;

                String str = enterWord.getText().toString();
                int maxMatch = getMaxMatch(str, lastWord);

                if (!CommonUtils.words.contains(str) || maxMatch == 0 || str.equals(lastWord))
                    shakeWord();
                else {
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

        });
    }

    private void resetCountdownTimer() {

        if (CommonUtils.mainGameTimer != null) CommonUtils.mainGameTimer.cancel();
        CommonUtils.mainGameTimer = new Timer();
        pb.setProgress(pb.getMax());
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
                                CommonUtils.mainGameTimer.cancel();
                            }
                        }
                        if (left <= -10 * 1000) {
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
                            CommonUtils.mainGameTimer.cancel();
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
        currentMyScore += getScoreForThisWord(str, lastWord);
        String points = String.valueOf(currentMyScore);
        while (points.length() < 3) points = "0" + points;
        myScore.setText("Score: " + points);
    }


    private void setWords(String str) {
        wordsLayout.removeAllViews();

        for (int i = 0; i < str.length(); ++i) {
            TextView newTextView = new TextView(that);
            String s = Character.toString(str.charAt(i));
            newTextView.setText(s);
            newTextView.setGravity(Gravity.CENTER);
            setBackgroundBox(newTextView, R.drawable.enter_word_background_black);
            newTextView.setHeight(getPixelsfromDP(40f));
            newTextView.setWidth(getPixelsfromDP(40f));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(getPixelsfromDP(5f), getPixelsfromDP(5f), getPixelsfromDP(5f), getPixelsfromDP(5f));
            newTextView.setLayoutParams(params);
            newTextView.setTextColor(Color.BLUE);
            wordsLayout.addView(newTextView);

        }
        lastWord = str;
        resetCountdownTimer();

    }

    private void changeEnterWordBox() {
        if (CommonUtils.userId.equals(userTurn)) {
            enterWord.setCursorVisible(true);
            setBackgroundBox(enterWord, R.drawable.enter_word_background_black);
        } else {
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
        int parentWidth = wordsLayout.getWidth();
        final int margin = getPixelsfromDP(5f), blockWidth = getPixelsfromDP(40f);
        int viewWidth = (blockWidth + margin + margin);
        int length = str.length();
        int totalWidth = viewWidth * length;
        int autoMargin = (parentWidth - totalWidth) / 2;
        List<Integer> newLeftPosition = new ArrayList<Integer>();
        for (int i = 0; i < str.length(); ++i) {
            newLeftPosition.add(autoMargin + i * viewWidth);
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

        for (int i = 0; i < wordsLayout.getChildCount(); ++i) {
            TextView textView = (TextView) wordsLayout.getChildAt(i);
            Integer curLeftPosition = textView.getLeft();
            Animation animateToStart = new TranslateAnimation(leftPosition.get(lastWord.length() - maxMatch + i) - newLeftPosition.get(i), 0, 0, 0);
            animateToStart.setDuration(800);
            animateToStart.setStartOffset(200);
            animateToStart.setInterpolator(new OvershootInterpolator());
            textView.startAnimation(animateToStart);
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

            Animation animateFromExtremeRight = new TranslateAnimation(600 - i * 50, 0, 0, 0);
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

    // Not required.
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
            currentYourScore = Math.max(currentYourScore, intent.getIntExtra("your_score", 0));
            String points = String.valueOf(currentYourScore);
            while (points.length() < 3) points = "0" + points;
            yourScore.setText("Score: " + points);
            userTurn = CommonUtils.userId;
            changeEnterWordBox();
            resetCountdownTimer();
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
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

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
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
}