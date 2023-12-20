package back.ahwhew.service;

import back.ahwhew.entity.ResultEntity;
import back.ahwhew.entity.UserEntity;
import back.ahwhew.repository.ResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DashboardService {
    @Autowired
    private ResultRepository resultRepository;
    public List<ResultEntity> dashboard(UserEntity userEntity){
        List<ResultEntity> resultList = resultRepository.findAllByUser(userEntity);
        return resultList;
    }
}
