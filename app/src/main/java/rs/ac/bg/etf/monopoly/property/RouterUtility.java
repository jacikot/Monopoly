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
            action.setUser(user);
            controller.navigate(action);

            return;
        }

        if(property.getType()==3||property.getType()==4){
            TableFragmentDirections.Open action=TableFragmentDirections.open(index);
            action.setUser(user);
            controller.navigate(action);
            return;
        }
        if(property.getHolder()!=-1&&property.getHolder()!=user&&property.getType()==2 || property.getType()==5){
            TableFragmentDirections.Taxes action=TableFragmentDirections.taxes(index);
            action.setUser(user);
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
            action.setUser(user);
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
            action.setUser(user);
            controller.navigate(action);
            return;
        }
    }

    public static void routeFromCards(NavController controller, Property property, int user){
        int index=property.getId();
        if(index%10==0){
            OpenCardFragmentDirections.Corner action=OpenCardFragmentDirections.corner(index);
            action.setUser(user);
            controller.navigate(action);

            return;
        }

        if(property.getType()==3||property.getType()==4){
            OpenCardFragmentDirections.Open action=OpenCardFragmentDirections.open(index);
            action.setUser(user);
            controller.navigate(action);
            return;
        }
        if(property.getHolder()!=-1&&property.getHolder()!=user&&property.getType()==2 || property.getType()==5){
            OpenCardFragmentDirections.Taxes action=OpenCardFragmentDirections.taxes(index);
            action.setUser(user);
            controller.navigate(action);
            return;
        }
        if(property.getHolder()== user && property.getType()==0){
            OpenCardFragmentDirections.Details action=OpenCardFragmentDirections.details();
            action.setIndex(index);
            action.setUser(user);
            controller.navigate(action);
            return;
        }
        if(property.getHolder()== user && (property.getType()==1 || property.getType()==2)){
            OpenCardFragmentDirections.ToStation action=OpenCardFragmentDirections.toStation(index);
            action.setUser(user);
            controller.navigate(action);
            return;
        }
        if(property.getHolder()==-1){
            OpenCardFragmentDirections.Buy action=OpenCardFragmentDirections.buy();
            action.setIndex(index);
            action.setUser(user);
            controller.navigate(action);
            return;
        }
        if(property.getHolder()!=-1 && property.getHolder()!=user){
            OpenCardFragmentDirections.Pay action=OpenCardFragmentDirections.pay(index);
            action.setUser(user);
            controller.navigate(action);
            return;
        }
    }
}
