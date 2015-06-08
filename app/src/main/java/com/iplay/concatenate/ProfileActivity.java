package com.iplay.concatenate;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.facebook.Session;
import com.iplay.concatenate.common.CommonUtils;
import com.iplay.concatenate.support.CircularProfilePicView;


public class ProfileActivity extends Fragment {
    //    public String url;
    View myFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myFragment = inflater.inflate(R.layout.activity_profile, container, false);
        super.onCreate(savedInstanceState);

        ((TextView) myFragment.findViewById(R.id.profile_heading)).setTypeface(CommonUtils.FreightSansFont);


        ((TextView) myFragment.findViewById(R.id.profile_name)).setText(CommonUtils.name);
        ((TextView) myFragment.findViewById(R.id.profile_score)).setText(CommonUtils.score + " XP");
        ((CircularProfilePicView) myFragment.findViewById(R.id.profile_pic_ac)).setProfileId(CommonUtils.userId);
        Button btn = (Button) myFragment.findViewById(R.id.logout_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog md = new MaterialDialog.Builder(getActivity())
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Session.getActiveSession().closeAndClearTokenInformation();
                                Intent in = new Intent(getActivity(), SplashScreenActivity.class);
                                startActivity(in);
                                getActivity().finish();

                            }
                        })
                        .title("Logout")
                        .titleGravity(GravityEnum.CENTER)
                        .content("Do you really want to logout?")
                        .positiveText("Logout")
                        .negativeText("Cancel")
                        .theme(Theme.LIGHT)
                        .negativeColorRes(R.color.material_deep_teal_500)
                        .positiveColorRes(R.color.material_red_500)
                        .show();


            }
        });
//        Bundle bd = new Bundle();
//        bd.putString("fields", "cover");
//        new Request(
//                Session.getActiveSession(),
//                CommonUtils.userId,
//                bd,
//                HttpMethod.GET,
//                new Request.Callback() {
//                    public void onCompleted(Response response) {
//                        Response res = response;
//                        try {
//                            if (response != null) {
//                                url = response.getGraphObject().getInnerJSONObject().getJSONObject("cover").getString("source");
//                                new DownloadImageTask((ImageView)myFragment.findViewById(R.id.cover)).execute(url);
//                                System.out.print(url);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//        ).executeAsync();

        return myFragment;
    }
//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        ImageView bmImage;
//
//        public DownloadImageTask(ImageView bmImage) {
//            this.bmImage = bmImage;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//
//        }
//    }


}
