package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Food} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.FoodResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /foods?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class FoodCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter catory;

    private StringFilter description;

    private StringFilter calo;

    private StringFilter price;

    public FoodCriteria() {}

    public FoodCriteria(FoodCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.catory = other.catory == null ? null : other.catory.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.calo = other.calo == null ? null : other.calo.copy();
        this.price = other.price == null ? null : other.price.copy();
    }

    @Override
    public FoodCriteria copy() {
        return new FoodCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getCatory() {
        return catory;
    }

    public StringFilter catory() {
        if (catory == null) {
            catory = new StringFilter();
        }
        return catory;
    }

    public void setCatory(StringFilter catory) {
        this.catory = catory;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getCalo() {
        return calo;
    }

    public StringFilter calo() {
        if (calo == null) {
            calo = new StringFilter();
        }
        return calo;
    }

    public void setCalo(StringFilter calo) {
        this.calo = calo;
    }

    public StringFilter getPrice() {
        return price;
    }

    public StringFilter price() {
        if (price == null) {
            price = new StringFilter();
        }
        return price;
    }

    public void setPrice(StringFilter price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FoodCriteria that = (FoodCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(catory, that.catory) &&
            Objects.equals(description, that.description) &&
            Objects.equals(calo, that.calo) &&
            Objects.equals(price, that.price)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, catory, description, calo, price);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FoodCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (catory != null ? "catory=" + catory + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (calo != null ? "calo=" + calo + ", " : "") +
            (price != null ? "price=" + price + ", " : "") +
            "}";
    }
}
