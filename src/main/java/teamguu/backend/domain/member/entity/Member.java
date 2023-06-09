package teamguu.backend.domain.member.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import teamguu.backend.domain.EntityDateInfo;
import teamguu.backend.domain.team.entity.Team;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Member extends EntityDateInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    @OneToMany(mappedBy = "captain", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();

    private String username;
    private String password;
    private String name;
    private String phone;
    private String birth;
    private String profileImageUrl;
    @Enumerated(EnumType.STRING)
    private Authority authority;

    public String changeProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
        return this.profileImageUrl;
    }

    public void editMember(String name, String phone, String birth) {
        this.name = name;
        this.phone = phone;
        this.birth = birth;
    }
}