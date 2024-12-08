package nhom4Mobile.ueh.edu.tintucapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private List<Post> postList;
    private Context context;
    private OnItemClickListener listener;

    public SearchAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_search, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvTitle.setText(post.getTitle());
        holder.tvCategory.setText(post.getCategory());
        Glide.with(context).load(post.getImageUrl()).into(holder.ivThumbnail);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory;
        ImageView ivThumbnail;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
        }
    }
}
