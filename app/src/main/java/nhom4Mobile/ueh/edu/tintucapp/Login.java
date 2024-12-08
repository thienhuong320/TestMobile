package nhom4Mobile.ueh.edu.tintucapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Login extends AppCompatActivity {
    EditText email, password;
    Button loginBtn, gotoRegister, googleLogin;
    boolean valid = true;
    private static final int RC_SIGN_IN = 9001;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        gotoRegister = findViewById(R.id.gotoRegister);
        googleLogin = findViewById(R.id.googleLogin);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(email);
                checkField(password);

                if (valid) {
                    fAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Login.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                            checkUserAccessLevel(authResult.getUser().getUid());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }

            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.api)) //web api
                .requestEmail()
                .build();

        Log.d("GoogleSignInDebug", "Request ID Token: " + gso.getServerClientId());


        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        loginBtn.setOnClickListener(view -> {
            checkField(email);
            checkField(password);

            if (valid) {
                fAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnSuccessListener(authResult -> {
                            Toast.makeText(Login.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                            checkUserAccessLevel(authResult.getUser().getUid());
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(Login.this, "Đăng nhập thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });


        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });


    }

    private void checkUserAccessLevel(String uid) {
        DocumentReference df = fStore.collection("Users").document(uid);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d("TAG", "onSuccess:" + documentSnapshot.getData());

                if (documentSnapshot.exists()) {
                    if (documentSnapshot.getString("isAdmin") != null) {
                        // Admin
                        startActivity(new Intent(getApplicationContext(), AdminPage.class));
                        finish();
                    } else if (documentSnapshot.getString("isUser") != null) {
                        // User
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                } else {
                    // Nếu không có dữ liệu, có thể là người dùng mới. Gọi hàm lưu người dùng vào Firestore
                    saveUserToFirestore(FirebaseAuth.getInstance().getCurrentUser());
                }
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) { // RC_SIGN_IN
            Log.d("GoogleLogin", "Request code matched RC_SIGN_IN");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task == null) {
                Log.e("GoogleSignIn", "GoogleSignIn task is null.");
                return;
            }

            if (task.isSuccessful()) {
                Log.d("GoogleSignIn", "Google Sign-In successful.");
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        Log.d("GoogleSignIn", "Signed in account: " + account.getEmail());
                        firebaseAuthWithGoogle(account.getIdToken());
                    }
                } catch (ApiException e) {
                    Log.e("GoogleSignIn", "Error getting account result: " + e.getStatusCode());
                }
            } else {
                // Log chi tiết lỗi nếu task không thành công
                Exception exception = task.getException();
                if (exception != null) {
                    Log.e("GoogleSignIn", "Google Sign-In failed. Exception: " + exception.getMessage(), exception);
                } else {
                    Log.e("GoogleSignIn", "Google Sign-In failed. Unknown error.");
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Log.d("Google ID Token", "ID Token: " + idToken); // In ID Token ra Log
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = fAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(Login.this, "Đăng nhập Google thành công.", Toast.LENGTH_SHORT).show();
                            saveUserToFirestore(user);
                            checkUserAccessLevel(user.getUid());
                        }
                    } else {
                        Toast.makeText(Login.this, "Đăng nhập Google thất bại: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(FirebaseUser user) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference userRef = fStore.collection("Users").document(user.getUid());

        // Kiểm tra xem người dùng đã tồn tại chưa
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()) {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("Tên", user.getDisplayName()); // Lấy tên người dùng từ Google
                    userData.put("Email Người Dùng", user.getEmail());
                    userData.put("isUser", "1"); // Thiết lập isUser = 1 cho người dùng mới

                    // Lưu thông tin vào Firestore
                    userRef.set(userData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("Firestore", "User added to Firestore");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Firestore", "Error adding user", e);
                                }
                            });
                }
            }
        });
    }

    public boolean checkField(EditText textField) {
        if (textField.getText().toString().isEmpty()) {
            textField.setError("Error");
            valid = false;
        } else {
            valid = true;
        }
        return valid;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}
