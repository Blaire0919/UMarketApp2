package bbc.umarket.umarketapp2.Database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    //Session names
    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";

    //user session variables
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_FNAME = "fname";
    public static final String KEY_LNAME = "lname";
    public static final String KEY_STUDID = "studid";
    public static final String KEY_CONTACTS = "contacts";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_BDAY = "bday";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASS = "password";
    public static final String KEY_IS_SELLER = "is_seller";

    //remember me variables
    private static final String IS_REMEMBERME = "IsRememberMe";
    public static final String KEY_SESSIONSTUDID = "studId";
    public static final String KEY_SESSIONPASS = "password";


    //constructor
    public SessionManager(Context _context, String sessionName) {
        context = _context;
        usersSession = context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = usersSession.edit();
    }

    public void createLoginSession(String fname, String lname, String studID, String contacts, String gender, String bday, String email, String password, String is_seller) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_FNAME, fname);
        editor.putString(KEY_LNAME, lname);
        editor.putString(KEY_STUDID, studID);
        editor.putString(KEY_CONTACTS, contacts);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_BDAY, bday);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASS, password);
        editor.putString(KEY_IS_SELLER, is_seller);
        editor.commit();
    }

    public HashMap<String, String> getUserDetailSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_FNAME, usersSession.getString(KEY_FNAME, null));
        userData.put(KEY_LNAME, usersSession.getString(KEY_LNAME, null));
        userData.put(KEY_STUDID, usersSession.getString(KEY_STUDID, null));
        userData.put(KEY_CONTACTS, usersSession.getString(KEY_CONTACTS, null));
        userData.put(KEY_GENDER, usersSession.getString(KEY_GENDER, null));
        userData.put(KEY_BDAY, usersSession.getString(KEY_BDAY, null));
        userData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));
        userData.put(KEY_PASS, usersSession.getString(KEY_PASS, null));
        userData.put(KEY_IS_SELLER, usersSession.getString(KEY_IS_SELLER, null));
        return userData;
    }

    public boolean checkLogin() {
        return usersSession.getBoolean(IS_LOGIN, false);
    }

    public void logoutUserfromSession() {
        editor.clear();
        editor.commit();
    }

    //remember me session functions
    public void createRememberMeSession(String studId, String password){
        editor.putBoolean(IS_REMEMBERME, true);
        editor.putString(KEY_SESSIONSTUDID, studId);
        editor.putString(KEY_SESSIONPASS, password);
        editor.commit();
    }

    public HashMap<String, String> getRememberMeDetailsFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_SESSIONSTUDID, usersSession.getString(KEY_SESSIONSTUDID, null));
        userData.put(KEY_SESSIONPASS, usersSession.getString(KEY_SESSIONPASS, null));
        return userData;
    }

    public boolean checkRememberMe() {
        return usersSession.getBoolean(IS_REMEMBERME, false);
    }

}
