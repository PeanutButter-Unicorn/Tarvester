package com.graqr.threshr.model.redsky.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record EnvironmentalSegmentation(
        @JsonProperty("is_hazardous_material")
        Boolean isHazardousMaterial) {
}
