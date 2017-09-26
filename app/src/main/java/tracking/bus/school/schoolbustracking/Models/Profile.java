package tracking.bus.school.schoolbustracking.Models;

/**
 * Created by phmima on 9/25/2017.
 */

public class Profile {
    private String fullName;
    private String email;
    private String password;
    private String type;

    public Profile (){

    }

    public String getFullName(){
        return fullName;
    }

    public void setFullName(String firstName){
        this.fullName = firstName;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public String getType() {return type;}

    public void setType(String type) {this.type = type;}
    public void setPassword(String password){
        this.password = password;
    }
}
