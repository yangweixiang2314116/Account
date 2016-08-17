package com.example.account;

import java.util.ArrayList;
import com.example.module.ImageItem;
import com.example.module.ImageLoader;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class AccountAllImageListAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    private Resources mResources;
    private Context mContext = null;

    protected ArrayList<ImageItem> mDetailImageList = new ArrayList<ImageItem>();

    public AccountAllImageListAdapter(Context context, ArrayList<ImageItem> dataSourece) {

        super();

        Log.i(Constants.TAG, "-----------AccountDetailImageListAdapter start--------");

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mResources = context.getResources();

        mDetailImageList = dataSourece;

        mContext = context;

        Log.i(Constants.TAG, "-----------AccountDetailImageListAdapter end--------");
    }

    private Handler WindTalker = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            notifyDataSetChanged();
        }
    };

    public void updateUI() {
        WindTalker.sendEmptyMessage(0);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        Log.i(Constants.TAG, "-----------getCount--------" + mDetailImageList.size());
        return mDetailImageList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        Log.i(Constants.TAG, "-----------getItem--------" + position);
        if (position < 0 || position > mDetailImageList.size()) {
            Log.i(Constants.TAG, "-----------getItem-error-------" + position);
            return null;
        }
        return mDetailImageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        Log.i(Constants.TAG, "-----------getItemId--------");
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.i(Constants.TAG, "-----------getView----position----" + position);

        ViewHolderImage holderImage = null;

        if (convertView == null) {

            holderImage = new ViewHolderImage();

            convertView = mInflater.inflate(R.layout.activity_account_all_image_grid_item, null);

            holderImage.content = (ImageView) convertView.findViewById(R.id.id_item_image);
            convertView.setTag(holderImage);

        } else {

            holderImage = (ViewHolderImage) convertView.getTag();
        }

        /*
        String DecoderImagePath = "";
        if(mDetailImageList.get(position).Path.equals("")  == false) {
            String ImagePath = mDetailImageList.get(position).Path;
            DecoderImagePath = "file://" + ImagePath;
        }
        else
        {
            DecoderImagePath = mDetailImageList.get(position).ServerPath;
        }

        Log.i(Constants.TAG, "---position----" + position + "--DecoderImagePath---"+DecoderImagePath);

        if (DecoderImagePath.isEmpty() == false) {
            Picasso.with(mContext).load(DecoderImagePath)
                    .placeholder(R.mipmap.info_item_image).into(holderImage.content);
        }
        */
        String ImagePath = mDetailImageList.get(position).Path;
        ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(ImagePath, holderImage.content);

        return convertView;
    }

    class ViewHolderImage {
        private ImageView content;
    }
}