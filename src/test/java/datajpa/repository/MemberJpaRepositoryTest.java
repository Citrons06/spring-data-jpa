package datajpa.repository;

import datajpa.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberJpaRepositoryTest {

    @Autowired
    MemberJpaRepository repository;

    @Test
    void testMember() {
        Member member = new Member("memberA");
        Member savedMember = repository.save(member);

        Member findMember = repository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(savedMember.getId());
        assertThat(findMember.getUsername()).isEqualTo(savedMember.getUsername());

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
    void paging() throws Exception {
        // given
        repository.save(new Member("member1", 10));
        repository.save(new Member("member2", 10));
        repository.save(new Member("member3", 10));
        repository.save(new Member("member4", 10));
        repository.save(new Member("member5", 10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        // when
        List<Member> members = repository.findByPage(age, offset, limit);
        long totalCount = repository.totalCount(age);

        //totalPage = totalCount / size

        // then
        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
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

        //then
        assertThat(resultCount).isEqualTo(3);
    }
}