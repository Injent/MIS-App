package com.injent.miscalls.data.database.registry;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Objectively {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "general_state")
    private String generalState;
    @ColumnInfo(name = "body_build")
    private String bodyBuild;
    @ColumnInfo(name = "skin")
    private String skin;
    @ColumnInfo(name = "node_gland")
    private String nodeAndGland;
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

    public String getNodeAndGland() {
        if (nodeAndGland == null)
            return "";
        return nodeAndGland;
    }

    public void setNodeAndGland(String nodeAndGland) {
        this.nodeAndGland = nodeAndGland;
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

    public boolean isSick() {
        return sick;
    }

    public void setSick(boolean sick) {
        this.sick = sick;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Objectively)) return false;

        Objectively that = (Objectively) o;

        if (getId() != that.getId()) return false;
        if (getRegistryId() != that.getRegistryId()) return false;
        if (getGeneralState() != null ? !getGeneralState().equals(that.getGeneralState()) : that.getGeneralState() != null)
            return false;
        if (getBodyBuild() != null ? !getBodyBuild().equals(that.getBodyBuild()) : that.getBodyBuild() != null)
            return false;
        if (getSkin() != null ? !getSkin().equals(that.getSkin()) : that.getSkin() != null)
            return false;
        if (getNodeAndGland() != null ? !getNodeAndGland().equals(that.getNodeAndGland()) : that.getNodeAndGland() != null)
            return false;
        if (getTemperature() != null ? !getTemperature().equals(that.getTemperature()) : that.getTemperature() != null)
            return false;
        if (getPharynx() != null ? !getPharynx().equals(that.getPharynx()) : that.getPharynx() != null)
            return false;
        if (getRespiratoryRate() != null ? !getRespiratoryRate().equals(that.getRespiratoryRate()) : that.getRespiratoryRate() != null)
            return false;
        if (getBreathing() != null ? !getBreathing().equals(that.getBreathing()) : that.getBreathing() != null)
            return false;
        if (getArterialPressure() != null ? !getArterialPressure().equals(that.getArterialPressure()) : that.getArterialPressure() != null)
            return false;
        if (getPulse() != null ? !getPulse().equals(that.getPulse()) : that.getPulse() != null)
            return false;
        if (getHeartTones() != null ? !getHeartTones().equals(that.getHeartTones()) : that.getHeartTones() != null)
            return false;
        if (getAbdomen() != null ? !getAbdomen().equals(that.getAbdomen()) : that.getAbdomen() != null)
            return false;
        return getLiver() != null ? getLiver().equals(that.getLiver()) : that.getLiver() == null;
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getGeneralState() != null ? getGeneralState().hashCode() : 0);
        result = 31 * result + (getBodyBuild() != null ? getBodyBuild().hashCode() : 0);
        result = 31 * result + (getSkin() != null ? getSkin().hashCode() : 0);
        result = 31 * result + (getNodeAndGland() != null ? getNodeAndGland().hashCode() : 0);
        result = 31 * result + (getTemperature() != null ? getTemperature().hashCode() : 0);
        result = 31 * result + (getPharynx() != null ? getPharynx().hashCode() : 0);
        result = 31 * result + (getRespiratoryRate() != null ? getRespiratoryRate().hashCode() : 0);
        result = 31 * result + (getBreathing() != null ? getBreathing().hashCode() : 0);
        result = 31 * result + (getArterialPressure() != null ? getArterialPressure().hashCode() : 0);
        result = 31 * result + (getPulse() != null ? getPulse().hashCode() : 0);
        result = 31 * result + (getHeartTones() != null ? getHeartTones().hashCode() : 0);
        result = 31 * result + (getAbdomen() != null ? getAbdomen().hashCode() : 0);
        result = 31 * result + (getLiver() != null ? getLiver().hashCode() : 0);
        result = 31 * result + getRegistryId();
        return result;
    }
}
