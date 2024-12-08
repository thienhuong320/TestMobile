package nhom4Mobile.ueh.edu.tintucapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class News_Detail_Activity extends AppCompatActivity {

    private ImageButton btn_back, btn_fav, btn_save;
    private TextView titleText, categoryText, contentText;
    private ImageView txt_img;
    private boolean isFavorite = false; // Trạng thái lưu tin yêu thích
    private boolean isSaved = false; // Trạng thái lưu tin "save"
    private FirebaseFirestore db;
    private String postId; // Unique identifier for the post
    private String userEmail; // User's email for saving favorites

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get current user's email
        userEmail = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                : "anonymous@example.com";

        // Liên kết các nút
        btn_back = findViewById(R.id.btnBack);
        btn_fav = findViewById(R.id.btn_fav);
        titleText = findViewById(R.id.txtTitle);
        txt_img = findViewById(R.id.txt_image);
        categoryText = findViewById(R.id.txtStatus);
        contentText = findViewById(R.id.txtBody);
        btn_save = findViewById(R.id.btn_save);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String category = intent.getStringExtra("category");
        String imageUrl = intent.getStringExtra("imageUrl");
        String content = intent.getStringExtra("content");
        postId = intent.getStringExtra("id");

        // Gán dữ liệu vào các TextView
        titleText.setText(title);
        categoryText.setText(category);
        contentText.setText(content);

        // Sử dụng Glide để tải ảnh từ URL vào ImageView
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(txt_img);

        // Lấy trạng thái từ Firestore
        fetchStateFromFirestore();

        // Xử lý nút quay lại
        btn_back.setOnClickListener(v -> onBackPressed());

        // Xử lý nút lưu yêu thích
        btn_fav.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            updateFavoriteIcon();
            saveFavorite();

            String message = isFavorite ? "Đã lưu vào tin yêu thích" : "Đã bỏ khỏi tin yêu thích";
            Toast.makeText(News_Detail_Activity.this, message, Toast.LENGTH_SHORT).show();
        });

        // Xử lý nút lưu "save"
        btn_save.setOnClickListener(v -> {
            isSaved = !isSaved;
            updateSaveIcon();
            saveFavorite();

            String message = isSaved ? "Đã lưu tin vào danh sách" : "Đã bỏ khỏi danh sách lưu";
            Toast.makeText(News_Detail_Activity.this, message, Toast.LENGTH_SHORT).show();
        });
    }


    private void updateFavoriteIcon() {
        if (isFavorite) {
            btn_fav.setImageResource(R.drawable.baseline_star_24);  // Đổi icon yêu thích
        } else {
            btn_fav.setImageResource(R.drawable.baseline_star_border_24);  // Đặt lại icon yêu thích
        }
    }

    private void updateSaveIcon() {
        if (isSaved) {
            btn_save.setImageResource(R.drawable.baseline_turned_in_24);  // Đổi icon "save"
        } else {
            btn_save.setImageResource(R.drawable.baseline_turned_in_not_24);  // Đặt lại icon "save"
        }
    }

    private void saveFavorite() {
        // Create a document with the post ID and user email as the unique identifier
        DocumentReference docRef = db.collection("new").document(postId + "_" + userEmail);

        // Create a map with the data to be saved
        Map<String, Object> data = new HashMap<>();
        data.put("email", userEmail);
        data.put("idpost", postId);
        data.put("favor", isFavorite);
        data.put("save", isSaved);

        // Save the data to Firestore
        docRef.set(data)
                .addOnSuccessListener(aVoid -> {
                    // Document was successfully written
                    Log.d("News_Detail_Activity", "Favorite state saved successfully!");
                })
                .addOnFailureListener(e -> {
                    // Error occurred while writing document
                    Log.w("News_Detail_Activity", "Error saving favorite state", e);
                });
    }

    private void fetchStateFromFirestore() {
        DocumentReference docRef = db.collection("new").document(postId + "_" + userEmail);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                isFavorite = documentSnapshot.getBoolean("favor") != null && documentSnapshot.getBoolean("favor");
                isSaved = documentSnapshot.getBoolean("save") != null && documentSnapshot.getBoolean("save");

                // Cập nhật giao diện
                updateFavoriteIcon();
                updateSaveIcon();
            }
        }).addOnFailureListener(e -> {
            Log.w("News_Detail_Activity", "Error fetching state from Firestore", e);
        });
    }



}