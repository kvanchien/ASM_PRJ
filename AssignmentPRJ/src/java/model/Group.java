package model;

public class Group {

    private int gid;
    private String gname;

    public Group() {
    }

    public Group(int gid, String gname) {
        this.gid = gid;
        this.gname = gname;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    @Override
    public String toString() {
        return "Group{" + "gid=" + gid + ", gname=" + gname + '}';
    }

}
