package app.firebase.security.rules.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
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
import app.firebase.security.rules.R;
import app.firebase.security.rules.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseFirestore firestore;
    private final List<Item> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();

        initViews();
    }

    private void initViews() {
        binding.rvItemList.setHasFixedSize(true);
        binding.rvItemList.setLayoutManager(new LinearLayoutManager(this));
        loadOurItems();
    }

    private void loadOurItems() {
        firestore.collection("items")
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
                                ItemAdapter adapter = new ItemAdapter(itemList, false);
                                adapter.OnRefreshListListener(new ItemAdapter.RefreshList() {
                                    @Override
                                    public void RefreshData() {
                                        loadOurItems();
                                    }
                                });
                                binding.rvItemList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOurItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.favMenu) {
            startActivity(new Intent(MainActivity.this, FavoriteActivity.class));
        } else if (item.getItemId() == R.id.refreshMenu) {
            loadOurItems();
        }
        return super.onOptionsItemSelected(item);
    }
}