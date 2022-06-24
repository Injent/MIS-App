package com.injent.miscalls.data.database.registry;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "objectively")
public class Objectively {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "general_state")
    private String generalState;
    @ColumnInfo(name = "body_build")
    private String bodyBuild;
    @ColumnInfo(name = "skin")
    private String skin;
    @ColumnInfo(name = "nodes")
    private String nodes;
    @ColumnInfo(name = "temp")
    private String temperature;
    @ColumnInfo(name = "pharynx")
    private String pharynx;
    @ColumnInfo(name = "resp_rate")
    private String respiratoryRate;
    @ColumnInfo(name = "breathing")
    private String breathing;
    @ColumnInfo(name = "arterial_pressure")
    private String arterialPressure;
    @ColumnInfo(name = "pulse")
    private String pulse;
    @ColumnInfo(name = "heart_tones")
    private String heartTones;
    @ColumnInfo(name = "abdomen")
    private String abdomen;
    @ColumnInfo(name = "liver")
    private String liver;
    @ColumnInfo(name = "reg_id")
    private int registryId;
    @ColumnInfo(name = "pensioner")
    private boolean pensioner;
    @ColumnInfo(name = "sick")
    private boolean sick;
    @ColumnInfo(name = "glands")
    private String glands;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGeneralState() {
        if (generalState == null)
            return "";
        return generalState;
    }

    public void setGeneralState(String generalState) {
        this.generalState = generalState;
    }

    public String getBodyBuild() {
        if (bodyBuild == null)
            return "";
        return bodyBuild;
    }

    public void setBodyBuild(String bodyBuild) {
        this.bodyBuild = bodyBuild;
    }

    public String getSkin() {
        if (skin == null)
            return "";
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public String getNodes() {
        if (nodes == null)
            return "";
        return nodes;
    }

    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getTemperature() {
        if (temperature == null)
            return "";
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getPharynx() {
        if (pharynx == null)
            return "";
        return pharynx;
    }

    public void setPharynx(String pharynx) {
        this.pharynx = pharynx;
    }

    public String getRespiratoryRate() {
        if (respiratoryRate == null)
            return "";
        return respiratoryRate;
    }

    public void setRespiratoryRate(String respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public String getBreathing() {
        if (breathing == null)
            return "";
        return breathing;
    }

    public void setBreathing(String breathing) {
        this.breathing = breathing;
    }

    public String getArterialPressure() {
        if (arterialPressure == null)
            return "";
        return arterialPressure;
    }

    public void setArterialPressure(String arterialPressure) {
        this.arterialPressure = arterialPressure;
    }

    public String getPulse() {
        if (pulse == null)
            return "";
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
    }

    public String getHeartTones() {
        return heartTones;
    }

    public void setHeartTones(String heartTones) {
        this.heartTones = heartTones;
    }

    public String getAbdomen() {
        if (abdomen == null)
            return "";
        return abdomen;
    }

    public String isWorking() {
        if (isPensioner()) {
            return "Неработающий";
        }
        return "Работающий";
    }

    public String isSick() {
        if (getSick()) {
            return "Назначен больничный";
        }
        return "";
    }

    public void setAbdomen(String abdomen) {
        this.abdomen = abdomen;
    }

    public String getLiver() {
        if (liver == null)
            return "";
        return liver;
    }

    public void setLiver(String liver) {
        this.liver = liver;
    }

    public int getRegistryId() {
        return registryId;
    }

    public void setRegistryId(int registryId) {
        this.registryId = registryId;
    }

    public boolean isPensioner() {
        return pensioner;
    }

    public void setPensioner(boolean pensioner) {
        this.pensioner = pensioner;
    }

    public boolean getSick() {
        return sick;
    }

    public void setSick(boolean sick) {
        this.sick = sick;
    }

    public String getGlands() {
        if (glands == null) {
            return "";
        }
        return glands;
    }

    public void setGlands(String glands) {
        this.glands = glands;
    }
}
