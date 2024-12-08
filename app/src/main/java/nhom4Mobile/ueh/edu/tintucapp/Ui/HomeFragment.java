package nhom4Mobile.ueh.edu.tintucapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private List<Post> postList = new ArrayList<>();
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    private String currentCategory = "Mới nhất"; // Default category
    private String userEmail; // User email to pass to the adapter

    public HomeFragment() {
        // Required empty public constructor
    }

    // newInstance method to pass category data
    public static HomeFragment newInstance(String category) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("category", category); // Pass category
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(requireContext());
        db = FirebaseFirestore.getInstance();

        // Get user email from FirebaseAuth
        userEmail = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                : "default_email@example.com"; // Fallback email if not logged in

        // Check if the category is passed and set it
        if (getArguments() != null) {
            currentCategory = getArguments().getString("category", "Mới nhất");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize RecyclerView
        recyclerView = rootView.findViewById(R.id.reclyclerview);

        // Pass db and userEmail to adapter
        adapter = new NewsAdapter(requireContext(), postList, db, userEmail);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Set the item click listener for the adapter
        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Post newsItem) {
                Intent intent = new Intent(getActivity(), News_Detail_Activity.class);
                intent.putExtra("title", newsItem.getTitle());
                intent.putExtra("category", newsItem.getCategory());
                intent.putExtra("imageUrl", newsItem.getImageUrl());
                intent.putExtra("content", newsItem.getDetailContent());
                intent.putExtra("id", newsItem.getId()); // Pass the post ID
                getActivity().startActivity(intent);
            }
        });

        // Load data for the selected category
        loadPostsFromFirestore(currentCategory);

        return rootView;
    }

    /**
     * Load posts from Firestore based on the selected category.
     * @param category The category to filter posts by.
     */
    private void loadPostsFromFirestore(String category) {
        db.collection("posts")
                .whereEqualTo("status", true) // Only get active posts
                .whereEqualTo("category", category) // Filter by category
                .orderBy("title", Query.Direction.ASCENDING) // Optional: Sort by title
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            error.printStackTrace();
                            return;
                        }
                        if (snapshots != null) {
                            postList.clear();
                            for (QueryDocumentSnapshot document : snapshots) {
                                Post post = document.toObject(Post.class);
                                post.setId(document.getId()); // Get Firestore ID
                                postList.add(post);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    /**
     * Update the category and refresh the data.
     * @param category The new category selected by the user.
     */
    public void updateCategory(String category) {
        if (isAdded() && !currentCategory.equals(category)) {
            currentCategory = category; // Update category
            Log.d("HomeFragment", "Updated category: " + category);
            loadPostsFromFirestore(category); // Reload data
        }
    }
}
