package project.hrpjt.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class SubEntity extends BaseEntity {
    @Column(updatable = false)
    private String createdBy;
    private String lastModifiedBy;
}
