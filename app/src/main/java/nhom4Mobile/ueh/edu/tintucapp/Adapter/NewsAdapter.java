package nhom4Mobile.ueh.edu.tintucapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final Context context;
    private final List<Post> postList;
    private final FirebaseFirestore db; // Firebase Firestore instance
    private final String userEmail; // Email của người dùng để lưu trạng thái
    private OnItemClickListener listener; // Listener để lắng nghe sự kiện click

    public NewsAdapter(Context context, List<Post> postList, FirebaseFirestore db, String userEmail) {
        this.context = context;
        this.postList = postList;
        this.db = db;
        this.userEmail = userEmail;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_list, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.txt_title.setText(post.getTitle());
        holder.txt_body.setText(post.getDetailContent());
        holder.txt_category.setText(post.getCategory());

        // Load ảnh với Glide
        Glide.with(context)
                .load(post.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.txt_img);

        // Lấy trạng thái yêu thích và lưu từ Firestore
        fetchStateFromFirestore(holder, post.getId());

        // Xử lý sự kiện click cho nút yêu thích
        holder.btn_fav.setOnClickListener(v -> {
            boolean isFavorite = holder.btn_fav.isSelected();
            holder.btn_fav.setSelected(!isFavorite);
            updateFavoriteIcon(holder, !isFavorite);
            saveFavoriteState(post.getId(), !isFavorite, holder.btn_save.isSelected());
            String message = !isFavorite ? "Đã lưu vào tin yêu thích" : "Đã bỏ khỏi tin yêu thích";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });

        // Xử lý sự kiện click cho nút lưu
        holder.btn_save.setOnClickListener(v -> {
            boolean isSaved = holder.btn_save.isSelected();
            holder.btn_save.setSelected(!isSaved);
            updateSaveIcon(holder, !isSaved);
            saveFavoriteState(post.getId(), holder.btn_fav.isSelected(), !isSaved);
            String message = !isSaved ? "Đã lưu bài viết" : "Đã bỏ lưu bài viết";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        });

        // Xử lý sự kiện click cho item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(post);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Post post);
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView txt_title, txt_body, txt_category;
        ImageView txt_img;
        ImageButton btn_save, btn_fav;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_title = itemView.findViewById(R.id.txt_title);
            txt_body = itemView.findViewById(R.id.txt_body);
            txt_category = itemView.findViewById(R.id.txt_category);
            txt_img = itemView.findViewById(R.id.txt_image);
            btn_save = itemView.findViewById(R.id.btn_save);
            btn_fav = itemView.findViewById(R.id.btn_fav);
        }
    }

    private void updateFavoriteIcon(NewsViewHolder holder, boolean isFavorite) {
        holder.btn_fav.setImageResource(isFavorite ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);
    }

    private void updateSaveIcon(NewsViewHolder holder, boolean isSaved) {
        holder.btn_save.setImageResource(isSaved ? R.drawable.baseline_turned_in_24 : R.drawable.baseline_turned_in_not_24);
    }

    private void saveFavoriteState(String postId, boolean isFavorite, boolean isSaved) {
        DocumentReference docRef = db.collection("new").document(postId + "_" + userEmail);

        Map<String, Object> data = new HashMap<>();
        data.put("email", userEmail);
        data.put("idpost", postId);
        data.put("favor", isFavorite);
        data.put("save", isSaved);

        docRef.set(data)
                .addOnSuccessListener(aVoid -> Log.d("NewsAdapter", "Trạng thái đã lưu thành công!"))
                .addOnFailureListener(e -> Log.w("NewsAdapter", "Lỗi khi lưu trạng thái", e));
    }

    private void fetchStateFromFirestore(NewsViewHolder holder, String postId) {
        DocumentReference docRef = db.collection("new").document(postId + "_" + userEmail);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                boolean isFavorite = documentSnapshot.getBoolean("favor") != null && documentSnapshot.getBoolean("favor");
                boolean isSaved = documentSnapshot.getBoolean("save") != null && documentSnapshot.getBoolean("save");

                holder.btn_fav.setSelected(isFavorite);
                holder.btn_save.setSelected(isSaved);
                updateFavoriteIcon(holder, isFavorite);
                updateSaveIcon(holder, isSaved);
            }
        }).addOnFailureListener(e -> Log.w("NewsAdapter", "Lỗi khi lấy trạng thái từ Firestore", e));
    }
}
