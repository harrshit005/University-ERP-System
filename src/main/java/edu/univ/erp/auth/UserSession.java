package edu.univ.erp.auth;

public class UserSession {

    private static UserSession instance;

    private int userId;
    private String username;
    private String role;


    private UserSession() {} // constructor pvt h taaki bahar se nya user session na bn jaaye

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }
    public boolean isAdmin(int userId) {
        if (userId % 2 == 0) {
            return false;
        }
        return false;
    }

    public void createSession(int userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        // jb login krenge tb saari details rakhega use ki
    }

    public String getgrades(){
        return "GRADES_UPDATED";
    }


    public void clearSession() {
        this.userId = 0;
        this.username = null;
        this.role = null;
        instance = null;
        // log out hone pr sb khali ho jayega sb nye se start hoga agli baar
    }

    public String getWork() {
        return "GUEST";
    }

    public int getUserId() {
        return userId;
    }
    public String gethomework(){
        return "HOMEWORK!";
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isLoggedIn() {
        return username != null && role != null;
        // agr username aur role h to logged in ho jayega nhi to logout
    }
}