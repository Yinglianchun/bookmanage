package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.BookOrderHistoryQueryDto;
import cn.kmbeast.pojo.entity.BookOrderHistory;
import cn.kmbeast.pojo.vo.BookOrderHistoryVO;

import java.util.List;

/**
 * 公告业务逻辑接口
 */
public interface BookOrderHistoryService {

    Result<Void> save(BookOrderHistory bookOrderHistory);

    Result<Void> batchDelete(List<Integer> ids);

    Result<Void> update(BookOrderHistory bookOrderHistory);

    Result<List<BookOrderHistoryVO>> query(BookOrderHistoryQueryDto bookOrderHistoryQueryDto);

}
