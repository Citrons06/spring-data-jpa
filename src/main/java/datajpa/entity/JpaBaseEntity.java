package datajpa.entity;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
public class JpaBaseEntity {

    @Column(updatable = false)  //업데이트X
    private LocalDateTime createdDate;  //등록일
    private LocalDateTime updatedDate;  //수정일

    //persist 전에 실행
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    //update 전에 실행
    @PreUpdate
    public void preUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
