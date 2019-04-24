package com.example.newproject.client.ui.trademarkSetActivitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newproject.R;
import com.example.newproject.client.core.DirManager;
import com.example.newproject.client.ui.trademarkInfoActivitys.TrademarkInfoActivity;
import com.example.newproject.web.cons.CommonConstant;
import com.example.newproject.web.core.OkHttpsFactory;
import com.example.newproject.web.core.TrademarkList;
import com.example.newproject.web.domain.TrademarkDomain;
import com.example.newproject.web.domain.TrademarkListDomain;
import com.example.newproject.web.util.APICreator;
import com.example.newproject.web.util.JSONStringUtil;
import com.example.newproject.web.util.MyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class TrademarkAdapter extends RecyclerView.Adapter<TrademarkAdapter.ViewHolder> {

    private static final String TAG = "TrademarkAdapter";

    private Context mContext;
    private List<TrademarkListDomain> trademarkListDomains;


    private MyHandler handler;


    static class MyHandler extends Handler {

        WeakReference<Activity> weakReference;
        ViewHolder holder;

        public ViewHolder getHolder() {
            return holder;
        }

        public void setHolder(ViewHolder holder) {
            this.holder = holder;
        }

        public MyHandler(Activity activity) {
            weakReference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            TrademarkInfoActivity trademarkInfoActivity = (TrademarkInfoActivity) weakReference.get();


            String imgFilePath = data.getString("imgFilePath");
            Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath);
//            trademarkInfoActivity.mImageView.setImageBitmap(bitmap);
            holder.itemImage.setImageBitmap(bitmap);

        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView itemImage;
        TextView itemName;


        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            itemImage = (ImageView) view.findViewById(R.id.item_iamge);
            itemName = (TextView) view.findViewById(R.id.item_name);

        }
    }


    public TrademarkAdapter(List<TrademarkListDomain> trademarkListDomains) {
        this.trademarkListDomains = trademarkListDomains;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_library, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trademarkName = viewHolder.itemName.getText().toString();
                TrademarkListDomain trademarkListDomain = TrademarkList.getTrademarkListDomainByTrademarkName(trademarkName);

                if (trademarkListDomain == null) {
                    Toast.makeText(mContext, "找不到该商标？？？", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(mContext, TrademarkInfoActivity.class);

                intent.putExtra("trademarkId", trademarkListDomain.getTrademarkId());
                mContext.startActivity(intent);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TrademarkAdapter.ViewHolder holder, int position) {

        handler = new MyHandler((TrademarkActivity)mContext);


        TrademarkListDomain trademarkListDomain = trademarkListDomains.get(position);
        String imgFilePath = DirManager.getFilePath(String.valueOf(trademarkListDomain.getTrademarkId()), CommonConstant.TRADEMARK_TB_IMG_FILE, mContext);
        final File imgFile = new File(imgFilePath);

        if (!imgFile.exists()) {
            String imgFileApi = APICreator.createGetTrademarkTbImgApi(trademarkListDomain.getTrademarkId());
            Log.d(TAG, "init: " + imgFileApi);
            Request request = OkHttpsFactory.createRequest(imgFileApi);
            OkHttpClient client = OkHttpsFactory.getOkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(mContext, "获取图片失败", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream inputStream = response.body().byteStream();
                    //将输入流数据转化为Bitmap位图数据
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imgFile.createNewFile();
                    //创建文件输出流对象用来向文件中写入数据
                    FileOutputStream out = new FileOutputStream(imgFile);
                    //将bitmap存储为jpg格式的图片
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    //刷新文件流
                    out.flush();
                    out.close();

                    Message message = new Message();
                    Bundle data = new Bundle();
                    data.putString("imgFilePath", imgFile.getPath());
                    message.setData(data);
                    handler.setHolder(holder);
                    handler.sendMessage(message);

                }
            });
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath);
            holder.itemImage.setImageBitmap(bitmap);
        }

//        TrademarkDomain trademarkDomain = mTrademarkDomainList.get(position);
        holder.itemName.setText(trademarkListDomain.getTrademarkName());
//        Glide.with(mContext).load(trademarkDomain.getImageId()).into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return trademarkListDomains.size();
    }
}
