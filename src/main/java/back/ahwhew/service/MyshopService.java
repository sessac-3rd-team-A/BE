package back.ahwhew.service;

import back.ahwhew.entity.DiaryEntity;
import back.ahwhew.entity.GifEntity;
import back.ahwhew.entity.ResultEntity;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.repository.DiaryRepository;
import back.ahwhew.repository.GifRepository;
import back.ahwhew.repository.ResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MyshopService {
    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    ResultRepository resultRepository;

    @Autowired
    GifRepository gifRepository;
    public DiaryEntity getLatestDiary(UserEntity userEntity) {
        return diaryRepository.findTopByUserIdOrderByDateDesc(userEntity.getId());
    }
    public ResultEntity getLatestResult(UserEntity userEntity) {
        return resultRepository.findTopByUserIdOrderByDateDesc(userEntity.getId());
    }

    public GifEntity getGifEntity(String recommendedGif) {
        return gifRepository.findByGifUrl(recommendedGif); // 추천 그림 저장 후 사용
    }
}
