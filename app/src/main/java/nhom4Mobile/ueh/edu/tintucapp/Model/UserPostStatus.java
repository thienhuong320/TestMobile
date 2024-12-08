package nhom4Mobile.ueh.edu.tintucapp;

public class UserPostStatus {
    private String newId;      // ID mới
    private String email;      // Email người dùng
    private boolean favor;     // Trạng thái yêu thích
    private boolean save;      // Trạng thái lưu
    private String idPost;     // ID bài viết

    // Constructor không tham số
    public UserPostStatus() {}

    // Constructor đầy đủ tham số
    public UserPostStatus(String newId, String email, boolean favor, boolean save, String idPost) {
        this.newId = newId;
        this.email = email;
        this.favor = favor;
        this.save = save;
        this.idPost = idPost;
    }

    // Getter và Setter cho newId
    public String getNewId() {
        return newId;
    }

    public void setNewId(String newId) {
        this.newId = newId;
    }

    // Getter và Setter cho email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter và Setter cho favor
    public boolean isFavor() {
        return favor;
    }

    public void setFavor(boolean favor) {
        this.favor = favor;
    }

    // Getter và Setter cho save
    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    // Getter và Setter cho idPost
    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }
}

