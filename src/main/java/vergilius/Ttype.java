package vergilius;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class Ttype {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer idtype;

    @Column(name="tname")
    private String name;

    private Integer id;

    @Column(name="size")
    private Integer sizeof;

    public enum Kind {
        STRUCT, ENUM, UNION, ARRAY, BASE, POINTER, FUNCTION;

        public String toString() {
            String lowercase = name().toLowerCase(java.util.Locale.US);
            return lowercase;
        }
    }
    @Column(name="kind")
    @Enumerated(EnumType.STRING)
    private Kind kind;

    @Column(name="const")
    private Boolean isConst;

    @Column(name="volatile")
    private Boolean isVolatile;

    //relationship with Os
    @ManyToOne
    @JoinColumn(name = "Operating_system_idopersys")
    private Os opersys;

    //relationship with Tdata
    @OneToMany(mappedBy="ttype", cascade = CascadeType.ALL)
    private Set<Tdata> data;

    public Integer getIdtype() {
        return idtype;
    }

    public void setIdtype(Integer idtype) {
        this.idtype = idtype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSizeof() {
        return sizeof;
    }

    public void setSizeof(Integer sizeof) {
        this.sizeof = sizeof;
    }

    public Kind getKind()
    {
       return kind;
    }

    public void setKind(Kind kind) {

        this.kind = kind;
    }

    public Os getOpersys() {
        return opersys;
    }

    public void setOpersys(Os opersys) {
        this.opersys = opersys;
    }

    public void setData(Set<Tdata> data) {
        this.data = data;
    }

    public Set<Tdata> getData() {
        return data;
    }

    public boolean isIsConst() {
        return isConst == null? false: true;
    }

    public void setIsConst(Boolean isConst) {
        this.isConst = isConst;
    }

    public boolean isIsVolatile() {
        return isVolatile == null? false: true;
    }

    public void setIsVolatile(boolean isVolatile) {
        this.isVolatile = isVolatile;
    }

    public static List<Ttype> filterResults(List<Ttype> list)
    {
        List<Ttype> filter = new ArrayList<>();
        for(int i = 0; i < list.size(); i++)
        {
            if(list.get(i).isIsVolatile() == false && list.get(i).isIsConst() == false)
            {
                filter.add(list.get(i));
            }
        }
        return filter;
    }

    public String toString()
    {
        return "" + getIdtype();
        /*return "" + getIdtype() + " " + getName() + " " + getId() + " " + getKind()+ " " + getSizeof() + " " +
                isIsConst() + " "+ isIsVolatile() + " " + getData();*/
    }
}


