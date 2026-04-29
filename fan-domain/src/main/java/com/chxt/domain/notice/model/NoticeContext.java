package com.chxt.domain.notice.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class NoticeContext {

    private ScreenEnum screenEnum;

    private Object data;

}
