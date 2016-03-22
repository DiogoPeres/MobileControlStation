package com.diogoperes.mobilecontrolstation.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.diogoperes.mobilecontrolstation.MainActivity;
import com.diogoperes.mobilecontrolstation.R;
import com.diogoperes.mobilecontrolstation.UV;

import java.util.ArrayList;

public class UVListAdapter extends BaseAdapter /*extends ArrayAdapter<String>*/{

    private MainActivity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    UV tempValues=null;
    int i=0;

    public UVListAdapter(Activity a, ArrayList d) {
        activity = (MainActivity) a;
        data=d;
        inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        if(data.size()<=0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView txtTitle;
        public ImageView imageView;
        public TextView extratxt;
        public TextView txtSelected;


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.list_uv, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.txtTitle = (TextView) vi.findViewById(R.id.item);
            holder.imageView = (ImageView) vi.findViewById(R.id.icon);
            holder.extratxt = (TextView) vi.findViewById(R.id.textView1);
            holder.txtSelected = (TextView) vi.findViewById(R.id.uv_selected);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        } else
            holder=(ViewHolder)vi.getTag();
        if(data.size()<=0){
            holder.txtTitle.setText("No Data");

        }else{
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( UV ) data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.txtTitle.setText( ""+ tempValues.getSERVER_ADDRESS() );
            holder.extratxt.setText("" + tempValues.getSERVER_PORT());
            holder.imageView.setImageResource(tempValues.getType().getIcon());
            if(tempValues.equals(activity.getUvSelected())){
                holder.txtSelected.setVisibility(View.VISIBLE);
            }else{
                holder.txtSelected.setVisibility(View.GONE);
            }


            /******** Set Item Click Listner for LayoutInflater for each row *******/

            //vi.setOnClickListener(new OnItemClickListener( position ));
        }

        return vi;
    }
}


//public class UVListAdapter extends ArrayAdapter<String>{
//
//
//
//        private final Activity context;
//        private final String[] itemname;
//        private final Integer[] imgid;
//
//        public UVListAdapter(Activity context, String[] itemname, Integer[] imgid ) {
//            super(context, R.layout.list_uv, itemname);
//
//            // TODO Auto-generated constructor stub
//
//            this.context=context;
//            this.itemname=itemname;
//            this.imgid=imgid;
//        }
//
//        public View getView(int position,View view,ViewGroup parent) {
//            LayoutInflater inflater=context.getLayoutInflater();
//            View rowView=inflater.inflate(R.layout.list_uv, null,true);
//
//            TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
//            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
//            TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);
//
//            txtTitle.setText(itemname[position]);
//            imageView.setImageResource(imgid[position]);
//            extratxt.setText("Description "+itemname[position]);
//            return rowView;
//
//        };
//
//
//}
