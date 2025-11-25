package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.BookMapper;
import cn.kmbeast.mapper.BookOrderHistoryMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.BookOrderHistoryQueryDto;
import cn.kmbeast.pojo.dto.query.extend.BookQueryDto;
import cn.kmbeast.pojo.entity.BookOrderHistory;
import cn.kmbeast.pojo.vo.BookOrderHistoryVO;
import cn.kmbeast.pojo.vo.BookVO;
import cn.kmbeast.service.BookOrderHistoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告业务逻辑实现
 */
@Service
public class BookOrderHistoryServiceImpl implements BookOrderHistoryService {

    @Resource
    private BookOrderHistoryMapper bookOrderHistoryMapper;

    @Resource
    private BookMapper bookMapper;

    /**
     * 图书借阅新增
     *
     * @param bookOrderHistory 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(BookOrderHistory bookOrderHistory) {
        /*先看图书是否存在*/
        /*通过BookOrderHistoryQueryDto的用户id和书籍id去找到对应的用户借阅的图书*/
        BookOrderHistoryQueryDto dto=new BookOrderHistoryQueryDto();
        dto.setUserId(LocalThreadHolder.getUserId());
        dto.setBookId(bookOrderHistory.getBookId());
        /*找到对应的用户借阅的图书后，找到他所有的图书，然后返回借阅历史的List*/
        List<BookOrderHistoryVO> BookOrderHistoryVOS=bookOrderHistoryMapper.query(dto);
        /*增强for循环去找是否有未归还的图书*/
        for (BookOrderHistoryVO bookOrderHistoryVOS:BookOrderHistoryVOS){
            if(!bookOrderHistoryVOS.getIsReturn()){
                return ApiResult.error("书籍未归还");
            }
        }
        /*先找书籍是否存在*/
        BookQueryDto bookQueryDto=new BookQueryDto();
        /*通过bookOrderHistory的bookId在BookQueryDto去找到对应的图书*/
        bookQueryDto.setId(bookOrderHistory.getBookId());
        /*找到图书后通过bookMapper的query返回对应的BookVO实体*/
        List<BookVO> bookVOS=bookMapper.query(bookQueryDto);
        if(bookVOS.isEmpty()){
            return ApiResult.error("该图书不存在");
        }
        /*最后判断书籍的馆藏数量够不够借*/
        BookVO bookVO=bookVOS.get(0);
        if(bookVO.getNum()-bookOrderHistory.getDeadlineNum()<0){
            return ApiResult.error("馆藏数量不足");
        }
        /*找到对应的用户*/
        bookOrderHistory.setUserId(LocalThreadHolder.getUserId());
        bookOrderHistory.setIsReturn(false);
        bookOrderHistory.setCreateTime(LocalDateTime.now());
        bookOrderHistoryMapper.save(bookOrderHistory);
        return ApiResult.success();
    }
    /**
     * 公告删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        bookOrderHistoryMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 公告修改
     *
     * @param bookOrderHistory 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(BookOrderHistory bookOrderHistory) {
        System.out.println(bookOrderHistory);
        bookOrderHistoryMapper.update(bookOrderHistory);
        return ApiResult.success();
    }

    /**
     * 公告查询
     *
     * @param bookOrderHistoryQueryDto 查询参数
     * @return Result<List < BookOrderHistoryVO>>
     */
    @Override
    public Result<List<BookOrderHistoryVO>> query(BookOrderHistoryQueryDto bookOrderHistoryQueryDto) {
        List<BookOrderHistoryVO> bookOrderHistoryList = bookOrderHistoryMapper.query(bookOrderHistoryQueryDto);
        Integer totalCount = bookOrderHistoryMapper.queryCount(bookOrderHistoryQueryDto);
        return PageResult.success(bookOrderHistoryList, totalCount);
    }


}
