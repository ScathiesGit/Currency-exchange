package git.scathiesgit.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Currency {

    @EqualsAndHashCode.Include
    private int id;
    private String code;
    private String fullName;
    private String sign;
}
