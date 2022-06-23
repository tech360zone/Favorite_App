package app.firebase.security.rules.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.firebase.security.rules.Model.Item;
import app.firebase.security.rules.R;
import app.firebase.security.rules.databinding.LayoutItemsBinding;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final FirebaseFirestore firestore;
    private LayoutInflater mInflater;
    private List<Item> itemList = new ArrayList<>();
    private RefreshList refreshList;
    private boolean checkFavLayout;

    public ItemAdapter(List<Item> itemList, boolean checkFavLayout) {
        this.itemList = itemList;
        this.checkFavLayout = checkFavLayout;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mInflater = LayoutInflater.from(parent.getContext());
        return new ItemViewHolder(
                mInflater.inflate(
                        R.layout.layout_items,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bindData(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void OnRefreshListListener(RefreshList refreshList) {
        this.refreshList = refreshList;
    }

    public interface RefreshList {
        void RefreshData();
    }

    protected class ItemViewHolder extends RecyclerView.ViewHolder {

        private final LayoutItemsBinding binding;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutItemsBinding.bind(itemView);
        }

        private void bindData(Item item) {
            binding.tvItemName.setText(item.getiName());

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy K:mm a", Locale.getDefault());
            Date createdDate = item.getCreated_at().toDate();
            String myDateTime = dateFormat.format(createdDate);
            binding.tvItemDate.setText(myDateTime);

            if (checkFavLayout) {
                binding.ivFavorite.setImageResource(R.drawable.ic_bookmark_active);
            } else {
                if (item.isCheckFavorite()) {
                    binding.ivFavorite.setImageResource(R.drawable.ic_bookmark_active);
                } else {
                    binding.ivFavorite.setImageResource(R.drawable.ic_bookmark_inactive);
                }
            }

            binding.ivFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkFavLayout) {
                        firestore.collection("favorite")
                                .document(item.getKey())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        firestore.collection("items")
                                                .document(item.getKey())
                                                .update("checkFavorite", false)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        if (refreshList != null) {
                                                            refreshList.RefreshData();
                                                        }
                                                        Toast.makeText(mInflater.getContext(), "Remove from Favorite.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                    } else {
                        if (item.isCheckFavorite()) {
                            firestore.collection("favorite")
                                    .document(item.getKey())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            firestore.collection("items")
                                                    .document(item.getKey())
                                                    .update("checkFavorite", false)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            if (refreshList != null) {
                                                                refreshList.RefreshData();
                                                            }
                                                            Toast.makeText(mInflater.getContext(), "Remove from Favorite.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    });
                        } else {
                            DocumentReference documentReference = firestore.collection("favorite").document(item.getKey());
                            Map<String, Object> favorite = new HashMap<>();
                            favorite.put("favoriteKey", item.getKey());
                            documentReference.set(favorite).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    firestore.collection("items")
                                            .document(item.getKey())
                                            .update("checkFavorite", true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    if (refreshList != null) {
                                                        refreshList.RefreshData();
                                                    }
                                                    Toast.makeText(mInflater.getContext(), "Favorite Done.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
