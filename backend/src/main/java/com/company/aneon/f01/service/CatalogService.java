package com.company.aneon.f01.service;

import com.company.aneon.f01.dto.response.CategoryResponse;
import com.company.aneon.f01.dto.response.ZoneResponse;
import java.util.List;

public interface CatalogService {
    List<ZoneResponse> listZones();

    List<CategoryResponse> listCategories();
}

