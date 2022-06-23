package app.firebase.security.rules.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import app.firebase.security.rules.Adapter.ItemAdapter;
import app.firebase.security.rules.Model.Item;
import app.firebase.security.rules.databinding.ActivityFavoriteBinding;

public class FavoriteActivity extends AppCompatActivity {

    private ActivityFavoriteBinding binding;
    private FirebaseFirestore firestore;
    private final List<Item> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        initView();
    }

    private void initView() {
        binding.rvFavList.setHasFixedSize(true);
        binding.rvFavList.setLayoutManager(new LinearLayoutManager(this));
        loadFavList();
    }

    private void loadFavList() {
        firestore.collection("items")
                .whereEqualTo("checkFavorite", true)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        itemList.clear();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            Item item = snapshot.toObject(Item.class);
                            if (item != null) {
                                itemList.add(item);
                                ItemAdapter adapter = new ItemAdapter(itemList, true);
                                adapter.OnRefreshListListener(new ItemAdapter.RefreshList() {
                                    @Override
                                    public void RefreshData() {
                                        loadFavList();
                                    }
                                });
                                binding.rvFavList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}