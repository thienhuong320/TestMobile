package nhom4Mobile.ueh.edu.tintucapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Tin_doc_sau_Activity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tin_gan_sao);

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Lấy và hiển thị tất cả bài viết với trạng thái save = true
        loadArticles();
    }

    private void loadArticles() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail(); // Lấy email của người dùng đã đăng nhập

            if (userEmail != null) {
                db.collection("new")
                        .whereEqualTo("email", userEmail) // So khớp email với email của người dùng
                        .whereEqualTo("save", true) // Lọc bài viết có trạng thái save = true
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                LinearLayout articlesContainer = findViewById(R.id.articles_container);
                                articlesContainer.removeAllViews(); // Xóa các bài viết cũ

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String idPost = document.getString("idpost"); // Lấy idpost từ bảng new

                                    if (idPost != null) {
                                        // Lấy dữ liệu từ bảng posts dựa trên idPost
                                        db.collection("posts")
                                                .document(idPost)
                                                .get()
                                                .addOnCompleteListener(postTask -> {
                                                    if (postTask.isSuccessful() && postTask.getResult() != null) {
                                                        DocumentSnapshot postDocument = postTask.getResult();
                                                        String title = postDocument.getString("title");
                                                        String content = postDocument.getString("detailContent");
                                                        String imageUrl = postDocument.getString("imageUrl");
                                                        boolean isFavorited = document.getBoolean("favor") != null && document.getBoolean("favor");
                                                        boolean isSaved = document.getBoolean("save") != null && document.getBoolean("save");
                                                        String documentId = document.getId(); // Lưu trữ ID của tài liệu

                                                        // Hiển thị dữ liệu
                                                        displayArticle(title, content, imageUrl, articlesContainer, isFavorited, isSaved, documentId);
                                                    } else {
                                                        Log.d("Tin_gan_sao_Activity", "Error getting post document: ", postTask.getException());
                                                    }
                                                });
                                    }
                                }
                            } else {
                                Log.d("Tin_doc_sau_Activity", "Error getting documents: ", task.getException());
                                Toast.makeText(this, "Lỗi khi lấy dữ liệu.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Không thể lấy email người dùng.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show();
        }
    }



    private void displayArticle(String title, String content, String imageUrl, LinearLayout articlesContainer, boolean isFavorited, boolean isSaved, String documentId) {
        // Tạo một CardView cho mỗi bài viết
        LinearLayout cardView = new LinearLayout(this);
        cardView.setOrientation(LinearLayout.VERTICAL);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        cardView.setPadding(50, 50, 50, 50);
        cardView.setBackgroundResource(R.drawable.button_border); // Đảm bảo bạn có hình nền CardView

        // Tạo layout cho tiêu đề và nội dung
        LinearLayout articleLayout = new LinearLayout(this);
        articleLayout.setOrientation(LinearLayout.VERTICAL);

// Tạo Title TextView
        TextView titleTextView = new TextView(this);
        titleTextView.setText(title);
        titleTextView.setTextSize(30);
        titleTextView.setTextColor(Color.BLACK); // Đặt màu chữ
        titleTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        titleTextView.setPadding(0, 0, 0, 20); // Khoảng cách dưới tiêu đề
        articleLayout.addView(titleTextView);

// Tạo Content TextView
        TextView contentTextView = new TextView(this);
        contentTextView.setText(content);
        contentTextView.setTextSize(18);
        contentTextView.setTextColor(Color.DKGRAY); // Đặt màu chữ
        contentTextView.setPadding(0, 0, 0, 20); // Khoảng cách dưới nội dung
        articleLayout.addView(contentTextView);


        // Tạo ImageView cho hình ảnh
        ImageView newsImage = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600 // Chiều cao hình ảnh (cố định hoặc theo yêu cầu)
        );
        newsImage.setLayoutParams(imageParams);
        newsImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Picasso.get().load(imageUrl).into(newsImage, new Callback() {
            @Override
            public void onSuccess() {
                newsImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                newsImage.setVisibility(View.GONE);
                Toast.makeText(Tin_doc_sau_Activity.this, "Không thể tải hình ảnh", Toast.LENGTH_SHORT).show();
            }
        });
        articleLayout.addView(newsImage);

        // Tạo layout chứa các nút Favor và Save
        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

// Nút Favor
        final boolean[] favorited = {isFavorited}; // Sử dụng mảng để giữ trạng thái có thể thay đổi
        ImageButton btnFavor = new ImageButton(this);
        btnFavor.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        btnFavor.setImageResource(favorited[0] ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);

        btnFavor.setOnClickListener(v -> {
            // Chuyển đổi trạng thái favor
            favorited[0] = !favorited[0];
            btnFavor.setImageResource(favorited[0] ? R.drawable.baseline_star_24 : R.drawable.baseline_star_border_24);

            // Cập nhật trạng thái "favor" trong Firestore
            DocumentReference docRef = db.collection("new").document(documentId);
            docRef.update("favor", favorited[0])
                    .addOnSuccessListener(aVoid -> {
                        // Hiển thị thông báo trạng thái được cập nhật thành công
                        Toast.makeText(this, favorited[0] ? "Đã thêm vào Favor" : "Đã bỏ khỏi Favor", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý lỗi khi cập nhật trạng thái Favor
                        Toast.makeText(this, "Lỗi khi cập nhật trạng thái Favor", Toast.LENGTH_SHORT).show();
                    });
        });

// Nút Save
        final boolean[] saved = {isSaved}; // Sử dụng mảng để giữ trạng thái có thể thay đổi
        ImageButton btnSave = new ImageButton(this);
        btnSave.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        btnSave.setImageResource(saved[0] ? R.drawable.baseline_turned_in_24 : R.drawable.baseline_turned_in_not_24);

        btnSave.setOnClickListener(v -> {
            // Chuyển đổi trạng thái save
            saved[0] = !saved[0];
            btnSave.setImageResource(saved[0] ? R.drawable.baseline_turned_in_24 : R.drawable.baseline_turned_in_not_24);

            // Cập nhật trạng thái "save" trong Firestore
            DocumentReference docRef = db.collection("new").document(documentId);
            docRef.update("save", saved[0])
                    .addOnSuccessListener(aVoid -> {
                        // Hiển thị thông báo trạng thái được cập nhật thành công
                        Toast.makeText(this, saved[0] ? "Đã lưu bài viết" : "Đã bỏ lưu bài viết", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý lỗi khi cập nhật trạng thái Save
                        Toast.makeText(this, "Lỗi khi cập nhật trạng thái Save", Toast.LENGTH_SHORT).show();
                    });
        });




        // Thêm các nút vào layout
        buttonLayout.addView(btnFavor);
        buttonLayout.addView(btnSave);

        // Thêm layout bài viết vào CardView
        cardView.addView(articleLayout);
        cardView.addView(buttonLayout);

        // Thêm CardView vào container
        articlesContainer.addView(cardView);
        cardView.setBackgroundResource(R.drawable.button_border); // Nền chỉ áp dụng cho CardView

    }


}