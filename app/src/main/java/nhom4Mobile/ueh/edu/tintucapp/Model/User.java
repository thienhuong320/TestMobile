package nhom4Mobile.ueh.edu.tintucapp;

public class User {
    private String userId;      // ID người dùng
    private String username;    // Tên đăng nhập
    private String email;       // Email người dùng
    private String password;    // Mật khẩu
    private String phone;       // Số điện thoại
    private boolean isUser;     // Cờ đánh dấu người dùng thông thường
    private boolean isAdmin;    // Cờ đánh dấu quản trị viên

    // Constructor không tham số
    public User() {}

    // Constructor đầy đủ tham số
    public User(String userId, String username, String email, String password, String phone, boolean isUser, boolean isAdmin) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.isUser = isUser;
        this.isAdmin = isAdmin;
    }

    // Getter và Setter cho userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter và Setter cho username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter và Setter cho email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Getter và Setter cho password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter và Setter cho phone
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter và Setter cho isUser
    public boolean isUser() {
        return isUser;
    }

    public void setUser(boolean user) {
        isUser = user;
    }

    // Getter và Setter cho isAdmin
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}

