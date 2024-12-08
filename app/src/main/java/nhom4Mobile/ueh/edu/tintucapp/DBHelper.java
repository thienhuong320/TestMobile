package nhom4Mobile.ueh.edu.tintucapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "posts.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_POSTS = "posts";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DETAIL_CONTENT = "detailContent";
    private static final String COLUMN_IMAGE_URL = "imageUrl";
    private static final String COLUMN_STATUS = "status";

    // Câu lệnh tạo bảng
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_POSTS + " (" +
                    COLUMN_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_CATEGORY + " TEXT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DETAIL_CONTENT + " TEXT, " +
                    COLUMN_IMAGE_URL + " TEXT, " +
                    COLUMN_STATUS + " INTEGER" + ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
        onCreate(db);
    }

    // Thêm bài viết vào SQLite
    public void addPost(Post post) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, post.getId());
        values.put(COLUMN_CATEGORY, post.getCategory());
        values.put(COLUMN_TITLE, post.getTitle());
        values.put(COLUMN_DETAIL_CONTENT, post.getDetailContent());
        values.put(COLUMN_IMAGE_URL, post.getImageUrl());
        values.put(COLUMN_STATUS, post.isStatus() ? 1 : 0);

        db.insert(TABLE_POSTS, null, values);
        db.close();
    }

    // Lấy tất cả bài viết từ SQLite
    public List<Post> getAllPosts() {
        List<Post> postList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_POSTS, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = getCursorString(cursor, COLUMN_ID);
                    String category = getCursorString(cursor, COLUMN_CATEGORY);
                    String title = getCursorString(cursor, COLUMN_TITLE);
                    String detailContent = getCursorString(cursor, COLUMN_DETAIL_CONTENT);
                    String imageUrl = getCursorString(cursor, COLUMN_IMAGE_URL);
                    boolean status = cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)) == 1;

                    Post post = new Post(id, category, title, detailContent, imageUrl, status);
                    postList.add(post);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return postList;
    }

    // Kiểm tra bài viết đã tồn tại trong SQLite chưa
    public boolean isPostExists(String postId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_POSTS, null, COLUMN_ID + "=?", new String[]{postId}, null, null, null);

        boolean exists = false;
        if (cursor != null) {
            exists = cursor.getCount() > 0;
            cursor.close();
        }
        db.close();
        return exists;
    }

    // Cập nhật trạng thái bài viết
    public void updatePostStatus(String postId, boolean status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status ? 1 : 0);

        db.update(TABLE_POSTS, values, COLUMN_ID + " = ?", new String[]{postId});
        db.close();
    }

    // Xóa bài viết khỏi SQLite
    public void deletePost(String postId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_POSTS, COLUMN_ID + " = ?", new String[]{postId});
        db.close();
    }

    // Hàm hỗ trợ lấy giá trị từ cursor an toàn
    private String getCursorString(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getString(columnIndex);
        }
        return null;
    }
}