package com.muhil.zohokart.models.specification;

import java.util.List;

public class SpecificationGroup {
    public String name;
    public List<Specification> specifications;

    public SpecificationGroup() {

    }

    public SpecificationGroup(String name, List<Specification> specifications) {
        this.name = name;
        this.specifications = specifications;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Specification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<Specification> specifications) {
        this.specifications = specifications;
    }
}
