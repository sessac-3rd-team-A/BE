package back.ahwhew.service.resultService;

import back.ahwhew.repository.ResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ResultService {
    @Autowired
    private ResultRepository resultRepository;

}
