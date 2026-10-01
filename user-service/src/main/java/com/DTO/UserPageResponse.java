package com.DTO;

import lombok.Builder;
import lombok.Data;
import java.util.List;

import com.DTO.UserResponse;

@Data
@Builder
public class UserPageResponse {
    private List<UserResponse> content;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
