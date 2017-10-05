package stream.custompopupsample;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class KeywordViewHolder extends RecyclerView.ViewHolder {

    public Keyword keyword;
    public TextView mKeyword;
    public TextView mScore;
    public Context mContext;
    public static String mActivity = "KeywordViewHolder";

    public KeywordViewHolder(View itemView) {
        super(itemView);

        mKeyword = itemView.findViewById(R.id.word);
        mScore = itemView.findViewById(R.id.word_score);

        mContext = itemView.getContext();
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
        if (keyword.getKeyword() != null)
        {
            mKeyword.setText(keyword.getKeyword());
        }

        float keywordScore = keyword.getScore();
        int statScore = Math.round(keywordScore * 100);
        if (statScore <= 5 && statScore >= -5)
        {
            GradientDrawable drawable = (GradientDrawable) mKeyword.getBackground();
            drawable.setColor(ContextCompat.getColor(mContext, R.color.button_normal));
        }
        else
        {
            GradientDrawable drawable = (GradientDrawable) mKeyword.getBackground();
            drawable.setColor(Color.HSVToColor(GetStatColor((statScore + 100)/2)));
        }
        if (keywordScore >= 0)
        {
            String keywordText = " " + String.valueOf(keywordScore);
            mScore.setText(keywordText);
        }
        else
        {
            mScore.setText(String.valueOf(keywordScore));
        }
    }

    public float[] GetStatColor(int hue)
    {
        float H = (float) ((float) hue * 1.3); // Hue (note 0.4 = Green, see huge chart below)
        float S = (float) 1; // Saturation
        float B = (float) 0.9; // Brightness

        return new float[]{H, S, B};
    }
}
