package com.example.unifriends.friendFinder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unifriends.R;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    protected TextView firstName;
    protected TextView lastName;
    protected TextView major;
    protected TextView interests;
    protected TextView subjects;

    private List<Friend> list;

    public FriendAdapter(List<Friend> l) {
        this.list = l;
    }

    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context c = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(c);

        View friendView = inflater.inflate(R.layout.friend_list_layout, parent, false);

        ViewHolder v = new ViewHolder(friendView);

        return v;
    }

    @Override
    public void onBindViewHolder(FriendAdapter.ViewHolder viewHolder, int position) {
        Friend f = list.get(position);

        viewHolder.firstName.setText(f.getFirstName());
        viewHolder.lastName.setText(f.getLastName());

        String subjects = "Subject1";
        String interests = "Interest1";
        viewHolder.subjects.setText(subjects);
        viewHolder.interests.setText(interests);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView firstName;
        public TextView lastName;
        public TextView interests;
        public TextView subjects;

        public ViewHolder(View v){
            super(v);
            firstName = (TextView) v.findViewById(R.id.first_name);
            lastName = (TextView) v.findViewById(R.id.last_name);
            interests = (TextView) v.findViewById(R.id.interests);
            subjects = (TextView) v.findViewById(R.id.subjects);
        }
    }
}
