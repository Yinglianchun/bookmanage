package cn.kmbeast.pojo.vo;

import cn.kmbeast.pojo.entity.ReaderProposal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReaderProposalVO extends ReaderProposal {
    private String userName;

}
