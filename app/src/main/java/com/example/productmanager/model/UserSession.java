package com.example.productmanager.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class UserSession {
    private static final String FILE_NAME = "savedUser";
    public static User currentUser;

    public static void remember(Context ctx) {
        SharedPreferences sh = ctx.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();

        editor.putString("username", currentUser.getUsername());
        editor.putString("pass", currentUser.getPass());
        editor.putString("realname", currentUser.getRealname());
        editor.putString("email", currentUser.getEmail());
        editor.putString("type", currentUser.getType().toString());

        editor.commit();
    }

    public static void recover(Context ctx) {
        SharedPreferences sh = ctx.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);

        String username = sh.getString("username", null);
        String pass = sh.getString("pass", null);
        String realname = sh.getString("realname", null);
        String email = sh.getString("email", null);
        String type = sh.getString("type", null);

        if (isSomethingEmpty(username, pass, realname, email, type)) {
            currentUser = null;
        }
        else {
            currentUser = new User(username, realname, email, pass, UserType.valueOf(type));
        }
    }

    public static void miss(Context ctx) {
        currentUser = null;

        SharedPreferences sh = ctx.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();

        editor.clear();
        editor.commit();
    }

    private static boolean isSomethingEmpty(String... data) {
        for (String chunk : data) {
            if (chunk == null) return true;
        }
        return false;
    }
}
