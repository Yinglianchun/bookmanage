package cn.kmbeast.controller;

import cn.kmbeast.aop.Pager;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.BookshelfQueryDto;
import cn.kmbeast.pojo.entity.Bookshelf;
import cn.kmbeast.service.BookshelfService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公告的 Controller
 */
@RestController
@RequestMapping(value = "/bookshelf")
public class BookshelfController {

    @Resource
    private BookshelfService bookshelfService;

    /**
     * 公告新增
     *
     * @param bookshelf 新增数据
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/save")
    public Result<Void> save(@RequestBody Bookshelf bookshelf) {
        return bookshelfService.save(bookshelf);
    }

    /**
     * 公告删除
     *
     * @param ids 要删除的公告ID列表
     * @return Result<Void> 通用响应体
     */
    @PostMapping(value = "/batchDelete")
    public Result<Void> batchDelete(@RequestBody List<Integer> ids) {
        return bookshelfService.batchDelete(ids);
    }

    /**
     * 公告修改
     *
     * @param bookshelf 参数
     * @return Result<Void> 响应
     */
    @PutMapping(value = "/update")
    public Result<Void> update(@RequestBody Bookshelf bookshelf) {
        return bookshelfService.update(bookshelf);
    }

    /**
     * 公告查询
     *
     * @param bookshelfQueryDto 查询参数
     * @return Result<List < Bookshelf>> 通用响应
     */
    @Pager
    @PostMapping(value = "/query")
    public Result<List<Bookshelf>> query(@RequestBody BookshelfQueryDto bookshelfQueryDto) {
        return bookshelfService.query(bookshelfQueryDto);
    }

}
