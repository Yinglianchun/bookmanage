package cn.kmbeast.pojo.vo;

import cn.kmbeast.pojo.entity.BookOrderHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookOrderHistoryVO extends BookOrderHistory {
    /*书籍名*/
    private String bookName;
    /*用户名*/
    private String userName;

}
