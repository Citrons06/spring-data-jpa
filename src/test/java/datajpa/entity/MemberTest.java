package datajpa.entity;

import datajpa.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository repository;

    @Test
    void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member memberA = new Member("memberA", 10, teamA);
        Member memberB = new Member("memberA", 20, teamA);
        Member memberC = new Member("memberB", 30, teamB);
        Member memberD = new Member("memberB", 40, teamB);

        em.persist(memberA);
        em.persist(memberB);
        em.persist(memberC);
        em.persist(memberD);

        em.flush(); //강제로 insert
        em.clear(); //영속성 컨텍스트 초기화

        //확인
        List<Member> members =
                em.createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("team = " + member.getTeam());
        }
    }

    @Test
    void JpaEventBaseEntity() throws InterruptedException {
        //given
        Member member = new Member("member1");
        repository.save(member);    //@PrePersist 발생

        Thread.sleep(100);
        member.setUsername("member2");

        em.flush(); //@PreUpdate 발생
        em.clear();

        //when
        Member findMember = repository.findById(member.getId()).get();

        //then
        System.out.println("CreatedDate = " + findMember.getCreatedDate());
        System.out.println("UpdatedDate = " + findMember.getLastModifiedDate());
        System.out.println("CreatedBy = " + findMember.getCreatedBy());
        System.out.println("LastModifiedBy = " + findMember.getLastModifiedBy());
    }
}