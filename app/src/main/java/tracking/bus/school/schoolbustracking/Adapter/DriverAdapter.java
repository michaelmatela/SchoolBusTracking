package tracking.bus.school.schoolbustracking.Adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import tracking.bus.school.schoolbustracking.Models.Child;
import tracking.bus.school.schoolbustracking.Models.Profile;
import tracking.bus.school.schoolbustracking.R;

public class DriverAdapter
        extends RecyclerView.Adapter<DriverAdapter.MyViewHolder> {
    private List<Profile> driverList;
    private Context context;
    private ArrayList<StorageReference> storageReferences;

    public void setStorageReferences(ArrayList<StorageReference> storageReferences){
        this.storageReferences = storageReferences;
    }
    public void setContext(Context context){
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvFullName;
        public TextView tvEmail;

        public TextView tvCapacity;
        public TextView tvCurrent;

        public TextView tvId;

        public MyViewHolder(View view){
            super(view);

            tvFullName = (TextView) view.findViewById(R.id.tvFullName);
            tvEmail = (TextView) view.findViewById(R.id.tvEmail);

            tvId = (TextView) view.findViewById(R.id.tvId);
            tvCapacity = (TextView) view.findViewById(R.id.tvCapacity);

            tvCurrent = (TextView) view.findViewById(R.id.tvCurrent);
        }
    }

    public DriverAdapter(List<Profile> driverList){
        this.driverList = driverList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        Profile d = driverList.get(position);
        holder.tvFullName.setText(d.getFullName());
        holder.tvEmail.setText(d.getEmail());
        holder.tvCapacity.setText(d.getCapacity());
        holder.tvCurrent.setText(d.getCurrent());
    }

    @Override
    public int getItemCount(){
        return driverList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_item, parent, false);
        return new MyViewHolder(v);
    }

}