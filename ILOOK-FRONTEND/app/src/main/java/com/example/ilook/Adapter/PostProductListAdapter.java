package com.example.ilook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ilook.Model.PostProduct;
import com.example.ilook.R;

import java.util.ArrayList;

public class PostProductListAdapter extends RecyclerView.Adapter<PostProductListAdapter.ViewHolder> {

    private ArrayList<PostProduct> mData = null ;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            textView1 = itemView.findViewById(R.id.textView30) ;
            textView2 = itemView.findViewById(R.id.textView31) ;
            textView3 = itemView.findViewById(R.id.textView32) ;
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public PostProductListAdapter(ArrayList<PostProduct> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public PostProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.post_product_list_item, parent, false) ;
        PostProductListAdapter.ViewHolder vh = new PostProductListAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(PostProductListAdapter.ViewHolder holder, int position) {
        PostProduct text = mData.get(position) ;
        holder.textView1.setText(text.getBrand()) ;
        holder.textView2.setText(text.getName()) ;
        holder.textView3.setText(text.getSize()) ;
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }
}