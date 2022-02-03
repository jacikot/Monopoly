package rs.ac.bg.etf.monopoly;

import android.os.Handler;
import android.os.Looper;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import rs.ac.bg.etf.monopoly.databinding.ResultHolderBinding;
import rs.ac.bg.etf.monopoly.db.Game;
import rs.ac.bg.etf.monopoly.db.Player;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultHolder>{
    private List<Player> players=new ArrayList<>();
    private List<Game> games=new ArrayList<>();

    interface Callback{
        void call(int game);
    }

    Callback callback;
    public void setCallback(Callback c){
        callback=c;
    }



    public void setPlayers(List<Player> players){
        this.players=players;

    }
    public void setGames(List<Game> g){
        games=g;
    }
    @NonNull
    @Override
    public ResultAdapter.ResultHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ResultHolderBinding amb = ResultHolderBinding.inflate(
                layoutInflater,
                parent,
                false);
        return new ResultHolder(amb);
    }

    public interface Evaluator{
        LiveData<Integer> valuate(Player p);
    }





    @Override
    public void onBindViewHolder(@NonNull ResultHolder holder, int position) {
        ResultHolderBinding amb=holder.amb;

        amb.date.setText(DateTimeUtil.getDateTime(games.get(position).getStart(),games.get(position).getDuration()));
        Handler h=new Handler(Looper.getMainLooper());

        List<Player> gamePlayers=players.stream().filter(p->{
            return p.getGame()==games.get(position).getIdGame();
        }).sorted((p1,p2)->{
            return p2.getEvaluation()-p1.getEvaluation();
        }).collect(Collectors.toList());

        StringBuilder builder=new StringBuilder();
        AtomicReference<Integer>indexx=new AtomicReference<>(1);
        int max= gamePlayers.get(0).getEvaluation();
        gamePlayers.forEach(p->{
            builder.append("\t").append(indexx.get()).append(". "+p.getName()).append("(");
            indexx.set(indexx.get()+1);
            if(p.getEvaluation()==-1){
                builder.append("bankrot");
            }
            else{
                builder.append(p.getEvaluation());
                if(p.getEvaluation()==max){
                    builder.append(" - pobednik");
                }
            }
            builder.append(")\n");
        });
        amb.players.setText(builder.toString());
        amb.content.setOnClickListener(e->{
            callback.call(games.get(position).getIdGame());
        });

    }



    @Override
    public int getItemCount() {
        return games.size();
    }

    public class ResultHolder extends RecyclerView.ViewHolder {

        public ResultHolderBinding amb;

        public ResultHolder(@NonNull ResultHolderBinding binding) {
            super(binding.getRoot());
            this.amb = binding;
        }


    }
}
