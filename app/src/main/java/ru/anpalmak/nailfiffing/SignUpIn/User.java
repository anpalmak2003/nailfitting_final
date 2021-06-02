package ru.anpalmak.nailfiffing.SignUpIn;


import com.google.firebase.database.IgnoreExtraProperties;
/**Пользователь*/

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;

    public User(String name, String email) {
        this.username = name;
        this.email = email;


    }

}
