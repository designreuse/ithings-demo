package ch.ithings.demo.domain;

import java.io.Serializable;
import java.util.Date;
import org.springframework.data.annotation.Id;

/**
 *
 * @author tph
 */
public class BaseDocument implements Serializable {

    @Id
    private String id;
    
    private int version;

    private Date timeCreated;

    public BaseDocument() {
        this.timeCreated = new Date();
    }

    public String getId() {
        return id;
    }

    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseDocument that = (BaseDocument) o;

        if (!id.equals(that.id)) {
            return false;
        }

        return true;
    }

    public int getVersion() {
        return version;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }
}
