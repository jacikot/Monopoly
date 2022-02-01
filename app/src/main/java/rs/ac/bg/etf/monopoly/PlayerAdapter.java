package rs.ac.bg.etf.monopoly;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.monopoly.databinding.PlayerHolderBinding;
import rs.ac.bg.etf.monopoly.db.Player;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerHolder> {


    private List<Player> players=new ArrayList<>();


    public void setPlayers(List<Player> players){
        this.players=players;

    }
    @NonNull
    @Override
    public PlayerHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PlayerHolderBinding amb = PlayerHolderBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new PlayerHolder(amb);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerHolder holder, int position) {
        PlayerHolderBinding amb=holder.amb;
        amb.Name.setText(players.get(position).getName());
    }



    @Override
    public int getItemCount() {
        return players.size();
    }

    public class PlayerHolder extends RecyclerView.ViewHolder {

        public PlayerHolderBinding amb;

        public PlayerHolder(@NonNull PlayerHolderBinding binding) {
            super(binding.getRoot());
            this.amb = binding;
        }


    }
}
