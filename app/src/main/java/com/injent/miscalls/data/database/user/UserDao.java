package com.injent.miscalls.data.database.user;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user WHERE authed =:authed LIMIT 1")
    User getCurrentUser(int authed);

    @Query("SELECT * FROM user WHERE login =:login AND password =:password LIMIT 1")
    User getByLoginAndPassword(String login, String password);

    @Query("SELECT * FROM token WHERE user_id =:id LIMIT 1")
    Token getTokenById(int id);

    @Query("SELECT * FROM organization WHERE id =:id LIMIT 1")
    Organization getOrganizationById(int id);

    @Insert(onConflict = REPLACE)
    long insertUser(User user);

    @Update
    void updateUser(User user);

    @Insert(onConflict = REPLACE)
    long insertToken(Token token);

    @Insert(onConflict = REPLACE)
    long insertOrganization(Organization organization);

    @Query("SELECT * FROM user JOIN token ON token.id = user.token_id JOIN organization ON organization.id = user.org_id")
    UserWithTokenAndOrg getUserWithTokenAndOrg();
}
