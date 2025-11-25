package cn.kmbeast.pojo.dto.query.extend;

import cn.kmbeast.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BookOrderHistoryQueryDto extends QueryDto {
    private Integer id;
    private String userName;
    private Integer userId;
    private Integer bookId;
    private String bookName;
    private Boolean isReturn;
}
