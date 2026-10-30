package com.company.aneon.f01.service.impl;

import com.company.aneon.f01.dto.response.CategoryResponse;
import com.company.aneon.f01.dto.response.ZoneResponse;
import com.company.aneon.f01.mapper.F01Mapper;
import com.company.aneon.f01.repository.KhuVucRepository;
import com.company.aneon.f01.repository.NganhHangRepository;
import com.company.aneon.f01.service.CatalogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final KhuVucRepository zoneRepository;
    private final NganhHangRepository categoryRepository;
    private final F01Mapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ZoneResponse> listZones() {
        return zoneRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(mapper::toZoneResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> listCategories() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(mapper::toCategoryResponse)
                .toList();
    }
}

