package teamguu.backend.domain.team.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import teamguu.backend.config.aws.AmazonS3Service;
import teamguu.backend.domain.member.entity.Member;
import teamguu.backend.domain.team.dto.CreateTeamRequestDto;
import teamguu.backend.domain.team.dto.EditTeamInfoRequestDto;
import teamguu.backend.domain.team.dto.SimpleTeamInfoResponseDto;
import teamguu.backend.domain.team.dto.TeamInfoResponseDto;
import teamguu.backend.domain.team.entity.Team;
import teamguu.backend.domain.team.repository.TeamRepository;
import teamguu.backend.exception.situation.AlreadyBasicImageException;
import teamguu.backend.exception.situation.TeamNameAlreadyExistsException;
import teamguu.backend.exception.situation.TeamNotFoundException;

import java.util.List;

import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final AmazonS3Service amazonS3Service;

    public void createTeam(CreateTeamRequestDto createTeamRequestDto, Member captain) {
        if (teamRepository.existsByName(createTeamRequestDto.getName())) {
            throw new TeamNameAlreadyExistsException(createTeamRequestDto.getName());
        }
        teamRepository.save(createTeamRequestDto.toEntity(captain));
    }

    public TeamInfoResponseDto getTeamInfo(Long teamId) {
        return TeamInfoResponseDto.from(teamRepository.findById(teamId).orElseThrow(TeamNotFoundException::new));
    }

    public List<SimpleTeamInfoResponseDto> getSimpleTeamInfos(Member captain) {
        return teamRepository.findTeamsByCaptain(captain)
                .stream()
                .map(SimpleTeamInfoResponseDto::from)
                .collect(toList());
    }

    @Transactional
    public void editTeamInfo(EditTeamInfoRequestDto editTeamInfoRequestDto, Long teamId) {
        Team foundTeam = getTeam(teamId);
        validateDuplicateByName(editTeamInfoRequestDto, foundTeam);
        foundTeam.editTeam(editTeamInfoRequestDto.getName(), editTeamInfoRequestDto.getHistory(),
                editTeamInfoRequestDto.getIntro(), editTeamInfoRequestDto.getPlayerInfo());
    }

    public void deleteTeam(Long teamId) {
        Team foundTeam = getTeam(teamId);
        deleteLogoImageIfExits(foundTeam);
        teamRepository.delete(foundTeam);
    }

    @Transactional
    public String changeLogoImageToNew(MultipartFile logoImage, Long teamId){
        Team foundTeam = getTeam(teamId);
        deleteLogoImageIfExits(foundTeam);
        return foundTeam.changeLogoImageUrl(amazonS3Service.uploadFile(logoImage));
    }

    @Transactional
    public void changeLogoImageToBasic(Long teamId) {
        Team foundTeam = getTeam(teamId);
        String deleteLogoImageUrl = foundTeam.getLogoImageUrl();
        if (deleteLogoImageUrl.equals("basic_team.png")) {
            throw new AlreadyBasicImageException();
        }

        foundTeam.changeLogoImageUrl("basic_team.png");
        amazonS3Service.deleteFile(deleteLogoImageUrl);
    }

    public Team getTeam(Long teamId) {
        return teamRepository.findById(teamId).orElseThrow(TeamNotFoundException::new);
    }

    private void validateDuplicateByName(EditTeamInfoRequestDto editTeamInfoRequestDto, Team findTeam) {
        if (!editTeamInfoRequestDto.getName().equals(findTeam.getName())) {
            if (teamRepository.existsByName(editTeamInfoRequestDto.getName())) {
                throw new TeamNameAlreadyExistsException(editTeamInfoRequestDto.getName());
            }
        }
    }

    private void deleteLogoImageIfExits(Team teamToCheck) {
        if (!teamToCheck.getLogoImageUrl().equals("basic_team.png")) {
            amazonS3Service.deleteFile(teamToCheck.getLogoImageUrl());
        }
    }
}
