package back.ahwhew.dto;


import lombok.Data;

import java.util.List;

@Data
public class TopMemesResponseDTO {
    private boolean isSuccess;
    private String message;
    private List<TopMemesDTO> ranking;

}
