package project.hrpjt.base;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class SubEntity extends BaseEntity {
    @Column(updatable = false)
    private String createdBy;
    private String lastModifiedBy;
}
