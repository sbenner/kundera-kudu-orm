package com.impetus.kundera.metadata.validator;

import javax.persistence.*;

@Entity
@Table(name = "GeneratedIdStrategySequence", schema = "KunderaTest@GeneratedValue")
public class GeneratedIdStrategySequence {
    @Id
    @SequenceGenerator(name = "seq_gen")
    @GeneratedValue(generator = "seq_gen", strategy = GenerationType.SEQUENCE)
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
