package com.example.be12fin5verdosewmthisbe.menu_management.option.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.repository.CategoryRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.OptionValue;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.dto.OptionUpdateDto;
import com.example.be12fin5verdosewmthisbe.menu_management.option.repository.OptionRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.option.repository.OptionValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OptionService {

    private final OptionRepository optionRepository;
    private final OptionValueRepository optionValueRepository;
    private final CategoryRepository categoryRepository;

    public Option register(Option option) {
        return optionRepository.save(option);
    }

    public void registerOptionValues(List<OptionValue> optionValues) {
        optionValueRepository.saveAll(optionValues);
    }

    public List<OptionValue> findOptionValuesByOptionId(Long optionId) {
        return optionValueRepository.findByOptionId(optionId);
    }


    public void updateOptionWithValues(Long optionId, OptionUpdateDto.RequestDto updateDto) {
        Option existingOption = optionRepository.findById(optionId)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));

        if (updateDto.getName() != null) {
            existingOption.setName(updateDto.getName());
        }
        if (updateDto.getPrice() != null) {
            existingOption.setPrice(updateDto.getPrice());
        }
        if (updateDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateDto.getCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            existingOption.setCategory(category);
        }
        optionRepository.save(existingOption);

        if (updateDto.getInventoryQuantities() != null && !updateDto.getInventoryQuantities().isEmpty()) {
            // 기존 OptionValue들을 Map으로 관리 (inventoryId -> OptionValue)
            Map<Long, OptionValue> existingOptionValueMap = findOptionValuesByOptionId(optionId).stream()
                    .collect(Collectors.toMap(OptionValue::getInventoryId, ov -> ov));

            List<OptionValue> toSave = updateDto.getInventoryQuantities().stream()
                    .map(dto -> {
                        OptionValue existingValue = existingOptionValueMap.get(dto.getInventoryId());
                        if (existingValue != null) {
                            existingValue.setQuantity(dto.getQuantity());
                            return existingValue;
                        } else {
                            return OptionValue.builder()
                                    .option(existingOption)
                                    .inventoryId(dto.getInventoryId())
                                    .quantity(dto.getQuantity())
                                    .build();
                        }
                    })
                    .collect(Collectors.toList());

            optionValueRepository.saveAll(toSave);

            // 삭제된 OptionValue 처리 (요청에 없는 기존 inventoryId)
            List<Long> updatedInventoryIds = updateDto.getInventoryQuantities().stream()
                    .map(OptionUpdateDto.InventoryQuantityUpdateDto::getInventoryId)
                    .collect(Collectors.toList());

            List<Long> inventoryIdsToDelete = existingOptionValueMap.keySet().stream()
                    .filter(inventoryId -> !updatedInventoryIds.contains(inventoryId))
                    .collect(Collectors.toList());

            if (!inventoryIdsToDelete.isEmpty()) {
                optionValueRepository.deleteByOptionIdAndInventoryIdIn(optionId, inventoryIdsToDelete);
            }
        }
    }

    public void deleteOption(Long optionId) {
        Option existingOption = optionRepository.findById(optionId)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));
        optionRepository.delete(existingOption);
    }

    public Page<Option> findAllOptions(Pageable pageable) {
        return optionRepository.findAll(pageable);
    }
    public Page<Option> searchOptionsByName(String keyword, Pageable pageable) {
        return optionRepository.findByNameContaining(keyword, pageable);
    }
}