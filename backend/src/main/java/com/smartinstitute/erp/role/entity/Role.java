package com.smartinstitute.erp.role.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import com.smartinstitute.erp.common.enums.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {

//    @Column(nullable = false, unique = true, length = 50)
//    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private RoleType name;

    @Column(length = 255)
    private String description;

}