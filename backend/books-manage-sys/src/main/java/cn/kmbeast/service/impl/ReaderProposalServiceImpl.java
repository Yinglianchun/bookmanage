package cn.kmbeast.service.impl;

import cn.kmbeast.context.LocalThreadHolder;
import cn.kmbeast.mapper.ReaderProposalMapper;
import cn.kmbeast.pojo.api.ApiResult;
import cn.kmbeast.pojo.api.PageResult;
import cn.kmbeast.pojo.api.Result;
import cn.kmbeast.pojo.dto.query.extend.ReaderProposalQueryDto;
import cn.kmbeast.pojo.entity.ReaderProposal;
import cn.kmbeast.pojo.vo.ReaderProposalVO;
import cn.kmbeast.service.ReaderProposalService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告业务逻辑实现
 */
@Service
public class ReaderProposalServiceImpl implements ReaderProposalService {

    @Resource
    private ReaderProposalMapper readerProposalMapper;

    /**
     * 公告新增
     *
     * @param readerProposal 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> save(ReaderProposal readerProposal) {
        readerProposal.setUserId(LocalThreadHolder.getUserId());
        readerProposal.setCreateTime(LocalDateTime.now());
        readerProposalMapper.save(readerProposal);
        return ApiResult.success();
    }

    /**
     * 留言删除
     *
     * @param ids 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> batchDelete(List<Integer> ids) {
        readerProposalMapper.batchDelete(ids);
        return ApiResult.success();
    }

    /**
     * 公告修改
     *
     * @param readerProposal 参数
     * @return Result<Void>
     */
    @Override
    public Result<Void> update(ReaderProposal readerProposal) {
        readerProposal.setReplyTime(LocalDateTime.now());
        System.out.println(readerProposal);
        readerProposalMapper.update(readerProposal);
        return ApiResult.success();
    }

    /**
     * 公告查询
     *
     * @param readerProposalQueryDto 查询参数
     * @return Result<List < ReaderProposal>>
     */
    @Override
    public Result<List<ReaderProposalVO>> query(ReaderProposalQueryDto readerProposalQueryDto) {
        List<ReaderProposalVO> readerProposalList = readerProposalMapper.query(readerProposalQueryDto);
        Integer totalCount = readerProposalMapper.queryCount(readerProposalQueryDto);
        return PageResult.success(readerProposalList, totalCount);
    }


}
