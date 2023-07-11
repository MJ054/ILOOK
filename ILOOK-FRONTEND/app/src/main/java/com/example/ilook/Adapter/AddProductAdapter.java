package com.example.ilook.Adapter;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ilook.AddProductActivity;
import com.example.ilook.MainActivity;
import com.example.ilook.Model.PostProduct;
import com.example.ilook.R;

import java.io.Serializable;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class AddProductAdapter extends RecyclerView.Adapter<AddProductAdapter.ViewHolder> {

    private ArrayList<PostProduct> mData = null ;
    private Context mContext = null;

    private ItemClickListener listener;

    // 생성자에서 데이터 리스트 객체, Context를 전달받음.
    public AddProductAdapter(ArrayList<PostProduct> list, Context context, ItemClickListener listener) {
        mData = list ;
        mContext = context;

        this.listener = listener;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView_category;
        ConstraintLayout constraintLayout;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            textView1 = itemView.findViewById(R.id.textView_brand) ;
            textView2 = itemView.findViewById(R.id.textView_name) ;
            textView3 = itemView.findViewById(R.id.textView_size) ;
            textView_category = itemView.findViewById(R.id.textView_category);
            constraintLayout = itemView.findViewById(R.id.product_add_layout);
            imageView = itemView.findViewById(R.id.btn_delete_product);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemSelected(view,getAdapterPosition());
                }
            });
        }

    }


    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public AddProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_product_add, parent, false) ;
        AddProductAdapter.ViewHolder vh = new AddProductAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(AddProductAdapter.ViewHolder holder, int position) {
        PostProduct postProduct = mData.get(position);
        System.out.println(postProduct.getCategory());
        holder.textView_category.setText(postProduct.getCategory());
        holder.textView1.setText(postProduct.getBrand());
        holder.textView2.setText(postProduct.getName());
        holder.textView3.setText(postProduct.getSize());


       holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemSelected(view,position);
            }
        });

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mData.size());
            }
        });

    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }

    public ArrayList<PostProduct> getData(){
        return this.mData;
    }

}