package back.ahwhew.service.resultService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClassifyTagService {
    @Autowired
    NaverSentimentService naverSentimentService;

    public String classifySentiment(String sentiment, double positiveRatio, double negativeRatio, double neutralRatio, String detailNegativeSentiment) {
        if (sentiment.equals("positive")) {
            if (positiveRatio >= 80) {
                return "verygood";
            } else {
                return "good";
            }
        } else if (sentiment.equals("neutral")) {
            return "neutral";
        } else {//부정감정일 경우 세부감정 가져오기
            if (!detailNegativeSentiment.equalsIgnoreCase("null")) {
                return detailNegativeSentiment;
            }

        }
        return null;
    }

}
