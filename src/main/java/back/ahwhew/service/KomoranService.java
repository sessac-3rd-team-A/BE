package back.ahwhew.service;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KomoranService {

    public Map<String, String> jobCategoryKeywords = initializeJobCategoryKeywords();

    public Map<String, String> initializeJobCategoryKeywords() {
        // 직업 카테고리 키워드 초기화
        Map<String, String> keywords = new HashMap<>();
        keywords.put("IT", "프로그래머, 개발자, 소프트웨어 엔지니어, 시스템 엔지니어, 네트워크 엔지니어, 데이터 과학자, 보안 전문가, 클라우드 엔지니어, 머신러닝 엔지니어, 블록체인 개발자, 데이터베이스 관리자, 시스템 분석가, 네트워크 보안 전문가, IT 컨설턴트, IT 프로젝트 매니저");
        keywords.put("의료", "의사, 간호사, 의료인, 응급의료 기술자, 의무기록사, 의료 장비 엔지니어, 의료 연구원, 수의사, 치과의사, 한의사, 의료정보 기술자, 병원 관리자, 의료 마케터, 의료 행정직, 의료 기기 세일즈");
        keywords.put("디자인", "그래픽 디자이너, 웹 디자이너, UI/UX 디자이너, 산업 디자이너, 패션 디자이너, 인테리어 디자이너, 게임 디자이너, 일러스트레이터, 애니메이터, 브랜드 전문가, 3D 모델러, 포장 디자이너, 사진 작가, 영화 예술 감독");
        keywords.put("영업", "영업 사원, 영업 관리자, 기업영업 담당자, 외부영업 대표, 영업 기획자, 고객지원 담당자, 영업 분석가, 영업 트레이너, 영업 컨설턴트, 영업 전문가, 영업 리더, 영업 지원 직군, 영업 대행사");
        keywords.put("금융", "은행원, 금융 관리자, 재무 분석가, 회계사, 투자 은행가, 보험 에이전트, 금융 애널리스트, 부동산 중개인, 리스크 관리자, 세무 전문가, 신용 분석가, 자산 관리자, 투자 컨설턴트, 금융 솔루션 아키텍트");
        keywords.put("교육", "선생님, 학생, 교실 관리자, 교육 기획자, 학습 컨설턴트, 교육 연구원, 교육 커리큘럼 개발자, 학교 상담사, 학원 강사, 교육 테크놀로지 전문가, 교육 관련 작가, 교육 컨설터, 어린이 교육 전문가");
        keywords.put("학생", "학생, 대학생, 고등학생, 중학생, 초등학생, 대학 교수, 학부모, 학교생활, 학업, 교육, 학습, 교실, 수업, 교과과정, 학년, 시험, 과제, 학업 성취, 학문, 독서, 학교 동아리, 대외 활동, 대회 참가자, 졸업생");
        keywords.put("취준생", "구직, 취업 준비, 이력서 작성, 자기소개서, 면접 연습, 직무 인터뷰, 채용 공고, 입사 지원, 경력 개발, 자격증 취득, 취업 특강, 커리어 컨설팅, 인턴십, 신입사원, 취업 시장 조사, 취업 행사, 입사 지원서, 직무 기술서, 경력 발전, 신입 구직, 커리어 페어");
        keywords.put("연예인", "배우, 가수, 모델, 영화 감독, 프로듀서, 작가, 스포츠 스타, 예능인, 방송인, 디제이, 연예 기획사 대표, 매니저, 스타일리스트, 뷰티 크리에이터, 유튜버, 인플루언서");

        return keywords;
    }



    public List<String> extractJobRelatedWords(String text) {
        List<String> jobRelatedWords = new ArrayList<>();

        // Komoran을 사용하여 형태소 분석
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        List<Token> tokens = komoran.analyze(text).getTokenList();

        for (Token token : tokens) {
            if (isJobRelatedNoun(token.getMorph())) {
                jobRelatedWords.add(token.getMorph());
                String jobCategory = categorizeJobCategory(token.getMorph());
                if (jobCategory != null) {
                    jobRelatedWords.add(jobCategory);
                }
            }
        }

        return jobRelatedWords;
    }

    public List<String> extractJobCategories(String text) {
        List<String> jobCategories = new ArrayList<>();

        // Komoran을 사용하여 형태소 분석
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        List<Token> tokens = komoran.analyze(text).getTokenList();

        Map<String, Integer> categoryCounts = new HashMap<>();

        for (Token token : tokens) {
            String category = categorizeJobCategory(token.getMorph());
            if (category != null) {
                categoryCounts.merge(category, 1, Integer::sum);
            }
        }

        // Top 3 카테고리를 추출
        List<Map.Entry<String, Integer>> sortedCategories = new ArrayList<>(categoryCounts.entrySet());
        sortedCategories.sort(Collections.reverseOrder(Comparator.comparing(Map.Entry::getValue)));

        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedCategories) {
            jobCategories.add(entry.getKey());
            count++;
            if (count == 3) {
                break;  // Top 3만 추출
            }
        }

        return jobCategories;
    }

    public boolean isJobRelatedNoun(String noun) {
        // 직업과 관련된 키워드 목록
        List<String> jobKeywords = List.of("회사", "일", "직업");

        // 명사가 직업과 관련된 키워드를 포함하는지 확인
        return jobKeywords.stream().anyMatch(keyword -> noun.contains(keyword));
    }

    private String categorizeJobCategory(String word) {
        // 직종 카테고리 추출 로직
        for (Map.Entry<String, String> entry : jobCategoryKeywords.entrySet()) {
            String category = entry.getKey();
            String keywords = entry.getValue();

            if (keywords.contains(word)) {
                return category;
            }
        }

        return null; // 추출된 단어로부터 직종 카테고리를 결정할 수 없는 경우
    }
    public List<String> extractNounPhrases(List<String> sentences) {
        List<String> nounPhrases = new ArrayList<>();

        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);

        for (String sentence : sentences) {
            List<Token> tokens = komoran.analyze(sentence).getTokenList();
            StringBuilder nounPhraseBuilder = new StringBuilder();

            for (Token token : tokens) {
                String morph = token.getMorph().trim();

                // 명사 또는 형용사인 경우에만 추가
                if (token.getPos().equals("NNG") || token.getPos().equals("VA")) {
                    if (!morph.isEmpty() && !morph.equals("같")) {
                        nounPhraseBuilder.append(morph).append(" ");
                    }
                }

                // 현재 토큰이 부정어인지 확인하고, 부정어가 있다면 해당 구를 포함하여 추가
                if (isNegation(morph)) {
                    for (int i = tokens.indexOf(token) - 1; i >= 0; i--) {
                        Token phraseToken = tokens.get(i);
                        String phraseMorph = phraseToken.getMorph().trim();
                        if (!phraseMorph.isEmpty() && !isNegation(phraseMorph) &&
                                !phraseToken.getPos().equals("NNG") &&
                                !phraseToken.getPos().equals("VA")) {
                            nounPhraseBuilder.insert(0, phraseMorph + " "); // 띄어쓰기 추가
                            break;  // 부정어가 아니고 명사 또는 형용사가 아닌 경우 중지
                        }
                    }
                }
            }

            // 현재 문장에 대한 명사구를 추가
            String nounPhrase = nounPhraseBuilder.toString().trim();
            if (!nounPhrase.isEmpty()) {
                nounPhrases.add(nounPhrase);
            } else {
                // 만약 nounPhrase가 비어있으면, 원래 문장을 추가합니다.
                nounPhrases.add(sentence);
            }
        }

        return nounPhrases;
    }

    private boolean isNegation(String word) {
        // 부정어를 식별하는 로직 추가
        // 예를 들어, 미리 정의된 부정어 목록을 사용할 수 있습니다
        List<String> negations = List.of("안", "못", "아니");
        return negations.contains(word);
    }
}
