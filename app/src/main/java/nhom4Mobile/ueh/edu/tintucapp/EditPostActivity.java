package nhom4Mobile.ueh.edu.tintucapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditPostActivity extends AppCompatActivity {

    private EditText etID, etTitle, etDetailContent, etImage;
    private Spinner spCategory;
    private Button btnSave;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        // Ánh xạ các EditText, Spinner và Button
        etID = findViewById(R.id.etId);
        etTitle = findViewById(R.id.etTitle);
        etDetailContent = findViewById(R.id.etDetailContent);
        etImage = findViewById(R.id.etImage);
        spCategory = findViewById(R.id.spCategory);
        btnSave = findViewById(R.id.btnSave);

        // Nhận dữ liệu từ AdminPage
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId"); // Gán giá trị cho postId
        etID.setText(postId); // Hiển thị ID bài viết
        etTitle.setText(intent.getStringExtra("postTitle"));
        etDetailContent.setText(intent.getStringExtra("postDetailContent"));
        etImage.setText(intent.getStringExtra("postImage"));

        // Thiết lập Spinner với danh mục
        setupCategorySpinner(intent.getStringExtra("postCategory"));

        // Sự kiện nhấn nút "Lưu"
        btnSave.setOnClickListener(v -> savePost());
    }

    private void setupCategorySpinner(String selectedCategory) {
        // Lấy danh sách danh mục từ strings.xml
        String[] categories = getResources().getStringArray(R.array.category_list_edit);

        // Adapter cho Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        // Đặt giá trị được chọn sẵn
        if (selectedCategory != null) {
            int position = adapter.getPosition(selectedCategory);
            if (position >= 0) {
                spCategory.setSelection(position);
            }
        }
    }

    private void savePost() {
        String title = etTitle.getText().toString().trim();
        String detailContent = etDetailContent.getText().toString().trim();
        String image = etImage.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString(); // Lấy giá trị từ Spinner

        // Kiểm tra dữ liệu nhập
        if (postId == null || postId.isEmpty()) {
            Toast.makeText(this, "Không thể xác định bài viết cần chỉnh sửa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || detailContent.isEmpty() || image.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật bài viết trên Firestore
        db.collection("posts").document(postId)
                .update("title", title, "detailContent", detailContent, "imageUrl", image, "category", category)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Bài viết đã được cập nhật", Toast.LENGTH_SHORT).show();
                    // Trả lại kết quả cho AdminPage
                    setResult(RESULT_OK);
                    finish(); // Quay lại AdminPage
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi cập nhật bài viết", Toast.LENGTH_SHORT).show());
    }
}
