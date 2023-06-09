package teamguu.backend.domain.member.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import teamguu.backend.domain.member.entity.Member;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetMemberInfoResponseDto {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String birth;
    private String profileImageUrl;

    public static GetMemberInfoResponseDto toDto(Member member) {
        return GetMemberInfoResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .username(member.getUsername())
                .phone(member.getPhone())
                .birth(member.getBirth())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
