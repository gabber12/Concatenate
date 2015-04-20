package com.iplay.concatenate;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabber12 on 12/04/15.
 */
public class FriendListAdapter extends ArrayAdapter<FriendModel> implements Filterable{
    List<FriendModel> list;
    List<FriendModel> tlist;
    private LayoutInflater mInflater;
    Context mContext;
    int resource;
    Filter customFilter;
    public FriendListAdapter(Context ctx,int resource, List<FriendModel> list) {
        super(ctx, resource, list);
        this.list = list;
        this.resource = resource;
        mContext = ctx;
        tlist = new ArrayList<FriendModel>(list);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public FriendModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Filter getFilter() {
        customFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new Filter.FilterResults();
                ArrayList<FriendModel> tempList=new ArrayList<FriendModel>();
                //constraint is the result from text you want to filter against.
                //objects is your data set you will filter from
                if(constraint != null && tlist!=null) {
                    int length=tlist.size();
                    int i = 0;

                    while(i<length){
                        FriendModel item=tlist.get(i);
                        System.out.println("=="+constraint);
                        String ct = constraint.toString().toLowerCase();
                        String [] nameComp = item.getName().split(" ");
                        for(int j = 0; j < nameComp.length; j++) {
                            nameComp[j] = nameComp[j].toLowerCase();
                            System.out.println(j+" => "+ nameComp[j]+ " "+ nameComp[j].startsWith(ct));
                            if(nameComp[j].startsWith(ct)) {
                                tempList.add(item);
                                break;
                            }
                        }
                        i++;
                    }

                    //following two lines is very important
                    //as publish result can only take FilterResults objects
                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }
                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence contraint, FilterResults results) {
                list = (ArrayList<FriendModel>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return customFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        final View result;

        if (convertView == null) {

            result = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        } else {
            result = convertView;
        }
        // TODO replace findViewById by ViewHolder
        ((CircularProfilePicView) result.findViewById(R.id.profile_pic)).setProfileId(list.get(position).getId());
        ((TextView) result.findViewById(R.id.profile_name)).setText(list.get(position).getName());
        return result;


    }
}

