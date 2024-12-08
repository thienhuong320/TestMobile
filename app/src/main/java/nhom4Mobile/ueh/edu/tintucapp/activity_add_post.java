package nhom4Mobile.ueh.edu.tintucapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class activity_add_post extends AppCompatActivity {

    private EditText etID, etTitle, etDetailContent, etImage;
    private Spinner spCategory;
    private Button btnSave;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        etID = findViewById(R.id.etId);
        etTitle = findViewById(R.id.etTitle);
        etDetailContent = findViewById(R.id.etDetailContent);
        etImage = findViewById(R.id.etImage);
        spCategory = findViewById(R.id.spCategory);
        btnSave = findViewById(R.id.btnSave);

        loadNextPostID();
        setupCategorySpinner();

        btnSave.setOnClickListener(v -> savePost());
    }

    private void loadNextPostID() {
        db.collection("posts").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        int nextPostId = documents.size() + 1;
                        String postId = String.format("%03d", nextPostId);
                        etID.setText(String.valueOf("P" + postId));
                    } else {
                        Toast.makeText(this, "Lỗi khi tải dữ liệu bài viết", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupCategorySpinner() {
        String[] categories = {"Mới nhất", "Xem nhiều", "Thời sự", "Kinh doanh", "Bất động sản", "Khoa học", "Giải trí"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
    }

    private void savePost() {
        String id = etID.getText().toString();
        String title = etTitle.getText().toString();
        String detailContent = etDetailContent.getText().toString();
        String imageUrl = etImage.getText().toString();
        String category = spCategory.getSelectedItem().toString();
        boolean status = true;

        if (title.isEmpty() || detailContent.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Post newPost = new Post(id, title, detailContent, imageUrl, category, status);

        db.collection("posts").document(id).set(newPost)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Bài viết đã được lưu thành công", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi lưu bài viết", Toast.LENGTH_SHORT).show());
    }
}
