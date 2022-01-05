package rs.ac.bg.etf.monopoly.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Property {
    @PrimaryKey
    int id;
    int type;
    int group;
    int property_price;
    int building_price;
    int rent_l0;
    int rent_l1;
    int rent_l2;
    int rent_l3;
    int rent_l4;
    int rent_l5;
    int holder=-1;
    int houses=-1;

    public int getHolder() {
        return holder;
    }

    public void setHolder(int holder) {
        this.holder = holder;
    }

    public int getHouses() {
        return houses;
    }

    public void setHouses(int houses) {
        this.houses = houses;
    }

    public int getRent_l5() {
        return rent_l5;
    }

    public void setRent_l5(int rent_l5) {
        this.rent_l5 = rent_l5;
    }

    public Property(int id, int type, int group, int property_price, int building_price,
                    int rent_l0, int rent_l1, int rent_l2, int rent_l3, int rent_l4, int rent_l5) {
        this.id = id;
        this.type = type;
        this.group = group;
        this.property_price = property_price;
        this.building_price = building_price;
        this.rent_l0 = rent_l0;
        this.rent_l1 = rent_l1;
        this.rent_l2 = rent_l2;
        this.rent_l3 = rent_l3;
        this.rent_l4 = rent_l4;
        this.rent_l5 = rent_l5;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getProperty_price() {
        return property_price;
    }

    public void setProperty_price(int property_price) {
        this.property_price = property_price;
    }

    public int getBuilding_price() {
        return building_price;
    }

    public void setBuilding_price(int building_price) {
        this.building_price = building_price;
    }

    public int getRent_l0() {
        return rent_l0;
    }

    public void setRent_l0(int rent_l0) {
        this.rent_l0 = rent_l0;
    }

    public int getRent_l1() {
        return rent_l1;
    }

    public void setRent_l1(int rent_l1) {
        this.rent_l1 = rent_l1;
    }

    public int getRent_l2() {
        return rent_l2;
    }

    public void setRent_l2(int rent_l2) {
        this.rent_l2 = rent_l2;
    }

    public int getRent_l3() {
        return rent_l3;
    }

    public void setRent_l3(int rent_l3) {
        this.rent_l3 = rent_l3;
    }

    public int getRent_l4() {
        return rent_l4;
    }

    public void setRent_l4(int rent_l4) {
        this.rent_l4 = rent_l4;
    }

    public enum COLOR{
        BROWN, GRAY, PINK, ORANGE, RED, YELLOW, GREEN, BLUE
    }

}
