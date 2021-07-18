package doys.framework.a2.structure;
public class EntityTableField {
    public String name;
    public String datatype = "varchar";
    private int length = 50;

    public EntityTableField(String name) {
        this.name = name;
    }
}
