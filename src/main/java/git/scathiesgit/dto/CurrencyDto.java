package git.scathiesgit.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CurrencyDto {

    private String name;
    private String code;
    private String sign;
}
