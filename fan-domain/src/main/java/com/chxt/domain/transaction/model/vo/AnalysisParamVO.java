package com.chxt.domain.transaction.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AnalysisParamVO {

    private String userId;

    private String startTime;

    private String endTime;
}
