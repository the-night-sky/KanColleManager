package com.nmt.kancollemanager;

/**
 * ExpeditionData
 */
public class ExpeditionData {
    private int id;
    private String sea;
    private String level;
    private String name;
    private int time;
    private int shipLv;
    private int flagLv;
    private String explain;
    private String condition;
    private int exp;
    private int fuel;
    private int ammo;
    private int steel;
    private int bauxite;
    private String bonus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSea() {
        return sea;
    }

    public void setSea(String sea) {
        this.sea = sea;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public String getFormatTime() {
        int hour = (int) Math.floor(time / 3600);
        int minutes = (int) Math. floor((time % 3600) / 60);
        int second = (int) Math.floor((time % 60));
        String formatTime = String.format("%02d:%02d:%02d", hour, minutes, second);
        return formatTime;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getShipLv() {
        return shipLv;
    }

    public void setShipLv(int shipLv) {
        this.shipLv = shipLv;
    }

    public int getFlagLv() {
        return flagLv;
    }

    public void setFlagLv(int flagLv) {
        this.flagLv = flagLv;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public int getSteel() {
        return steel;
    }

    public void setSteel(int steel) {
        this.steel = steel;
    }

    public int getBauxite() {
        return bauxite;
    }

    public void setBauxite(int bauxite) {
        this.bauxite = bauxite;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }
}
