package nhom4Mobile.ueh.edu.tintucapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.AdapterView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AdminPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminItemAdapter adapter;
    private List<Post> itemList;
    private Button btnAdd, btnEdit, btnDelete, logout;
    private Post selectedPost;
    private int selectedPosition = -1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);

        recyclerView = findViewById(R.id.recyclerView);
        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        logout = findViewById(R.id.logoutAdBtn);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new AdminItemAdapter(itemList, (item, position) -> {
            selectedPost = item;
            selectedPosition = position;
            Toast.makeText(this, "Đã chọn: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore(); // Tải dữ liệu khi mở trang

        // Thêm adapter cho Spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.category_list, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // Sự kiện cho Spinner
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedCategory = (String) parentView.getItemAtPosition(position);

                if (selectedCategory.equals("Tất cả")) {
                    loadDataFromFirestore(); // Tải lại toàn bộ dữ liệu
                } else {
                    filterDataByCategory(selectedCategory); // Lọc dữ liệu theo danh mục
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                loadDataFromFirestore(); // Nếu không chọn gì, tải lại toàn bộ dữ liệu
            }
        });

        // Xử lý nút thêm, sửa, xóa, đăng xuất
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPage.this, activity_add_post.class);
            startActivityForResult(intent, 1);
        });

        btnEdit.setOnClickListener(v -> {
            if (selectedPosition != RecyclerView.NO_POSITION) {
                // Lấy bài viết mới nhất từ danh sách dựa trên vị trí
                selectedPost = itemList.get(selectedPosition);

                // Mở EditPostActivity với dữ liệu được cập nhật
                Intent intent = new Intent(AdminPage.this, EditPostActivity.class);
                intent.putExtra("postId", selectedPost.getId());
                intent.putExtra("postTitle", selectedPost.getTitle());
                intent.putExtra("postDetailContent", selectedPost.getDetailContent());
                intent.putExtra("postImage", selectedPost.getImageUrl());
                intent.putExtra("postCategory", selectedPost.getCategory());
                intent.putExtra("postStatus", selectedPost.isStatus());
                startActivityForResult(intent, 2);
            } else {
                Toast.makeText(this, "Vui lòng chọn bài viết cần sửa", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (selectedPost != null) {
                deletePost(selectedPost.getId());
            } else {
                Toast.makeText(this, "Vui lòng chọn bài viết cần xóa", Toast.LENGTH_SHORT).show();
            }
        });

        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) { // Quay về từ AddPostActivity
                String selectedCategory = spinnerCategory.getSelectedItem().toString();

                if (selectedCategory.equals("Tất cả")) {
                    loadDataFromFirestore(); // Tải lại toàn bộ dữ liệu
                } else {
                    filterDataByCategory(selectedCategory); // Tải dữ liệu theo danh mục
                }
            } else if (requestCode == 2) { // Quay về từ EditPostActivity
                // Cập nhật lại danh sách sau khi chỉnh sửa
                String selectedCategory = spinnerCategory.getSelectedItem().toString();

                if (selectedCategory.equals("Tất cả")) {
                    loadDataFromFirestore(); // Tải lại toàn bộ dữ liệu
                } else {
                    filterDataByCategory(selectedCategory); // Tải dữ liệu theo danh mục
                }
            }
        }
    }

    // This should be outside the onActivityResult() method.
    private void loadDataFromFirestore() {
        db.collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String title = document.getString("title");
                            String detailContent = document.getString("detailContent");
                            String imageUrl = document.getString("imageUrl");
                            String category = document.getString("category");
                            boolean status = document.getBoolean("status");

                            Post post = new Post(id, title, detailContent, imageUrl, category, status);
                            itemList.add(post);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Lỗi khi tải dữ liệu từ Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterDataByCategory(String category) {
        db.collection("posts")
                .whereEqualTo("category", category)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getId();
                            String title = document.getString("title");
                            String detailContent = document.getString("detailContent");
                            String imageUrl = document.getString("imageUrl");
                            String postCategory = document.getString("category");
                            boolean status = document.getBoolean("status");

                            Post post = new Post(id, title, detailContent, imageUrl, postCategory, status);
                            itemList.add(post);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Lỗi khi tải dữ liệu từ Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deletePost(String postId) {
        db.collection("posts").document(postId)
                .update("status", false)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Bài viết đã được xóa", Toast.LENGTH_SHORT).show();

                    // Lấy danh mục hiện tại từ Spinner
                    String selectedCategory = spinnerCategory.getSelectedItem().toString();

                    if (selectedCategory.equals("Tất cả")) {
                        loadDataFromFirestore(); // Tải lại toàn bộ dữ liệu
                    } else {
                        filterDataByCategory(selectedCategory); // Tải dữ liệu theo danh mục
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xóa bài viết", Toast.LENGTH_SHORT).show());
    }

}
