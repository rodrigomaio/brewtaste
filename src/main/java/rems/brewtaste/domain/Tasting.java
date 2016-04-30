package rems.brewtaste.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Tasting.
 */
@Entity
@Table(name = "tasting")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "tasting")
public class Tasting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "date")
    private ZonedDateTime date;

    @Size(max = 250)
    @Column(name = "appearance", length = 250)
    private String appearance;

    @Size(max = 250)
    @Column(name = "aroma", length = 250)
    private String aroma;

    @Size(max = 250)
    @Column(name = "flavor", length = 250)
    private String flavor;

    @Size(max = 250)
    @Column(name = "mouthfeel", length = 250)
    private String mouthfeel;

    @Size(max = 250)
    @Column(name = "finish", length = 250)
    private String finish;

    @Size(max = 1250)
    @Column(name = "general_impression", length = 1250)
    private String generalImpression;

    @ManyToOne
    private Beer beer;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getAppearance() {
        return appearance;
    }

    public void setAppearance(String appearance) {
        this.appearance = appearance;
    }

    public String getAroma() {
        return aroma;
    }

    public void setAroma(String aroma) {
        this.aroma = aroma;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public String getMouthfeel() {
        return mouthfeel;
    }

    public void setMouthfeel(String mouthfeel) {
        this.mouthfeel = mouthfeel;
    }

    public String getFinish() {
        return finish;
    }

    public void setFinish(String finish) {
        this.finish = finish;
    }

    public String getGeneralImpression() {
        return generalImpression;
    }

    public void setGeneralImpression(String generalImpression) {
        this.generalImpression = generalImpression;
    }

    public Beer getBeer() {
        return beer;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tasting tasting = (Tasting) o;
        if(tasting.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, tasting.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Tasting{" +
            "id=" + id +
            ", date='" + date + "'" +
            ", appearance='" + appearance + "'" +
            ", aroma='" + aroma + "'" +
            ", flavor='" + flavor + "'" +
            ", mouthfeel='" + mouthfeel + "'" +
            ", finish='" + finish + "'" +
            ", generalImpression='" + generalImpression + "'" +
            '}';
    }
}
