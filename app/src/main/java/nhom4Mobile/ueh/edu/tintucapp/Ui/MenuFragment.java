package nhom4Mobile.ueh.edu.tintucapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class MenuFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Lấy thông tin từ FirebaseAuth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();


        // Khởi tạo các thành phần giao diện
        Button btnMenuTTCN = view.findViewById(R.id.btn_menuTTCN);
        Button btnMenuTGS = view.findViewById(R.id.btn_menuTGS);
        Button btnMenuDS = view.findViewById(R.id.btn_menuDS);
        Button btnMenuCD = view.findViewById(R.id.btn_menuCD);
        Button btnMenuTT = view.findViewById(R.id.btn_menuTT);


        if (user != null) {
            String email = user.getEmail(); // Lấy email người dùng
            String displayName = user.getDisplayName(); // Lấy tên hiển thị nếu có

            if (displayName != null && !displayName.isEmpty()) {
                // Hiển thị tên người dùng trên Button
                btnMenuTTCN.setText(displayName);
            } else {
                // Nếu không có tên hiển thị, dùng email thay thế
                btnMenuTTCN.setText(email != null ? email : "No Name");
            }
        } else {
            Log.d("Auth", "Người dùng chưa đăng nhập");
        }

        // Đặt sự kiện click cho nút "Đăng nhập/ Tạo tài khoản"
        btnMenuTTCN.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Thong_tin_ca_nhan_Activity.class);
            startActivity(intent);
        });

        // Đặt sự kiện click cho nút "Tin gắn sao"
        btnMenuTGS.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Tin_gan_sao_Activity.class);
            startActivity(intent);
        });

        // Đặt sự kiện click cho nút "Tin đọc sau"
        btnMenuDS.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Tin_doc_sau_Activity.class);
            startActivity(intent);
        });

        // Đặt sự kiện click cho nút "Thời tiết"
        btnMenuTT.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ThoitietActivity.class);
            startActivity(intent);
        });

        // Đặt sự kiện click cho nút "Cài đặt"
        btnMenuCD.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Setting.class);
            startActivity(intent);
        });
        // Đặt sự kiện click cho nút "Gửi ý kiến"



        return view;
    }
}