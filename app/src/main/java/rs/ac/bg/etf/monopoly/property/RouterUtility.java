package rs.ac.bg.etf.monopoly.property;

import androidx.navigation.NavController;

import rs.ac.bg.etf.monopoly.TableFragmentDirections;
import rs.ac.bg.etf.monopoly.db.Property;

public class RouterUtility {

    public static void routeBuy(NavController controller, Property property, int user){
        if(property.getType()==0){
            PropertyBuyFragmentDirections.Bought action=PropertyBuyFragmentDirections.bought();
            action.setIndex(property.getId());
            action.setUser(user);
            controller.navigate(action);
            return;
        }
        if(property.getType()==1 || property.getType()==2){
            PropertyBuyFragmentDirections.StationBought action=PropertyBuyFragmentDirections.stationBought(property.getId());
            action.setUser(user);
            controller.navigate(action);
            return;
        }

    }
    
    public static void route(NavController controller, Property property, int user){
        int index=property.getId();
        if(index%10==0){
            TableFragmentDirections.Corner action=TableFragmentDirections.corner(index);
            controller.navigate(action);
            return;
        }

        if(property.getType()==3||property.getType()==4){
            TableFragmentDirections.Open action=TableFragmentDirections.open(index);
            controller.navigate(action);
            return;
        }
        if(property.getHolder()!=-1&&property.getHolder()!=user&&property.getType()==2 || property.getType()==5){
            TableFragmentDirections.Taxes action=TableFragmentDirections.taxes(index);
            controller.navigate(action);
            return;
        }
        if(property.getHolder()== user && property.getType()==0){
            TableFragmentDirections.Details action=TableFragmentDirections.details();
            action.setIndex(index);
            action.setUser(user);
            controller.navigate(action);
            return;
        }
        if(property.getHolder()== user && (property.getType()==1 || property.getType()==2)){
            TableFragmentDirections.ToStation action=TableFragmentDirections.toStation(index);
            controller.navigate(action);
            return;
        }
        if(property.getHolder()==-1){
            TableFragmentDirections.Buy action=TableFragmentDirections.buy();
            action.setIndex(index);
            action.setUser(user);
            controller.navigate(action);
            return;
        }
        if(property.getHolder()!=-1 && property.getHolder()!=user){
            TableFragmentDirections.Pay action=TableFragmentDirections.pay(index);
            controller.navigate(action);
            return;
        }
    }
}
