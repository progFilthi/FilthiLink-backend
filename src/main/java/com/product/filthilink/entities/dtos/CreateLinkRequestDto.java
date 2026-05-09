package com.product.filthilink.entities.dtos;

import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.annotation.JsonDeserialize;

@Setter
@Getter
@JsonDeserialize
public class CreateLinkRequestDto {
    private String originalUrl;

    public CreateLinkRequestDto() {}

}