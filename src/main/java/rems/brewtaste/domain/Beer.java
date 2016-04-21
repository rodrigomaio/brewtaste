package rems.brewtaste.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Beer.
 */
@Entity
@Table(name = "beer")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "beer")
public class Beer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "rate_beer_id")
    private Long rateBeerId;

    @Min(value = 0)
    @Column(name = "abv")
    private Double abv;

    @Min(value = 0)
    @Column(name = "overall_rating")
    private Integer overallRating;

    @Min(value = 0)
    @Column(name = "style_rating")
    private Integer styleRating;

    @Column(name = "style")
    private String style;

    @Column(name = "country")
    private String country;

    @Column(name = "brewery")
    private String brewery;

    @OneToMany(mappedBy = "beer")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Tasting> tastings = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRateBeerId() {
        return rateBeerId;
    }

    public void setRateBeerId(Long rateBeerId) {
        this.rateBeerId = rateBeerId;
    }

    public Double getAbv() {
        return abv;
    }

    public void setAbv(Double abv) {
        this.abv = abv;
    }

    public Integer getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(Integer overallRating) {
        this.overallRating = overallRating;
    }

    public Integer getStyleRating() {
        return styleRating;
    }

    public void setStyleRating(Integer styleRating) {
        this.styleRating = styleRating;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBrewery() {
        return brewery;
    }

    public void setBrewery(String brewery) {
        this.brewery = brewery;
    }

    public Set<Tasting> getTastings() {
        return tastings;
    }

    public void setTastings(Set<Tasting> tastings) {
        this.tastings = tastings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Beer beer = (Beer) o;
        if(beer.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, beer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Beer{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", rateBeerId='" + rateBeerId + "'" +
            ", abv='" + abv + "'" +
            ", overallRating='" + overallRating + "'" +
            ", styleRating='" + styleRating + "'" +
            ", style='" + style + "'" +
            ", country='" + country + "'" +
            ", brewery='" + brewery + "'" +
            '}';
    }
}
