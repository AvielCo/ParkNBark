package com.evan.parknbark.settings.admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.evan.parknbark.R;

import java.util.List;
import java.util.Map;

public class UsersListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private Map<UserItem, List<String>> childListItems;
    private List<UserItem> parentListItems;

    public UsersListAdapter(Context context, Map<UserItem, List<String>> childListItems, List<UserItem> parentListItems) {
        this.context = context;
        this.childListItems = childListItems;
        this.parentListItems = parentListItems;
    }

    //Parent options
    @Override
    public int getGroupCount() {
        return parentListItems.size();
    }

    @Override
    public UserItem getGroup(int groupPosition) {
        return parentListItems.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        String displayName = getGroup(groupPosition).getDisplayName(),
                email = getGroup(groupPosition).getEmail();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_users_list, parent);
        }
        TextView tv_displayName = view.findViewById(R.id.text_view_display_name),
                tv_email = view.findViewById(R.id.text_view_email);
        tv_displayName.setText(displayName);
        tv_email.setText(email);

        return view;
    }

    //Child options
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childListItems.get(parentListItems.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.activity_users_list, parent);
        }
        TextView item = view.findViewById(R.id.text_view_item);
        item.setText(childText);
        return view;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childListItems.get(parentListItems.get(groupPosition)).size();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
