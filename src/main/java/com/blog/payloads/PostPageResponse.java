package com.blog.payloads;

import java.util.List;

public record PostPageResponse(List<PostDTO> postDTOS,
                               Integer pageNumber,
                               Integer pageSize,
                               long totalElements,
                               int totalPages,
                               boolean islast) {}
