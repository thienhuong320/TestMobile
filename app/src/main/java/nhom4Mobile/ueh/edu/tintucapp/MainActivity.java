package nhom4Mobile.ueh.edu.tintucapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements CategorySelectionListener {
    private AppBarLayout topNavigation;
    private FrameLayout fragmentContainer;
    private String currentCategory = "Mới nhất";
    private FirestoreSyncManager firestoreSyncManager;

    @Override
    public void onCategorySelected(String category) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof HomeFragment) {
            // Gọi updateCategory để cập nhật danh mục trong HomeFragment
            ((HomeFragment) currentFragment).updateCategory(category);
        } else {
            // Nếu không phải HomeFragment, tải lại HomeFragment với danh mục mới
            loadFragment(HomeFragment.newInstance(category));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topNavigation = findViewById(R.id.mainAppBar);
        fragmentContainer = findViewById(R.id.fragment_container);

        ChipGroup chipGroup = findViewById(R.id.menuChipGroup);

        // Kiểm tra nếu chip mặc định chưa được chọn và chỉ chọn một lần
        Chip defaultChip = findViewById(R.id.chipLatest);
        if (defaultChip != null && !defaultChip.isChecked()) {
            defaultChip.setChecked(true);
            onCategorySelected(defaultChip.getText().toString()); // Gọi sự kiện mặc định
        }

        // Đăng ký thiết bị nhận thông báo từ topic FCM
        FirebaseMessaging.getInstance().subscribeToTopic("newsUpdates")
                .addOnCompleteListener(task -> {
                    String msg = task.isSuccessful() ? "Đăng ký thành công" : "Đăng ký thất bại";
                    Log.d("FCM", msg);
                });

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Kiểm tra nếu có chip nào được chọn
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);  // Lấy ID chip đầu tiên được chọn
                Chip chip = findViewById(checkedId);
                if (chip != null) {
                    String selectedCategory = chip.getText().toString();
                    onCategorySelected(selectedCategory); // Gọi giao diện để xử lý thay đổi danh mục
                }
            }
        });

        loadFragment(new HomeFragment());

        // Thiết lập BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
                topNavigation.setVisibility(View.VISIBLE);
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchhFragment();
                topNavigation.setVisibility(View.GONE);
            } else if (itemId == R.id.nav_menu) {
                selectedFragment = new MenuFragment();
                topNavigation.setVisibility(View.GONE);
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        firestoreSyncManager = new FirestoreSyncManager(this);

        // Đồng bộ hóa dữ liệu từ Firestore vào SQLite
        firestoreSyncManager.syncFirestoreToSQLite();

    }

    private void loadFragment(Fragment fragment) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            // Fragment hiện tại đã giống fragment cần tải, không cần thay thế
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
