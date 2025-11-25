package cn.kmbeast.service.impl;

import cn.kmbeast.mapper.BookshelfMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.BookshelfQueryDto;
import cn.kmbeast.pojo.entity.Bookshelf;
import cn.kmbeast.service.BookshelfService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 书架业务逻辑实现
 */
@Service
public class BookshelfServiceImpl implements BookshelfService {

    @Resource
    private BookshelfMapper bookshelfMapper;

    /**
     * 书架新增
     *
     * @param bookshelf 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(Bookshelf bookshelf) {
        //bookshelf.setCreateTime(LocalDateTime.now());
        bookshelfMapper.save(bookshelf);
        return ApiResult.success();
    }

    /**
     * 书架删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        bookshelfMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 书架修改
     *
     * @param bookshelf 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(Bookshelf bookshelf) {
        System.out.println(bookshelf);
        bookshelfMapper.update(bookshelf);
        return ApiResult.success();
    }

    /**
     * 书架查询
     *
     * @param bookshelfQueryDto 查询参数
     * @return Result<List < Bookshelf>>
     */
    @Override
    public Result<List<Bookshelf>> query(BookshelfQueryDto bookshelfQueryDto) {
        List<Bookshelf> bookshelfList = bookshelfMapper.query(bookshelfQueryDto);
        Integer totalCount = bookshelfMapper.queryCount(bookshelfQueryDto);
        return PageResult.success(bookshelfList, totalCount);
    }


}
