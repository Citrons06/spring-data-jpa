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
class MemberRepositoryTest {

    @Autowired
    MemberRepository repository;

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
}