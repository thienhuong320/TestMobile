package nhom4Mobile.ueh.edu.tintucapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.app.AlertDialog;
import android.widget.TextView;

public class Setting extends AppCompatActivity {

    private boolean isDarkMode; // Trạng thái hiện tại
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lấy SharedPreferences
        sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        isDarkMode = isDarkModeEnabled();

        // Áp dụng chế độ sáng/tối
        applyTheme(isDarkMode);

        setContentView(R.layout.activity_setting);

        // Tìm Switch và đặt trạng thái
        Switch themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setChecked(isDarkMode);

        // Thiết lập listener để thay đổi chế độ sáng/tối
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Lưu trạng thái mới vào SharedPreferences
            saveThemePreference(isChecked);

            // Thay đổi chế độ sáng/tối
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );


            // Cập nhật giao diện thủ công (nếu cần)
            recreate(); // Tải lại Activity để áp dụng giao diện mới
        });

        // Tìm Button và thêm sự kiện quay lại MainActivity
        Button button = findViewById(R.id.button2);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(Setting.this, MainActivity.class);
            startActivity(intent);
        });

        TextView appInfoText = findViewById(R.id.app_info);
        appInfoText.setOnClickListener(v -> showAppInfoDialog());

        // Xử lý sự kiện cho TextView "Phiên bản"
        TextView appVersionText = findViewById(R.id.app_version);
        appVersionText.setOnClickListener(v -> showVersionDialog());
    }

    private void applyTheme(boolean darkMode) {
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        int targetMode = darkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;

        if (currentMode != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode); // Thay đổi chế độ
        }
    }

    private boolean isDarkModeEnabled() {
        return sharedPreferences.getBoolean("dark_mode", false);
    }

    private void saveThemePreference(boolean isDarkMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("dark_mode", isDarkMode);
        editor.apply();
    }
    private void showAppInfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Giới thiệu về ứng dụng")
                .setMessage("Ứng dụng này cung cấp các tin tức cập nhật mới nhất. Chúng tôi cung cấp nhiều tính năng hấp dẫn như chế độ tối, thông báo, và các chức năng khác. Cảm ơn bạn đã sử dụng ứng dụng!")
                .setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    /**
     * Hiển thị dialog phiên bản ứng dụng.
     */
    private void showVersionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Phiên bản ứng dụng")
                .setMessage("Phiên bản: 1.0.0\nĐây là phiên bản đầu tiên của ứng dụng. Cảm ơn bạn đã sử dụng!")
                .setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Xử lý giao diện nếu cần
        if ((newConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            // Chế độ tối
        } else {
            // Chế độ sáng
        }
    }
}
