package com.injent.miscalls.data.database.user;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user WHERE authed =:authed")
    User getCurrentUser(boolean authed);

    @Insert(onConflict = REPLACE)
    void insertUser(User user);

    @Insert(onConflict = REPLACE)
    void insertToken(Token token);

    @Insert(onConflict = REPLACE)
    void insertOrganization(Organization organization);

    @Query("SELECT * FROM user JOIN token ON token.id = user.token_id JOIN organization ON organization.id = user.org_id")
    UserWithTokenAndOrg getUserWithTokenAndOrg();
}
