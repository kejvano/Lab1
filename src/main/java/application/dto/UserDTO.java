package application.dto;

public class UserDTO {
    private int uid;
    private String name;
    private String permissionLevel;

    public UserDTO(int uid, String name, String permissionLevel) {
        this.uid = uid;
        this.name = name;
        this.permissionLevel = permissionLevel;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
}