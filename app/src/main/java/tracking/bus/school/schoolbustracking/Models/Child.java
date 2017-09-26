package tracking.bus.school.schoolbustracking.Models;

/**
 * Created by phmima on 9/25/2017.
 */

public class Child {
    private String name;
    private String parent;
    private String driver;
    private String status;

    public Child(){

    }

    public String getStatus() {return status;}

    public void setStatus(String status){this.status =status;}

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getParent(){
        return parent;
    }

    public void setParent(String parent){
        this.parent = parent;
    }

    public String getDriver() { return driver; }

    public void setDriver(String driver) { this.driver = driver; }

}
