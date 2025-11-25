package cn.kmbeast.service;

import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.BookshelfQueryDto;
import cn.kmbeast.pojo.entity.Bookshelf;

import java.util.List;

/**
 * 书架 逻辑接口
 */
public interface BookshelfService {

    Result<Void> save(Bookshelf bookshelf);

    Result<Void> batchDelete(List<Integer> ids);

    Result<Void> update(Bookshelf bookshelf);

    Result<List<Bookshelf>> query(BookshelfQueryDto bookshelfQueryDto);

}
