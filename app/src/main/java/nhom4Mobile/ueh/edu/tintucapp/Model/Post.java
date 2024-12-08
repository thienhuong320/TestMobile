package nhom4Mobile.ueh.edu.tintucapp;

public class Post {
    private String id;
    private String title;
    private String detailContent;
    private String imageUrl;
    private String category;
    private boolean status;

    // Constructor không tham số (bắt buộc cho Firebase)
    public Post() {
    }

    // Constructor có id
    public Post(String id, String title, String detailContent, String imageUrl, String category, boolean status) {
        this.id = id;
        this.title = title;
        this.detailContent = detailContent;
        this.imageUrl = imageUrl;
        this.category = category;
        this.status = status;
    }

    // Constructor không có id, Firestore sẽ tạo ID tự động
    public Post(String title, String detailContent, String imageUrl, String category, boolean status) {
        this.title = title;
        this.detailContent = detailContent;
        this.imageUrl = imageUrl;
        this.category = category;
        this.status = status;
    }

    // Getter and Setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetailContent() {
        return detailContent;
    }

    public void setDetailContent(String detailContent) {
        this.detailContent = detailContent;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

