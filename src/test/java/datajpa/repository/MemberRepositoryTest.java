package datajpa.repository;

import datajpa.dto.MemberDto;
import datajpa.entity.Member;
import datajpa.entity.Team;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.swing.text.html.Option;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired MemberRepository repository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void testMember() {
        Member member = new Member("memberA");
        Member savedMember = repository.save(member);

        Member findMember = repository.findById(member.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        repository.save(member1);
        repository.save(member2);

        //단건 조회 검증
        Member findMember1 = repository.findById(member1.getId()).get();
        Member findMember2 = repository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = repository.findAll();
        assertThat(all).size().isEqualTo(2);

        //카운트 검증
        long count = repository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        repository.delete(member1);
        repository.delete(member2);

        long deletedCount = repository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThan() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberA", 20);
        repository.save(memberA);
        repository.save(memberB);

        List<Member> result = repository.findByUsernameAndAgeGreaterThan("memberA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("memberA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void findHelloBy() {
        List<Member> helloBy = repository.findHelloBy();
        List<Member> top3HelloBy = repository.findTop3HelloBy();
    }

    @Test
    void testNamedQuery() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        repository.save(memberA);
        repository.save(memberB);

        List<Member> result = repository.findByUsername("memberA");
        Member findMember = result.get(0);
        assertThat(findMember).isEqualTo(memberA);
    }

    @Test
    void testQuery() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        repository.save(memberA);
        repository.save(memberB);

        List<Member> result = repository.findUser("memberA", 10);
        assertThat(result.get(0)).isEqualTo(memberA);
    }

    @Test
    void findUsernameList() {
        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        repository.save(memberA);
        repository.save(memberB);

        List<String> usernameList = repository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s = " + s);
        }
    }

    @Test
    void findMemberDtoTest() {
        
        Team teamA = new Team("teamA");
        teamRepository.save(teamA);
        
        Member memberA = new Member("memberA", 10);
        memberA.setTeam(teamA);
        repository.save(memberA);

        List<MemberDto> memberDto = repository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }

    }

    @Test
    void findByNames() {

        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        repository.save(memberA);
        repository.save(memberB);

        List<Member> result = repository.findByNames(Arrays.asList("memberA", "memberB"));

        for (Member member : result) {
            System.out.println("member = " + member);
        }
    }

    @Test
    void returnType() {

        Member memberA = new Member("memberA", 10);
        Member memberB = new Member("memberB", 20);
        repository.save(memberA);
        repository.save(memberB);

        List<Member> result = repository.findByNames(Arrays.asList("memberA", "memberB"));

        Optional<Member> findMember = repository.findOptionalByUsername("memberA");
        System.out.println("findMember = " + findMember);
    }

    @Test
    void paging() throws Exception {
        // given
        repository.save(new Member("member1", 10));
        repository.save(new Member("member2", 10));
        repository.save(new Member("member3", 10));
        repository.save(new Member("member4", 10));
        repository.save(new Member("member5", 10));

        // when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = repository.findByAge(10, pageRequest);

        //Member -> MemberDto
        Page<MemberDto> dtoPage = page.map(m -> new MemberDto(m.getId(), m.getUsername(), m.getTeam().getName()));

        //then
        List<Member> content = page.getContent();   //page 안의 데이터 꺼냄(3개)

        assertThat(content.size()).isEqualTo(3);    //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5);   //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0);  //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2);  //전체 페이지 번호
        assertThat(page.isFirst()).isTrue();    //첫 번째 항목인가?
        assertThat(page.hasNext()).isTrue();    //다음 페이지가 있는가?
    }

    @Test
    void bulkUpdate() {
        //given
        repository.save(new Member("member1", 15));
        repository.save(new Member("member2", 19));
        repository.save(new Member("member3", 20));
        repository.save(new Member("member4", 21));
        repository.save(new Member("member5", 30));

        //when
        int resultCount = repository.bulkAgePlus(20);
        //em.clear();

        List<Member> result = repository.findByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void findMemberLazy() {
        //given
        //member1 -> teamA
        //member2 -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        repository.save(member1);
        repository.save(member2);

        em.flush();
        em.clear();

        //when
        List<Member> members = repository.findAll();

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("team.Class() = " + member.getTeam().getClass());
            System.out.println("team = " + member.getTeam().getName());
        }
    }

    @Test
    void queryHint() throws Exception {
        // given
        Member member = repository.save(new Member("member1", 10));
        repository.save(member);
        em.flush();
        em.clear();

        // when
        Member findMember = repository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    void lock() throws Exception {
        // given
        Member member = repository.save(new Member("member1", 10));
        repository.save(member);
        em.flush();
        em.clear();

        // when
        repository.findLockByUsername("member1");
    }
}