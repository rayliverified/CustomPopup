package stream.custompopupsample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class KeywordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    ArrayList<Keyword> keywordList;

    public KeywordAdapter(Context context, ArrayList<Keyword> keywordList) {
        this.mContext = context;
        this.keywordList = keywordList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keyword, parent, false);
        return new KeywordViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder genericHolder, final int position) {
        Keyword keyword = keywordList.get(position);
        KeywordViewHolder holder = (KeywordViewHolder) genericHolder;
        holder.setKeyword(keyword);
    }

    @Override
    public int getItemCount() { return keywordList.size(); }
}