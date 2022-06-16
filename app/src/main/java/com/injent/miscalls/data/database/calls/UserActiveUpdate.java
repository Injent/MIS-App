package com.injent.miscalls.data.database.calls;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity
public class UserActiveUpdate {

    @ColumnInfo(name = "last_active")
    private boolean lastActive;

    public boolean isLastActive() {
        return lastActive;
    }

    public void setLastActive(boolean lastActive) {
        this.lastActive = lastActive;
    }
}
