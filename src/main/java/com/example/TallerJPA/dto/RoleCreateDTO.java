package com.example.TallerJPA.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleCreateDTO {
    private String name;
    private String description;
}
