package GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBObject {
    public String name;
    public String lastname;
    public String type;
    public String extra;
    int id;

    public DBObject(String myname, String mytype, int myid){
        name = myname;
        type = mytype;
        lastname = "";
        if(mytype.equals("player")){
            List<String> fullname= Arrays.asList(name.split("\\s"));
            name = fullname.get(0);
            lastname = String.join("\\s", fullname.subList(1, fullname.size()-1));
        }
        id = myid;
    }
    public DBObject(String myname, String mytype, int myid, String team){
        name = myname;
        type = mytype;
        lastname = "";
        if(mytype.equals("player")){
            List<String> fullname= Arrays.asList(name.split("\\s"));
            name = fullname.get(0);
            lastname = String.join("\\s", fullname.subList(1, fullname.size()));
        }
        extra = team;
        id = myid;
    }

    @Override
    public String toString() {
        if(type.equals("player")){
            return name + " " + lastname + ", " + extra;
        }
        return name;
    }

    public String getIDString(){
        return type + "ID = " + id;
    }

    public ArrayList<String> getNameString(){
        ArrayList<String> ret = new ArrayList<>();
        if(type.equals("player")){
            ret.add("firstname=" + name);
            ret.add("lastname=" + lastname);
        }
        else{
            ret.add("name=" + name);
        }
        return ret;
    }
}
