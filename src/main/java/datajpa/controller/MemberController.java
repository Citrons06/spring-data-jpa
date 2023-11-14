package datajpa.controller;

import datajpa.dto.MemberDto;
import datajpa.entity.Member;
import datajpa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository repository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable Long id) {
        Member member = repository.findById(id).get();
        return member.getUsername();
    }

    @GetMapping("/members2/{id}")
    public String findMember2(@PathVariable("id") Member member) {
        return member.getUsername();
    }

    @GetMapping("/members")
    public Page<MemberDto> list(@PageableDefault(size = 5) Pageable pageable) {
        return repository.findAll(pageable)
                .map(MemberDto::new);
    }

   // @PostConstruct
    public void init() {
        for (int i = 0; i < 100 ; i++) {
            repository.save(new Member("user" + i, i));
        }
    }
}
