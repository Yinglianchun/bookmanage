package cn.kmbeast.mapper;

import cn.kmbeast.pojo.dto.query.extend.BookshelfQueryDto;
import cn.kmbeast.pojo.entity.Bookshelf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 书架持久化接口
 */
@Mapper
public interface BookshelfMapper {

    void save(Bookshelf bookshelf);

    void update(Bookshelf bookshelf);

    void batchDelete(@Param(value = "ids") List<Integer> ids);

    List<Bookshelf> query(BookshelfQueryDto bookshelfQueryDto);

    Integer queryCount(BookshelfQueryDto bookshelfQueryDto);

}
