package nhom4Mobile.ueh.edu.tintucapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class Thong_tin_ca_nhan_Activity extends AppCompatActivity {

    private TextView userName, userEmail;
    private Button btnLogout, btnDeleteAccount;
    private ImageButton btnBack;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);  // Bật chế độ Edge-to-Edge
        setContentView(R.layout.activity_thong_tin_ca_nhan);

        // Khởi tạo FirebaseAuth và lấy người dùng hiện tại
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Ánh xạ các view từ layout
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        btnLogout = findViewById(R.id.btnLogout);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);
        btnBack = findViewById(R.id.btnBack);

        // Kiểm tra người dùng đã đăng nhập chưa
        if (currentUser != null) {
            userName.setText(currentUser.getDisplayName());  // Hiển thị tên người dùng
            userEmail.setText(currentUser.getEmail());  // Hiển thị email người dùng
        } else {
            // Nếu chưa đăng nhập, chuyển về trang đăng nhập
            Intent intent = new Intent(Thong_tin_ca_nhan_Activity.this, Login.class);
            startActivity(intent);
            finish();
        }

        // Xử lý sự kiện đăng xuất
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();  // Đăng xuất Firebase
            Toast.makeText(Thong_tin_ca_nhan_Activity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Thong_tin_ca_nhan_Activity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Xử lý sự kiện xóa tài khoản
        btnDeleteAccount.setOnClickListener(v -> {
            if (currentUser != null) {
                currentUser.delete()  // Xóa tài khoản Firebase
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Thong_tin_ca_nhan_Activity.this, "Tài khoản đã được xóa", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Thong_tin_ca_nhan_Activity.this, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Thong_tin_ca_nhan_Activity.this, "Lỗi khi xóa tài khoản", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Xử lý các WindowInsets (đảm bảo nội dung không bị che khuất bởi thanh status và navigation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
