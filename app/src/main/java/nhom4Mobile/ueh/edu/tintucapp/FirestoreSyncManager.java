package nhom4Mobile.ueh.edu.tintucapp;

import android.content.Context;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class FirestoreSyncManager {

    private Context context;
    private DBHelper dbHelper;

    public FirestoreSyncManager(Context context) {
        this.context = context;
        this.dbHelper = new DBHelper(context);
    }

    // Đồng bộ dữ liệu từ Firestore vào SQLite
    public void syncFirestoreToSQLite() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy dữ liệu từ Firestore collection "posts"
        db.collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot) {
                                // Chuyển Firestore document thành Post object
                                Post post = document.toObject(Post.class);
                                // Kiểm tra xem bài viết có tồn tại trong SQLite không, nếu không thì thêm mới
                                if (!dbHelper.isPostExists(post.getId())) {
                                    dbHelper.addPost(post);
                                } else {
                                    dbHelper.updatePostStatus(post.getId(), post.isStatus());
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Xử lý lỗi khi lấy dữ liệu từ Firestore
                });
    }

    // Lấy tất cả bài viết từ SQLite (khi không có kết nối mạng)
    public List<Post> getPostsFromSQLite() {
        return dbHelper.getAllPosts();
    }

    // Lấy dữ liệu từ Firestore hoặc SQLite tùy theo kết nối mạng
    public void getPosts(boolean isConnectedToNetwork) {
        if (isConnectedToNetwork) {
            syncFirestoreToSQLite();  // Nếu có mạng thì đồng bộ từ Firestore
        }
    }
}
