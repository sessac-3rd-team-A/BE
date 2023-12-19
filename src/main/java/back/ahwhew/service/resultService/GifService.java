package back.ahwhew.service.resultService;

import back.ahwhew.dto.GifDTO;
import back.ahwhew.entity.GifEntity;
import back.ahwhew.repository.GifRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GifService {

    private final GifRepository gifRepository;

    @Autowired
    public GifService(GifRepository gifRepository) {
        this.gifRepository = gifRepository;
    }

    public List<GifDTO> getGifs(String classifyTag) {
        List<GifEntity> gifEntities = gifRepository.findByTag(classifyTag);

        // GifEntity를 GifDTO로 변환
        return gifEntities.stream()
                .map(gifEntity -> GifDTO.builder()
                        .id(gifEntity.getId())
                        .gifUrl(gifEntity.getGifUrl())
                        .tag(gifEntity.getTag())
                        .build())
                .collect(Collectors.toList());
    }
    }
