package com.smartinstitute.erp.institute.entity;

import com.smartinstitute.erp.common.entity.BaseEntity;
import com.smartinstitute.erp.common.enums.InstituteStatus;
import com.smartinstitute.erp.common.enums.InstituteType;
import com.smartinstitute.erp.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "institutes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "mobile")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Institute extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "owner_user_id",
            unique = true
    )
    private User owner;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String mobile;

    @Column(length = 20)
    private String landline;

    @Column(length = 300)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String pincode;

    @Column(length = 30)
    private String gstNumber;

    @Column(length = 150)
    private String website;

    @Column(length = 300)
    private String logoPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InstituteType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InstituteStatus status = InstituteStatus.ACTIVE;

    @Column(nullable = false)
    private Boolean active = true;
}