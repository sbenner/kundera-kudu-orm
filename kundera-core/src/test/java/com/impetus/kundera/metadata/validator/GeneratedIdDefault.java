package com.impetus.kundera.metadata.validator;

import javax.persistence.*;

@Entity
@Table(name = "GeneratedIdDefault", schema = "KunderaTest@GeneratedValue")
public class GeneratedIdDefault {
    @Id
    @GeneratedValue
    private int id;

    @Column
    private String name;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
