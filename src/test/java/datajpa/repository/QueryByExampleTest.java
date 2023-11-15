package datajpa.repository;

import datajpa.entity.Member;
import datajpa.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QueryByExampleTest {

    @Autowired MemberRepository repository;
    @Autowired EntityManager em;

    @Test
    void queryByExample() throws Exception {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 10, teamA);
        Member m2 = new Member("m2", 20, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        // when
        //Probe: 엔티티 자체가 검색 조건이 된다.
        Member member = new Member("m1");
        Team team = new Team("teamA");  //내부적으로 team 조인
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age");

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = repository.findAll(example);

        // then
        assertThat(result.get(0).getUsername()).isEqualTo("m1");
        assertThat(result.size()).isEqualTo(1);

    }
}
