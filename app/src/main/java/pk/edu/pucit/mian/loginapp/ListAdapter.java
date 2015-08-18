package pk.edu.pucit.mian.loginapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mian on 8/17/2015.
 */
public class ListAdapter extends ArrayAdapter<String>
{
    int items;
    ArrayList<String> textList;
    Context context;
    ListAdapter(Context c, ArrayList<String> text , int itm)
    {
        super(c , R.layout.list_item, R.id.detailText, text);
        context = c;
        this.items = itm;
        this.textList = text;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflator = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row = inflator.inflate(R.layout.list_item, parent ,false);

        TextView textView = (TextView) row.findViewById(R.id.detailText);
        textView.setText(textList.get(position));

        return row;

    }
}