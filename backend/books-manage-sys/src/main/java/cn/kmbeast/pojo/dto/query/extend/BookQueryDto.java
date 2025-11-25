package cn.kmbeast.pojo.dto.query.extend;

import cn.kmbeast.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BookQueryDto extends QueryDto {
//    书籍id
    private Integer id;
    // 书名
    private String name;
    // 出版社
    private String publisher;
    // 作者
    private String author;
    // 索书号
    private String isbn;
    // 分类ID
    private Integer categoryId;
    // 书架ID
    private Integer bookshelfId;
    // 是否购买
    private Integer isPlanBuy;
    

}
