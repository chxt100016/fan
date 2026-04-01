package com.chxt.client.huanglong.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookInfoRequest {
    private Long orderDateNum;
    private String _venue;
    private String _item;
    private String passBaseOn;
    private String showLine;
    private boolean showPassTime;
    private String _org;
}
