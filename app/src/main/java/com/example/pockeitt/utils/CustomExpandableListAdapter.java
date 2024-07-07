package com.example.pockeitt.utils;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.example.pockeitt.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> expandableListDetail;


    public CustomExpandableListAdapter(Context context, List<String> expandableListTitle, HashMap<String, List<String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;

    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return Objects.requireNonNull(this.expandableListDetail.get(this.expandableListTitle.get(listPosition))).get(expandedListPosition);

    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String expandedListText = (String) getChild(listPosition, expandedListPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView expandedListTextView = convertView.findViewById(R.id.expandedListItem);
        // Set the desired color for the subitems here
//        expandedListTextView.setTextColor(ContextCompat.getColor(context, R.color.white));

        String key = expandableListTitle.get(listPosition);
        // Get the list associated with the key from expandableListDetail
        List<String> list = expandableListDetail.get(key);

        expandedListTextView.setText(expandedListText);

        if (expandedListText.contains("here")) {

            Log.d(TAG, "getChildView: ");
            convertView.setOnClickListener(v -> {


                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
//                View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottomsheet, parent, false);
//                bottomSheetDialog.setContentView(bottomSheetView);
//                bottomSheetDialog.show();

//                Toast.makeText(context, "You can add item now", Toast.LENGTH_SHORT).show();


                View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottomsheet, parent, false);

                // Ensure the bottom sheet's height is set to match_parent
                bottomSheetView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                bottomSheetDialog.setContentView(bottomSheetView);

                // Get the BottomSheetBehavior
                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
                bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetDialog.show();

            });
        }
        Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins);
        expandedListTextView.setTypeface(typeface);
        return convertView;

    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.poppins);
        listTitleTextView.setTypeface(typeface);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }


}
