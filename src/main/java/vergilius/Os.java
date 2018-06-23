package vergilius;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

@Entity
public class Os{
    @Id
    private Integer idopersys;

    private String osname;

    @OneToMany(mappedBy = "opersys", cascade = CascadeType.ALL)
    private Set<Ttype> ttypes;

    public int getIdopersys() {
        return idopersys;
    }

    public void setIdopersys(Integer idopersys) {
        this.idopersys = idopersys;
    }

    public String getOsname() {
        return osname;
    }

    public void setOsname(String osname) {
        this.osname = osname;
    }

    public Set<Ttype> getTypes() {
        return ttypes;
    }

    public void setTypes(Set<Ttype> ttypes) {
        this.ttypes = ttypes;
    }

    public String toString()
    {
        return "" + getIdopersys();

    }

    /* New fields*/
    private String family;

    private long timestamp;

    public String convertTimestamptoDate(long timestamp)
    {
        //convert seconds to milliseconds
        Date date = new Date(timestamp*1000L);
        // format of the date
        //SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd");
        return jdf.format(date);
    }

    public String getFamily() {

        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}