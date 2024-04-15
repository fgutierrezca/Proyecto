package hn.unah.bases.proyecto_playstore.DTOs;

public class ConexionDTO {
    
    private int sid;
    private int serial;
    private String username;
    private String osuser;
    private String machine;
    private String program;
    
    public int getSid() {
        return sid;
    }
    public void setSid(int sid) {
        this.sid = sid;
    }
    public int getSerial() {
        return serial;
    }
    public void setSerial(int serial) {
        this.serial = serial;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getOsuser() {
        return osuser;
    }
    public void setOsuser(String osuser) {
        this.osuser = osuser;
    }
    public String getMachine() {
        return machine;
    }
    public void setMachine(String machine) {
        this.machine = machine;
    }
    public String getProgram() {
        return program;
    }
    public void setProgram(String program) {
        this.program = program;
    }

}
